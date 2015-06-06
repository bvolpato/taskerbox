package org.brunocunha.taskerbox.impl.tracking;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.alfredlibrary.utilitarios.correios.RegistroRastreamento;
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
public class CorreiosToasterAction extends
		DefaultTaskerboxAction<RegistroRastreamento> {

	@Getter @Setter
	private ActionListener actionListener;

	@Getter @Setter
	private String title = TaskerboxConstants.TITLE;

	@Override
	public void action(final RegistroRastreamento entry) {
		log.debug("Action on CorreiosToasterAction: " + entry);
		TaskerboxTrayUtils.displayMessage(title, CorreiosChannel
				.formatTracking(entry, getChannel().getProperty("tracking"),
						getChannel().getProperty("descricao")),
				MessageType.INFO, actionListener);
	}

}
