package org.brunocunha.taskerbox.impl.twitter;

import java.awt.Desktop;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.TaskerboxConstants;
import org.brunocunha.taskerbox.core.utils.TaskerboxTrayUtils;

import twitter4j.Status;

/**
 * Action that shows Tweets in a Toaster Popup
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class TwitterToasterAction extends DefaultTaskerboxAction<StatusWrapper> {

	private static long toasterTime = 3000L;
	
	@Override
	public void action(final StatusWrapper entry) {
		TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE, "@" + entry.getValue().getUser().getScreenName() + ": " + entry.getValue().getText(), MessageType.INFO, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://twitter.com/" + entry.getValue().getUser().getScreenName() + "/status/" + entry.getValue().getId()));
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		try {
			Thread.sleep(toasterTime);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
