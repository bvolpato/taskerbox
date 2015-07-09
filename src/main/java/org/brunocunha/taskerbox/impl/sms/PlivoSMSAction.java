package org.brunocunha.taskerbox.impl.sms;

import java.awt.TrayIcon.MessageType;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocunha.taskerbox.core.TaskerboxConstants;
import org.brunocunha.taskerbox.core.utils.TaskerboxTrayUtils;

import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.call.Call;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;

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

    RestAPI restAPI = new RestAPI(authId, authToken, "v1");

    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
    params.put("src", from);
    params.put("dst", to);

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
