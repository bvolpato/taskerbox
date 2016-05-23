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
package org.brunocvcunha.taskerbox.impl.sockettester;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import org.brunocvcunha.sockettester.vo.SocketTesterVO;
import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.TaskerboxConstants;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxTrayUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Action that shows Strings in a Toaster Popup
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class SocketTesterAction extends DefaultTaskerboxAction<SocketTesterVO> {

  @Getter
  @Setter
  private ActionListener actionListener;

  @Getter
  @Setter
  private String title = TaskerboxConstants.TITLE;

  @Override
  public void action(final SocketTesterVO entry) {
    log.debug("Action on SocketTesterAction: " + entry);

    StringBuffer sb = new StringBuffer();
    sb.append("Environment Problems");

    if (entry.getName() != null) {
      sb.append(" - ").append(entry.getName());
    }
    if (entry.getHost() != null) {
      sb.append(" - ").append(entry.getHost()).append(":").append(entry.getPort());
    }
    if (entry.getService() != null) {
      sb.append(" - Service: ").append(entry.getService());
    }

    TaskerboxTrayUtils.displayMessage("Taskerbox - Socket Tester", sb.toString(),
        MessageType.ERROR, this.actionListener);
  }
}
