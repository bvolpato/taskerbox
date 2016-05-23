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
package org.brunocvcunha.taskerbox.impl.jobs;

import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class DefaultJobSearchChannel extends TaskerboxChannel<String> {

  @Getter
  @Setter
  protected DefaultHttpClient httpClient;

  @Getter
  @Setter
  protected List<String> visaLines;

  @Getter
  @Setter
  protected List<String> experienceLines;

  @Getter
  @Setter
  protected List<String> roleLines;

  @Getter
  @Setter
  protected List<String> employerLines;

  @Getter
  @Setter
  protected List<String> locationLines;

  @Getter
  @Setter
  @TaskerboxField("Required Score")
  protected int requiredScore = 2000;

  @Getter
  @Setter
  @TaskerboxField("Max Pages")
  protected int maxPages = 1000;

  @Override
public void setup() {

    this.visaLines = listFromResource("jobseeker/visa.txt");
    this.experienceLines = listFromResource("jobseeker/experience.txt");
    this.roleLines = listFromResource("jobseeker/role.txt");
    this.employerLines = listFromResource("jobseeker/employer.txt");
    this.locationLines = listFromResource("jobseeker/location.txt");

  }



  public boolean considerTitle(String html) {
    String titleLc = html.toLowerCase();

    for (String line : this.roleLines) {
      if (titleLc.contains(line.toLowerCase().trim())) {
        logInfo(log, "[Role] --> Return false - Found: " + line);
        return false;
      }

      if (line.startsWith("=")) {
        String exactMatch = line.substring(1);

        if (titleLc.trim().equalsIgnoreCase(exactMatch.trim())) {
          logInfo(log, "[Role] --> Return false eq - Found: " + line);
          return false;
        }

      }
    }

    if ((titleLc.contains(".Net".toLowerCase()) && !titleLc.contains("Java".toLowerCase()))
        || (titleLc.contains("C++".toLowerCase()) && !titleLc.contains("Java".toLowerCase()))
        || (titleLc.contains("C#".toLowerCase()) && !titleLc.contains("Java".toLowerCase()))
        || (titleLc.contains("PHP".toLowerCase()) && !titleLc.contains("Java".toLowerCase()))
        || (titleLc.contains("Ruby".toLowerCase()) && !titleLc.contains("Java".toLowerCase()))
        || titleLc.contains("Mobile Application Developer".toLowerCase())

        /** Not yet Skill **/
        || titleLc.contains("Hadoop".toLowerCase()) || titleLc.contains("Big Data".toLowerCase())

    ) {
      return false;
    }

    return true;
  }

  public boolean considerEmployer(String html) {
    String htmlLc = html.toLowerCase();

    for (String line : this.employerLines) {
      if (htmlLc.contains(line.toLowerCase().trim())) {
        logInfo(log, "[Employer] --> Return false - Found: " + line);
        return false;
      }
    }

    return true;
  }

  public boolean considerLocation(String html) {
    String htmlLc = html.toLowerCase();

    for (String line : this.locationLines) {
      if (htmlLc.contains(line.toLowerCase())) {
        logInfo(log, "[Location] --> Return false - Found: " + line);
        return false;
      }
    }

    return true;
  }


  public boolean considerExperienceDescription(String html) {
    String htmlLc = html.toLowerCase();

    for (String line : this.experienceLines) {
      if (htmlLc.contains(line.toLowerCase())) {
        logInfo(log, "[Experience] --> Return false - Found: " + line);
        return false;
      }
    }

    return true;
  }

  public boolean considerVisaDescription(String html) {
    String htmlLc = html.toLowerCase();

    for (String visa : this.visaLines) {
      if (htmlLc.contains(visa.toLowerCase())) {
        logInfo(log, "[Visa] --> Return false - Found: " + visa);
        return false;
      }
    }
    return true;
  }


  public BasicClientCookie buildCookie(String name, String value) {
    return TaskerboxHttpBox.buildCookie(name, value, "www.linkedin.com", "/");
  }


  @Override
  protected String getItemFingerprint(String entry) {
    return entry;
  }


  @Override
  public String getGroupName() {
    return "JobSeeker";
  }
}
