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
package org.brunocvcunha.taskerbox.impl.whatsapp;

import java.awt.TrayIcon.MessageType;

import javax.validation.constraints.NotNull;

import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocvcunha.taskerbox.core.TaskerboxConstants;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxTrayUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.sumppen.whatsapi4j.WhatsApi;
import net.sumppen.whatsapi4j.WhatsAppException;

/**
 * Action that sends WhatsApp Msgs
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class WhatsappAction extends DefaultTaskerboxAction<Object> {

  @Getter
  @Setter
  @NotNull
  private String from;

  @Getter
  @Setter
  @NotNull
  private String password;

  @Getter
  @Setter
  @NotNull
  private String appName;

  @Getter
  @Setter
  @NotNull
  private String alias;

  @Getter
  @Setter
  @NotNull
  private String to;

  @Getter
  @Setter
  private Long messageInterval = 2000L;

  @Override
  public void action(Object text) {

    String sendMessage = text.toString();

    if (text instanceof ITaskerboxMessageable) {
      ITaskerboxMessageable messageble = (ITaskerboxMessageable) text;
      sendMessage =
          messageble.getMessageTitle(getChannel()) + " - "
              + messageble.getMessageBody(getChannel());
    } else if (text instanceof ITaskerboxEmailable) {
      ITaskerboxEmailable emailable = (ITaskerboxEmailable) text;
      sendMessage =
          emailable.getEmailTitle(getChannel()) + " - " + emailable.getEmailBody(getChannel());
    }

    logInfo(log, "Sending Message: " + sendMessage);

    WhatsApi api = WhatsappConnection.getOrCreateInstance(this.from, this.appName, this.alias, this.password);
    try {
      api.sendMessage(this.to, sendMessage);
      Thread.sleep(this.messageInterval); // Some delay
    } catch (WhatsAppException e) {
      log.error("Error sending WhatsApp Message", e);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE, "Sending WhatsApp " + sendMessage,
        MessageType.INFO, null);
  }
}
