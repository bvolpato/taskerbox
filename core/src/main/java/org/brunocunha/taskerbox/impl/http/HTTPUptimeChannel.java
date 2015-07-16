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
package org.brunocunha.taskerbox.impl.http;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.hibernate.validator.constraints.URL;

import com.sun.syndication.io.FeedException;

/**
 * HTTP Uptime Monitor Channel
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class HTTPUptimeChannel extends TaskerboxChannel<String> {

  @URL
  @Getter
  @Setter
  @TaskerboxField("URL")
  private String url;

  @Getter
  @Setter
  @TaskerboxField("Filter Contains")
  private boolean contains;

  @Getter
  @Setter
  @TaskerboxField("Filter")
  private String filter;

  @Getter
  @Setter
  @TaskerboxField("Unique Action")
  private boolean unique;

  @Getter
  @Setter
  @TaskerboxField("Number of Tries")
  private int numTries = 1;

  @Getter
  @Setter
  @TaskerboxField("Error Interval")
  private long errorInterval = 10000L;

  @Override
  protected void execute() throws IOException, IllegalArgumentException, FeedException {
    logDebug(log, "Checking #" + checkCount + "... [" + url + " / '" + filter + "']");

    Throwable lastError = null;

    int tryNumber = 0;

    while (tryNumber++ < numTries) {

      try {
        HttpResponse response = TaskerboxHttpBox.getInstance().getResponseForURLNewClient(url);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
          perform("Error fetching " + url + " - " + response.getStatusLine().toString());
          logWarn(log, "Error while fetching " + url + " - " + response.getStatusLine());
          return;
        }

        String responseBody =
            TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
        logDebug(log, "Got Response: " + responseBody);

        if ((contains && responseBody.toLowerCase().contains(filter.toLowerCase()))
            || (!contains && !responseBody.toLowerCase().contains(filter.toLowerCase()))) {
          perform(responseBody);
        }

        lastError = null;
        break;

      } catch (Exception e) {
        logWarn(log, "Exception \"" + e.getMessage() + "\" getting " + url + ". Try " + tryNumber
            + "/" + numTries);
        lastError = e;

        try {
          Thread.sleep(errorInterval);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }

      }

    }

    if (lastError != null) {
      logError(log, "Error occurred on HTTPUptimeChannel - " + url + " . Performing...", lastError);
      perform("Error fetching " + url + " - " + lastError.getMessage() + " - " + numTries
          + " tries");
    }

  }


  @Override
  protected String getItemFingerprint(String entry) {
    return entry;
  }

  @Override
  public String toString() {
    return "HTTPHTMLChannel [url=" + url + ", filter=" + filter + ", unique=" + unique + ", every="
        + getEvery() + "]";
  }


}
