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

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.brunocvcunha.taskerbox.gui.TaskerboxControlFrame;

import lombok.extern.log4j.Log4j;

/**
 * Class responsible for launching channels. It creates threads that are called in intervals for
 * timed threads, or a new single-thread for daemons.
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxLauncher {

  public static void startChannel(final TaskerboxChannel<?> channel,
      final TaskerboxControlFrame frame, final Collection<TaskerboxChannel<?>> daemons,
      final Collection<TaskerboxChannel<?>> channels) throws Exception {

    if (channel.isDaemon()) {

      try {
        Thread daemonThread = new Thread() {
          @Override
        public void run() {
            try {
              channel.setup();

              synchronized (channels) {
                channels.add(channel);
              }

              if (frame != null) {
                frame.updateChannels();
              }

              while (true) {
                try {
                  channel.check();
                } catch (Exception e) {
                  e.printStackTrace();
                }

                try {
                  Thread.sleep(channel.getEvery());
                } catch (InterruptedException ex) {
                }

              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        daemonThread.setName("daemon-" + channel.getId());
        daemonThread.start();

      } catch (Exception e) {
        e.printStackTrace();
      }

      daemons.add(channel);
    } else {
      try {

        Thread startThread = new Thread() {

          @Override
        public void run() {
            try {
              channel.setup();
            } catch (Exception e) {
              e.printStackTrace();
            }

            synchronized (channels) {
              channels.add(channel);
            }
            if (frame != null) {
              frame.updateChannels();
            }

            channel.scheduleTask(0, channel.getEvery(), TimeUnit.MILLISECONDS);

          }
        };
        startThread.start();

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    log.info("Channel " + channel + " created.");
  }

}
