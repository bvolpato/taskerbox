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
package org.brunocvcunha.taskerbox.impl.slack;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.brunocvcunha.inutils4j.MyStreamUtils;
import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Action that sends message to Slack chat
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class SlackAction extends DefaultTaskerboxAction<Object> {

  private final static String API_URL = "https://slack.com/api/chat.postMessage";
  private int timeout = 30_000;
  
  @NotEmpty
  @Getter
  @Setter
  @TaskerboxField("Token")
  private String token;

  @NotEmpty
  @Getter
  @Setter
  @TaskerboxField("Slack Channel")
  private String slackChannel;

  @Getter
  @Setter
  @NotEmpty
  @TaskerboxField("Username")
  private String username;

  @Getter
  @Setter
  @NotEmpty
  @TaskerboxField("Icon Emoji")
  private String iconEmoji;

  @Getter
  @Setter
  @TaskerboxField("Message Prefix")
  private String messagePrefix;

  @Getter
  @Setter
  @TaskerboxField("Message Override")
  private String messageOverride;

  @Getter
  @Setter
  private boolean showToaster = true;

  @Override
  public void action(final Object entry) {

    try {
      if (entry instanceof ITaskerboxMessageable) {
        handleMessageable((ITaskerboxMessageable) entry);
      } else if (entry instanceof ITaskerboxEmailable) {
        handleEmailable((ITaskerboxEmailable) entry);
      } else {
        send(entry.toString());
      }
    } catch (Exception e) {
      log.error("Error sending Slack message", e);
    }
  }

  private void handleEmailable(ITaskerboxEmailable entry) throws IOException {
    send(entry.getEmailTitle(getChannel()) + " - " + entry.getEmailBody(getChannel()));
  }


  private void handleMessageable(ITaskerboxMessageable entry) throws IOException {
    send(entry.getMessageTitle(getChannel()) + " - " + entry.getMessageBody(getChannel()));
  }



  public void send(String message) throws IOException {
    final URL url = new URL(API_URL);

    final StringWriter w = new StringWriter();
    w.append("token=").append(token).append("&");
    
    if (messageOverride != null) {
      w.append("text=").append(URLEncoder.encode(messageOverride, "UTF-8")).append('&');
    } else {
      w.append("text=").append(URLEncoder.encode(messagePrefix + " - " + message, "UTF-8")).append('&');
    }
    
    if (slackChannel != null) {
      w.append("channel=").append(URLEncoder.encode(slackChannel, "UTF-8")).append('&');
    }
    if (username != null) {
      w.append("username=").append(URLEncoder.encode(username, "UTF-8")).append('&');
    }
    if (iconEmoji != null) {
      w.append("icon_emoji=").append(URLEncoder.encode(iconEmoji, "UTF-8"));
    }

    log.info("Sending message to: " + url + ", data: " + w.toString());

    final byte[] bytes = w.toString().getBytes("UTF-8");

    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(timeout);
    conn.setReadTimeout(timeout);
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setFixedLengthStreamingMode(bytes.length);
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    
    final OutputStream os = conn.getOutputStream();
    os.write(bytes);

    os.flush();
    os.close();
    
    String returnContent = MyStreamUtils.readContent(conn.getInputStream());
    log.info("Message sent! Return: " + returnContent);

  }


}
