package org.brunocunha.taskerbox.impl.feed;

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

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Action that shows Feeds in a Toaster Popup
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class FeedToasterAction extends DefaultTaskerboxAction<SyndEntry> {

	@Override
	public void action(final SyndEntry entry) {
		TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE, entry.getTitle(), MessageType.INFO,
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						logInfo(log, "Action -> Open: " + entry.getUri());

						if (Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().browse(new URI(entry.getUri()));
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
	}

}
