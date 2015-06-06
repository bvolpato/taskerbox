package org.brunocunha.taskerbox.impl.http;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.hibernate.validator.constraints.URL;

import com.sun.syndication.io.FeedException;

/**
 * HTTP Uptime Monitor Channel
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class HTTPUptimeChannel extends TaskerboxChannel<String> {

	@URL
	@Getter @Setter
	private String url;

	@Getter @Setter
	private boolean contains;

	@Getter @Setter
	private String filter;

	@Getter @Setter
	private boolean unique;
	
	@Override
	protected void execute() throws IOException, IllegalArgumentException, FeedException {
		log.debug("Checking #"+checkCount+"... [" + url + " / '" + filter + "']");

		try {
			HttpResponse response = TaskerboxHttpBox.getInstance().getResponseForURLNewClient(url);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				perform("Error fetching " + url + " - " + response.getStatusLine().toString());
				logWarn(log, "Error while fetching " + url + " - " + response.getStatusLine());
				return;
			}
			
			String responseBody = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
			log.debug("Got Response: " + responseBody);
			
			if ((contains && responseBody.toLowerCase().contains(filter.toLowerCase()))
					||  (!contains && !responseBody.toLowerCase().contains(filter.toLowerCase()))) {
				perform(responseBody);
			}
		} catch(Exception e) {
			perform("Error fetching " + url + " - " + e.getMessage());
			e.printStackTrace();
		}

	}


	@Override
	protected String getItemFingerprint(String entry) {
		return entry;
	}

	@Override
	public String toString() {
		return "HTTPHTMLChannel [url=" + url + ", filter=" + filter + ", unique="
				+ unique + ", every=" + getEvery() + "]";
	}

	
}
