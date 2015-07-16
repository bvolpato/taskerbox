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
package org.brunocunha.taskerbox.impl.twitter;

import java.util.Properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxVelocityUtils;

import twitter4j.Status;

/**
 * Twitter Status Wrapper - Emailable
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class StatusWrapper implements ITaskerboxEmailable, ITaskerboxMessageable {

  @Getter
  @Setter
  private Status value;

  public StatusWrapper(Status value) {
    this.value = value;
  }

  @Override
  public String getEmailTitle(TaskerboxChannel<?> channel) {
    return "@" + value.getUser().getScreenName() + " Tweet";
  }

  @Override
  public String getEmailBody(TaskerboxChannel<?> channel) {

    Properties templateProps = new Properties();
    templateProps.put("status", value);

    return TaskerboxVelocityUtils.processTemplate("email/tweet.html", templateProps);
  }

  @Override
  public String getMessageTitle(TaskerboxChannel<?> channel) {
    return "Tweet de @" + value.getUser().getScreenName();
  }

  @Override
  public String getMessageBody(TaskerboxChannel<?> channel) {
    return value.getText();
  }

  @Override
  public String toString() {
    return "StatusWrapper [value=" + value + "]";
  }


}
