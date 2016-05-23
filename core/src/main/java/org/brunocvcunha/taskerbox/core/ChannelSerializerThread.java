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

import java.io.IOException;

import org.brunocvcunha.taskerbox.core.utils.TaskerboxFileUtils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ChannelSerializerThread extends Thread {

  @NonNull
  @Getter
  @Setter
  private TaskerboxChannel<?> channel;

  @Override
public void run() {

    while (this.channel.getLastPerformed() > (System.currentTimeMillis() - 1000)) {
      try {
        Thread.sleep(500L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      TaskerboxFileUtils.serializeMemory(this.channel);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.channel.setPendingSerializerThread(false);

  }

}
