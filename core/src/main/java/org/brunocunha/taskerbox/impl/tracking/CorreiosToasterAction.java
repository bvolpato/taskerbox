/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		DefaultTaskerboxAction<CorreiosTrackingWrapper> {

	@Getter @Setter
	private ActionListener actionListener;

	@Getter @Setter
	private String title = TaskerboxConstants.TITLE;

	@Override
	public void action(final CorreiosTrackingWrapper entry) {
		log.debug("Action on CorreiosToasterAction: " + entry);
		TaskerboxTrayUtils.displayMessage(title, CorreiosChannel
				.formatTracking(entry.getValue(), getChannel().getProperty("tracking"),
						getChannel().getProperty("descricao")),
				MessageType.INFO, actionListener);
	}

}
