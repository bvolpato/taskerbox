package org.brunocunha.taskerbox.impl.sockettester;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.sockettester.vo.SocketTesterVO;
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
public class SocketTesterAction extends DefaultTaskerboxAction<SocketTesterVO> {

	@Getter @Setter
	private ActionListener actionListener;
	
	@Getter @Setter
	private String title = TaskerboxConstants.TITLE;
	
	@Override
	public void action(final SocketTesterVO entry) {
		log.debug("Action on SocketTesterAction: " + entry);
		
		StringBuffer sb = new StringBuffer();
		sb.append("Problemas com o ambiente");
		
		if (entry.getName() != null) {
			sb.append(" - ").append(entry.getName());
		}
		if (entry.getHost() != null) {
			sb.append(" - ").append(entry.getHost()).append(":").append(entry.getPort());
		}
		if (entry.getServico() != null) {
			sb.append(" - Serviço: ").append(entry.getServico());
		}
		
		TaskerboxTrayUtils.displayMessage("Taskerbox - Socket Tester", sb.toString(), MessageType.ERROR, actionListener);
	}
}
