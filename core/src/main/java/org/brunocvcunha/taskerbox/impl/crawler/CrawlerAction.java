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
package org.brunocvcunha.taskerbox.impl.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.jsoup.nodes.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class CrawlerAction extends DefaultTaskerboxAction<Document> {

  private File output;

  @Getter
  @Setter
  private List<String> filters;

  @Getter
  @Setter
  private List<String> ignored;

  @Getter
  @Setter
  private List<String> patterns;

  @Getter
  @Setter
  private List<Pattern> compiledPatterns = new ArrayList<>();

  @Getter
  @Setter
  private Set<String> alreadyAct = new TreeSet<>();

  private int count;

  @Override
  public void setup() {
    try {
      logInfo(log, "Starting Crawler " + getId());
      logInfo(log, "Filters: " + this.getFilters());
      logInfo(log, "Ignores: " + this.getIgnored());

      if (this.patterns != null) {
        for (String pattern : this.patterns) {
          this.compiledPatterns.add(Pattern.compile(pattern));
        }
      }

      this.alreadyAct = (Set<String>) TaskerboxFileUtils.deserializeMemory(this);

    } catch (Exception e) {
      logWarn(log,
          "Error occurred while deserializing data for " + this.getId() + ": " + e.getMessage());
    }
  }

  public boolean isBounded(String content) {
    String lowerContent = content.toLowerCase();
    for (String filter : this.filters) {
      if (lowerContent.contains(filter.toLowerCase())) {
        return true;
      }
    }

    for (Pattern pattern : this.compiledPatterns) {
      if (pattern.matcher(lowerContent).find()) {
        return true;
      }
    }

    return false;
  }

  public boolean isIgnored(String content) {
    String lowerContent = content.toLowerCase();
    for (String find : this.ignored) {
      if (lowerContent.contains(find.toLowerCase())) {
        return true;
      }
    }

    return false;
  }

  public boolean canAct(String key) {
    return !getAlreadyAct().contains(key);
  }

  public void addAct(String key) {
    getAlreadyAct().add(key);
  }

  public void serializeAlreadyAct() {
    if (++this.count > 5) {
      this.count = 0;

      try {
        TaskerboxFileUtils.serializeMemory(this, getAlreadyAct());
      } catch (IOException e1) {
        e1.printStackTrace();
      }

    }
  }

  public boolean isConsiderable(String id, String content) {
    if (content.length() < 5) {
      return false;
    }

    return true;
  }

  public boolean isValid(String id, String content) {
    if (isIgnored(content)) {
      return false;
    }

    return isBounded(content);
  }

  public void doValid(String id, String content) throws IOException {
    FileWriter out = new FileWriter(new File(getOutput(), id + ".txt"));
    out.write(content.replaceAll("\r?\n", "\r\n"));
    out.close();
  }

  public void doInvalid(String id, String content) throws IOException {
    File dir = new File(getOutput(), "not");
    if (!dir.exists()) {
      dir.mkdirs();
    }

    FileWriter out = new FileWriter(new File(dir, id.replace("\\", "").replace("/", "") + ".txt"));
    out.write(content.replaceAll("\r?\n", "\r\n"));
    out.close();
  }

  public File getOutput() {
    if (!this.output.exists()) {
      this.output.mkdirs();
    }

    return this.output;
  }

  public void setOutput(File output) {
    if (!output.exists()) {
      output.mkdirs();
    }
    this.output = output;
  }

  public void sleep(long interval) {
    try {
      Thread.sleep(interval);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
