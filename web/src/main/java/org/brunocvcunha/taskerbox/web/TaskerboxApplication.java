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
package org.brunocvcunha.taskerbox.web;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.brunocvcunha.taskerbox.Taskerbox;
import org.brunocvcunha.taskerbox.web.config.TaskerboxConfiguration;
import org.brunocvcunha.taskerbox.web.health.TemplateHealthCheck;
import org.brunocvcunha.taskerbox.web.resources.ChannelsResource;

public class TaskerboxApplication extends Application<TaskerboxConfiguration> {
  
  private Taskerbox taskerboxInstance;
  
  public static void main(String[] args) throws Exception {
    new TaskerboxApplication().run(args);
  }

  @Override
  public String getName() {
    return "taskerbox";
  }

  @Override
  public void initialize(Bootstrap<TaskerboxConfiguration> bootstrap) {
    
  }

  @Override
  public void run(TaskerboxConfiguration configuration, Environment environment) {
    taskerboxInstance = new Taskerbox();
    try {
      taskerboxInstance.handleDefaultFiles();
    } catch (Exception e) {
      e.printStackTrace();
    }

    final ChannelsResource channels = new ChannelsResource(taskerboxInstance);
    
    final TemplateHealthCheck healthCheck =
        new TemplateHealthCheck(configuration.getTemplate());
    environment.healthChecks().register("template", healthCheck);

    environment.jersey().register(channels);

  }

}
