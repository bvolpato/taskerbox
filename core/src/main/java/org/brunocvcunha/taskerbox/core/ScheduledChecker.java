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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Thread that is created by a scheduler
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
@RequiredArgsConstructor
public class ScheduledChecker extends Thread {

  @NonNull
  @Getter
  @Setter
  private TaskerboxChannel<?> channel;

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Thread#run()
   */
  @Override
public void run() {
    try {
      if (!this.channel.isPaused()) {

        if (this.channel.getTimeout() <= 0) {
          this.channel.check();
        } else {

          ExecutorService executor = Executors.newCachedThreadPool();
          Callable<Object> task = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
              ScheduledChecker.this.channel.check();
              return ScheduledChecker.this.channel.getCheckCount();
            }
          };

          Future<Object> future = executor.submit(task);
          try {
            future.get(this.channel.getTimeout(), TimeUnit.MILLISECONDS);
          } catch (Exception e) {
            log.warn("Timeout reached on scheduler for " + this.channel.getId());
          } finally {
            future.cancel(true);
          }


        }


      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
