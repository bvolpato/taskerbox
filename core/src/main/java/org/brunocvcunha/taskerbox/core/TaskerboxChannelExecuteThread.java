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
package org.brunocvcunha.taskerbox.core;

import lombok.Getter;

public class TaskerboxChannelExecuteThread extends Thread {

  @Getter
  private TaskerboxChannel<?> channel;

  @Getter
  private boolean finished;

  @Getter
  private boolean success;

  @Getter
  private Exception exception;

  /**
   * @param channel
   */
  public TaskerboxChannelExecuteThread(TaskerboxChannel<?> channel) {
    super();
    this.channel = channel;
  }


  /*
   * (non-Javadoc)
   *
   * @see java.lang.Thread#run()
   */
  @Override
public void run() {
    try {
      this.channel.execute();

      this.success = true;
    } catch (Exception e) {
      this.exception = e;
      this.success = false;
    } finally {
      this.finished = true;
    }
  }



}
