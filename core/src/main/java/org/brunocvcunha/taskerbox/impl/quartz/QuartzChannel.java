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
package org.brunocvcunha.taskerbox.impl.quartz;

import java.util.List;

import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class QuartzChannel extends TaskerboxChannel<Long> {

  @Getter
  @Setter
  private List<String> triggers;

  @Getter
  @Setter
  private boolean daemon = true;

  @Override
  protected void execute() throws Exception {
    SchedulerFactory schedFact = new StdSchedulerFactory();
    Scheduler sched = schedFact.getScheduler();
    sched.start();


    JobDataMap map = new JobDataMap();
    map.put("channel", this);

    JobDetail job =
        JobBuilder.newJob(QuartzChannelJob.class).withIdentity("CronJob", getId()).setJobData(map)
            .storeDurably(true).build();

    sched.addJob(job, true);

    for (String triggerStr : this.triggers) {
      Trigger trigger =
          TriggerBuilder.newTrigger().withIdentity("CronTrigger" + triggerStr, getId()).forJob(job)
              .withSchedule(CronScheduleBuilder.cronSchedule(triggerStr)).build();

      sched.scheduleJob(trigger);

    }


  }

  @Override
  protected String getItemFingerprint(Long entry) {
    return entry.toString();
  }

}
