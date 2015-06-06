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
public class PastebinAction extends CrawlerAction {

	private static final String URL_START = "http://pastebin.com/raw.php?i=";

	private static long FETCH_INTERVAL = 3000L;
	
	@Override
	public void action(final Document entry) {

		log.debug("Validating " + entry.title());
		
		for (Element el : entry.select(".maintable").select("a")) {
			final String id = el.attr("href").substring(1);
			if (id.startsWith("archive")) {
				continue;
			}
			
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
			log.debug("Getting " + URL_START + id);
			
			HttpResponse response = TaskerboxHttpBox.getInstance().getResponseForURL(URL_START + id);
			
			String content = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
			
			if (isConsiderable(id, content)) {
				if (isValid(id, content)) {
					logInfo(log, "[+] Bound: [" + postTitle + "]");
					doValid("pastebin_"+id, content);
				} else {
					log.debug("[-] Not Bound: [" + postTitle + "]");
					doInvalid("pastebin_"+id, content);
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
