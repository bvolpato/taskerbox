package org.brunocunha.taskerbox.impl.crawler;

import java.io.IOException;

import lombok.extern.log4j.Log4j;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Log4j
public class SlexyAction extends CrawlerAction {

	private static final String URL_DOWNLOAD = "http://slexy.org/raw/{id}";

	private static long FETCH_INTERVAL = 1000L;
	
	@Override
	public void action(final Document entry) {

		log.debug("Validating " + entry.title());
		
		for (Element el : entry.select(".main").select("a")) {
			final String id = el.attr("href").replace("/view/", "");
			
			final String title = id;

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
			
			HttpGet get = new HttpGet(URL_DOWNLOAD.replace("{id}", id));
			get.addHeader("Referer", "http://slexy.org/recent");
			
			HttpResponse response = TaskerboxHttpBox.getInstance().getHttpClient().execute(get);
			
			String content = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
			
			if (isConsiderable(id, content)) {
				if (isValid(id, content)) {
					logInfo(log, "[+] Bound: [" + postTitle + "]");
					doValid("slexy_"+id, content);
				} else {
					logInfo(log, "[-] Not Bound: [" + postTitle + "]");
					doInvalid("slexy_"+id, content);
				}
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
}
