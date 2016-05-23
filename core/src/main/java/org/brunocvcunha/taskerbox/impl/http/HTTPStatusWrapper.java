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
package org.brunocvcunha.taskerbox.impl.http;

import java.util.Properties;

import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.TaskerboxVelocityUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * HTTP Status Wrapper - Emailable / Messageable
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class HTTPStatusWrapper implements ITaskerboxEmailable, ITaskerboxMessageable {

  @Getter
  @Setter
  @NonNull
  private String statusLine;

  @Getter
  @Setter
  @NonNull
  private String url;

  @Getter
  @Setter
  @NonNull
  private String content;

  @Override
  public String getEmailTitle(TaskerboxChannel<?> channel) {
    return channel.getId() + " Status";
  }

  @Override
  public String getEmailBody(TaskerboxChannel<?> channel) {

    Properties templateProps = new Properties();
    templateProps.put("statusLine", this.statusLine);
    templateProps.put("url", this.url);
    templateProps.put("content", this.content);

    return TaskerboxVelocityUtils.processTemplate("email/httpstatus.html", templateProps);
  }

  @Override
  public String getMessageTitle(TaskerboxChannel<?> channel) {
    return channel.getId() + " Status";
  }

  @Override
  public String getMessageBody(TaskerboxChannel<?> channel) {
    return channel.getId() + " - " + this.url + " - " + this.statusLine + " - " + this.content;
  }

  @Override
  public String toString() {
    return "HTTPStatusWrapper [statusLine=" + this.statusLine + ", url=" + this.url + ", content=" + this.content
        + "]";
  }



}
