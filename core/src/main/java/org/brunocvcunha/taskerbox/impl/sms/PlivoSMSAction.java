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
package org.brunocvcunha.taskerbox.impl.sms;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;

import java.awt.TrayIcon.MessageType;
import java.util.LinkedHashMap;

import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocvcunha.taskerbox.core.TaskerboxConstants;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxTrayUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Action that sends SMS using Plivo
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class PlivoSMSAction extends DefaultTaskerboxAction<Object> {

  @Getter
  @Setter
  private String authId;

  @Getter
  @Setter
  private String authToken;

  @Getter
  @Setter
  private String from;

  @Getter
  @Setter
  private String to;

  @Override
  public void action(Object text) {
    logInfo(log, "Sending SMS: " + text.toString());

    RestAPI restAPI = new RestAPI(this.authId, this.authToken, "v1");

    LinkedHashMap<String, String> params = new LinkedHashMap<>();
    params.put("src", this.from);
    params.put("dst", this.to);

    if (text instanceof ITaskerboxMessageable) {
      ITaskerboxMessageable messageble = (ITaskerboxMessageable) text;
      params.put(
          "text",
          messageble.getMessageTitle(getChannel()) + " - "
              + messageble.getMessageBody(getChannel()));
    } else if (text instanceof ITaskerboxEmailable) {
      ITaskerboxEmailable emailable = (ITaskerboxEmailable) text;
      params.put("text",
          emailable.getEmailTitle(getChannel()) + " - " + emailable.getEmailBody(getChannel()));
    } else {
      params.put("text", text.toString());
    }

    MessageResponse response;
    try {
      response = restAPI.sendMessage(params);
      logInfo(log, "Response: " + response.apiId);
    } catch (PlivoException e) {
      logError(log, "Error while sending SMS", e);
    }

    TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE, "Sending SMS " + text,
        MessageType.INFO, null);
  }
}
