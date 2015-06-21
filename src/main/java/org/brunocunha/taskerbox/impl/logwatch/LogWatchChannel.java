package org.brunocunha.taskerbox.impl.logwatch;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.hibernate.validator.constraints.URL;

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

	@TaskerboxField("Arquivo Resultados")
	@Getter @Setter
	private File saveResultFile;
	
	@Override
	protected void execute() {

		try {
			for (String uniqueUrl : url) {
				logDebug(log, "Checking [" + uniqueUrl + "]");
				String responseBody = TaskerboxHttpBox.getInstance().getStringBodyForURL(uniqueUrl);
				logDebug(log, "Got LogWatch Response: " + responseBody.length() + " bytes");
				perform(responseBody);
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
