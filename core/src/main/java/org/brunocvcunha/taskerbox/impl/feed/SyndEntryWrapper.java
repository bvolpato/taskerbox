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
package org.brunocvcunha.taskerbox.impl.feed;

import com.sun.syndication.feed.synd.SyndEntry;

import java.util.Properties;

import org.brunocvcunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.TaskerboxVelocityUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * SyndEntry Wrapper - Emailable
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class SyndEntryWrapper implements ITaskerboxEmailable {

  @Getter
  @Setter
  private SyndEntry value;

  public SyndEntryWrapper(SyndEntry value) {
    this.value = value;
  }

  @Override
  public String getEmailTitle(TaskerboxChannel<?> channel) {
    return "[" + channel.getId() + "] " + this.value.getTitle();
  }

  @Override
  public String getEmailBody(TaskerboxChannel<?> channel) {

    Properties templateProps = new Properties();
    templateProps.put("entry", this.value);

    return TaskerboxVelocityUtils.processTemplate("email/synd.html", templateProps);
  }
}
