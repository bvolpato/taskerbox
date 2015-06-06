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
public class PastieAction extends CrawlerAction {

	private static final String DOWNLOAD_URL = "http://pastie.org/pastes/{id}/download";

	private static long FETCH_INTERVAL = 2000L;
	
	@Override
	public void action(final Document entry) {

		log.debug("Validating " + entry.title());
		
		for (Element el : entry.select(".pastePreview")) {
			final String id = el.select("a").attr("href").replace("http://pastie.org/pastes/", "");
			String code = el.select("pre").text().replaceAll("\r?\n", " ");
			if (code.length() > 32) {
				code = code.substring(0, 32);
			}
			
			final String title = id + " - " + code;

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
			log.debug("Getting " + DOWNLOAD_URL.replace("{id}", id));
			
			HttpResponse response = TaskerboxHttpBox.getInstance().getResponseForURL(DOWNLOAD_URL.replace("{id}", id));
			
			String content = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
			
			if (isConsiderable(id, content)) {
				if (isValid(id, content)) {
					logInfo(log, "[+] Bound: [" + postTitle + "]");
					doValid("pastie_"+id, content);
				} else {
					log.debug("[-] Not Bound: [" + postTitle + "]");
					doInvalid("pastie_"+id, content);
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
