package org.brunocunha.taskerbox.impl.crawler;

import java.io.IOException;
import java.net.URISyntaxException;

import lombok.extern.log4j.Log4j;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Log4j
public class SniptAction extends CrawlerAction {

	private static final String URL_DOWNLOAD = "http://snipt.org/{id}/download";

	private static long FETCH_INTERVAL = 1000L;
	
	@Override
	public void action(final Document entry) {

		log.debug("Validating " + entry.title());
		
		for (Element el : entry.select(".grid-block").select("a")) {
			final String id = el.attr("href").replace("http://snipt.org/", "");
			
			final String title = id + " - " + el.text();

			if (canAct(id)) {
				addAct(id);
				
				spreadAction(id, title);
				serializeAlreadyAct();
				sleep(FETCH_INTERVAL);
			}

		}

	}
	
	public void spreadAction(final String id, String postTitle) {
		
		try {
			log.debug("Getting " + URL_DOWNLOAD.replace("{id}", id));
			
			HttpResponse response = TaskerboxHttpBox.getInstance().getResponseForURL(URL_DOWNLOAD.replace("{id}", id));
			
			String content = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
			
			if (isConsiderable(id, content)) {
				if (isValid(id, content)) {
					logInfo(log, "[+] Bound: [" + postTitle + "]");
					doValid("snipt_"+id, content);
				} else {
					log.debug("[-] Not Bound: [" + postTitle + "]");
					doInvalid("snipt_"+id, content);
				}
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
}
