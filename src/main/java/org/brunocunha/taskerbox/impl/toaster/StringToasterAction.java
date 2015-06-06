package org.brunocunha.taskerbox.impl.toaster;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.TaskerboxConstants;
import org.brunocunha.taskerbox.core.utils.TaskerboxTrayUtils;

/**
 * Action that shows Strings in a Toaster Popup
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class StringToasterAction extends DefaultTaskerboxAction<String> {

	@Getter @Setter
	private ActionListener actionListener;
	
	@Getter @Setter
	private String title = TaskerboxConstants.TITLE;
	
	@Override
	public void action(final String entry) {
		log.debug("Action on StringToasterAction: " + title + " / " + entry);
		TaskerboxTrayUtils.displayMessage(title, entry, MessageType.INFO, actionListener);
	}

}
