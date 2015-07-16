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
package org.brunocunha.taskerbox.impl.whatsapp;

import java.awt.TrayIcon.MessageType;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.sumppen.whatsapi4j.EventManager;
import net.sumppen.whatsapi4j.MessageProcessor;
import net.sumppen.whatsapi4j.WhatsApi;
import net.sumppen.whatsapi4j.WhatsAppException;
import net.sumppen.whatsapi4j.example.ExampleEventManager;
import net.sumppen.whatsapi4j.example.ExampleMessageProcessor;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocunha.taskerbox.core.TaskerboxConstants;
import org.brunocunha.taskerbox.core.utils.TaskerboxTrayUtils;

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

    WhatsApi api = WhatsappConnection.getOrCreateInstance(from, appName, alias, password);
    try {
      api.sendMessage(to, sendMessage);
      Thread.sleep(messageInterval); //Some delay
    } catch (WhatsAppException e) {
      log.error("Error sending WhatsApp Message", e);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE, "Sending WhatsApp " + sendMessage,
        MessageType.INFO, null);
  }
}
