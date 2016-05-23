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
package org.brunocvcunha.taskerbox.impl.http;

import com.sun.syndication.io.FeedException;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.hibernate.validator.constraints.URL;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * HTTP Uptime Monitor Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class HTTPUptimeChannel extends TaskerboxChannel<HTTPStatusWrapper> {

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
    logDebug(log, "Checking #" + this.checkCount + "... [" + this.url + " / '" + this.filter + "']");

    Throwable lastError = null;

    int tryNumber = 0;

    while (tryNumber++ < this.numTries) {

      try {
        HttpResponse response = TaskerboxHttpBox.getInstance().getResponseForURLNewClient(this.url);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
          perform(new HTTPStatusWrapper(response.getStatusLine().toString(), this.url, "Error fetching "
              + this.url + " - " + response.getStatusLine().toString()));
          logWarn(log, "Error while fetching " + this.url + " - " + response.getStatusLine());
          return;
        }

        String responseBody =
            TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());
        logDebug(log, "Got Response: " + responseBody);

        if ((this.contains && responseBody.toLowerCase().contains(this.filter.toLowerCase()))
            || (!this.contains && !responseBody.toLowerCase().contains(this.filter.toLowerCase()))) {
          perform(new HTTPStatusWrapper(response.getStatusLine().toString(), this.url, responseBody));
        }

        lastError = null;
        break;

      } catch (Exception e) {
        logWarn(log, "Exception \"" + e.getMessage() + "\" getting " + this.url + ". Try " + tryNumber
            + "/" + this.numTries);
        lastError = e;

        try {
          Thread.sleep(this.errorInterval);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }

      }

    }

    if (lastError != null) {
      logError(log, "Error occurred on HTTPUptimeChannel - " + this.url + " . Performing...", lastError);

      perform(new HTTPStatusWrapper("Error", this.url, "Error fetching " + this.url + " - "
          + lastError.getMessage() + " - " + this.numTries + " tries"));
    }

  }


  @Override
  protected String getItemFingerprint(HTTPStatusWrapper entry) {
    return entry.getContent();
  }

  @Override
  public String toString() {
    return "HTTPHTMLChannel [url=" + this.url + ", filter=" + this.filter + ", unique=" + this.unique + ", every="
        + getEvery() + "]";
  }


}
