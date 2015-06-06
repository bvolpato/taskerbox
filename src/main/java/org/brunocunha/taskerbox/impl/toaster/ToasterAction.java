package org.brunocunha.taskerbox.impl.toaster;

import java.awt.TrayIcon.MessageType;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.TaskerboxConstants;
import org.brunocunha.taskerbox.core.utils.TaskerboxTrayUtils;

/**
 * Action that shows file name in a Toaster Popup
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class ToasterAction<T> extends DefaultTaskerboxAction<T> {

	@Override
	public void action(T object) {
		TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE,
				object.toString(), MessageType.INFO, null);
	}

}
