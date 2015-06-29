package org.brunocunha.taskerbox.impl.logwatch;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;

/**
 * Log Watch Channel
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class LogWatchChannel extends TaskerboxChannel<String> {

	//@URL
	@TaskerboxField("URL")
	@Getter @Setter
	private String[] url;

	@TaskerboxField("Results File")
	@Getter @Setter
	private File saveResultFile;
	
	@TaskerboxField("Control by Size")
	@Getter @Setter
	private boolean controlBySize = true;
	
	private long[] size;
	
	@Override
	public void setup() {
		size = new long[url.length];
	}
	
	@Override
	protected void execute() {

		try {
			url:
			for (int x = 0; x < url.length; x++) {
				String uniqueUrl = url[x];
				
				try {
					logDebug(log, "Checking [" + uniqueUrl + "]");
					
					if (controlBySize) {
						long uniqueSize = TaskerboxHttpBox.getInstance().getResponseSizeForURL(uniqueUrl);
						
						logDebug(log, "Size of response: " + uniqueUrl + " - " + uniqueSize);
						
						if (size[x] == uniqueSize) {
							logDebug(log, "Same size! Skipping URL " + uniqueUrl);
							continue url;
						}
						
						size[x] = uniqueSize;
					}
					
					String responseBody = TaskerboxHttpBox.getInstance().getStringBodyForURL(uniqueUrl);
					
					logDebug(log, "Got LogWatch Response: " + uniqueUrl + " - " + responseBody.length() + " bytes");
					
					perform(responseBody);
				} catch (Exception e) {
					logError(log, "Error watching log for " + getId() + " - " + uniqueUrl, e);
				}
			}
		
		} catch (Exception e) {
			logError(log, "Error watching log for " + getId(), e);
		}

	}

	@Override
	protected String getItemFingerprint(String entry) {
		return entry.replaceAll("\\s+", " ").trim();
	}



	
}
