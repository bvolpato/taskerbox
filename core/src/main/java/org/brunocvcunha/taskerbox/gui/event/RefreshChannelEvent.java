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
package org.brunocvcunha.taskerbox.gui.event;

import org.brunocvcunha.taskerbox.core.TaskerboxChannel;

public class RefreshChannelEvent {
  private TaskerboxChannel<?> channel;

  private boolean running;

  public RefreshChannelEvent(TaskerboxChannel<?> channel, boolean running) {
    super();
    this.channel = channel;
    this.running = running;
  }

  public TaskerboxChannel<?> getChannel() {
    return this.channel;
  }

  public void setChannel(TaskerboxChannel<?> channel) {
    this.channel = channel;
  }

  public boolean isRunning() {
    return this.running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }


}
