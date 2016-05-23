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
package org.brunocvcunha.taskerbox.impl.logwatch;

import java.io.File;

import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Log Watch Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class LogWatchChannel extends TaskerboxChannel<String> {

  // @URL
  @TaskerboxField("URL")
  @Getter
  @Setter
  private String[] url;

  @TaskerboxField("Results File")
  @Getter
  @Setter
  private File saveResultFile;

  @TaskerboxField("Control by Size")
  @Getter
  @Setter
  private boolean controlBySize = true;

  private long[] size;

  @Override
  public void setup() {
    this.size = new long[this.url.length];
  }

  @Override
  protected void execute() {

    try {
      url: for (int x = 0; x < this.url.length; x++) {
        String uniqueUrl = this.url[x];

        try {
          logDebug(log, "Checking [" + uniqueUrl + "]");

          if (this.controlBySize) {
            long uniqueSize = TaskerboxHttpBox.getInstance().getResponseSizeForURL(uniqueUrl);

            logDebug(log, "Size of response: " + uniqueUrl + " - " + uniqueSize);

            if (uniqueSize >= 0 && this.size[x] == uniqueSize) {
              logDebug(log, "Same size! Skipping URL " + uniqueUrl);
              continue url;
            }

            this.size[x] = uniqueSize;
          }

          String responseBody = TaskerboxHttpBox.getInstance().getStringBodyForURL(uniqueUrl);

          logDebug(log, "Got LogWatch Response: " + uniqueUrl + " - " + responseBody.length()
              + " bytes");

          perform(responseBody);
        } catch (Exception e) {
          logError(log, "Error watching log for " + getId() + " - " + uniqueUrl, e);
        }
      }

    } catch (Exception e) {
      logError(log, "Error watching log for " + getId(), e);
    }

  }

  @Override
  protected String getItemFingerprint(String entry) {
    return entry.replaceAll("\\s+", " ").trim();
  }



}
