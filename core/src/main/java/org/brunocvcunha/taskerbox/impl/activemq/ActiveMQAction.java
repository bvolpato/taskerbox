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
package org.brunocvcunha.taskerbox.impl.activemq;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;

import lombok.extern.log4j.Log4j;

@Log4j
public class ActiveMQAction extends DefaultTaskerboxAction<Message> {

  @Override
  public void action(Message message) {
    try {
      if (message instanceof ActiveMQTextMessage) {
        ActiveMQTextMessage mqTextMessage = (ActiveMQTextMessage) message;
        logInfo(log, "Received MQ Message: " + mqTextMessage.getText());
      } else {
        logInfo(log, "Received Default Message: " + message);
      }
    } catch (Exception e) {
      logError(log, "Error on Action", e);
    }
  }

}
