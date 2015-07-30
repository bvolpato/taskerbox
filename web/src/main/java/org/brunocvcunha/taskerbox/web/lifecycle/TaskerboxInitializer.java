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
package org.brunocvcunha.taskerbox.web.lifecycle;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.brunocvcunha.taskerbox.Taskerbox;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;

@WebListener
public class TaskerboxInitializer implements ServletContextListener {

  private static final Logger log = Logger.getLogger(TaskerboxInitializer.class.getSimpleName());

  private static Taskerbox taskerboxInstance;

  public static Taskerbox getInstance() {
    if (taskerboxInstance == null) {
      throw new IllegalStateException("Taskerbox was not initialized.");
    }

    return taskerboxInstance;
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    log.info("Initializating Taskerbox...");

    try {

      taskerboxInstance = new Taskerbox();
      taskerboxInstance.handleDefaultFiles();

    } catch (Exception e) {
      throw new RuntimeException("Exception initializing taskerbox", e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    log.info("Destroying Taskerbox...");

    if (taskerboxInstance != null) {

      for (TaskerboxChannel<?> channel : taskerboxInstance.getChannels()) {
        channel.getScheduler().shutdown();
      }

    }

  }


}
