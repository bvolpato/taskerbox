package org.brunocunha.taskerbox.impl.http;

import java.awt.Desktop;
import java.net.URI;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;

/**
 * Open URL Action
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class URLOpenerAction extends DefaultTaskerboxAction<String>{

	@Override
	public void action(String input) {
		try {
			Desktop.getDesktop().browse(new URI(input));
		} catch (Exception e) {
			logError(log, "Error running URLOpenerAction for " + input, e);
			e.printStackTrace();
		} 
	}

}
