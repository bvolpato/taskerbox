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

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.brunocvcunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.brunocvcunha.taskerbox.impl.email.EmailAction;
import org.brunocvcunha.taskerbox.impl.email.EmailDelegateAction;
import org.brunocvcunha.taskerbox.impl.email.EmailValueVO;
import org.brunocvcunha.taskerbox.impl.toaster.StringToasterAction;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Log4j
public class LogWatchAction extends EmailDelegateAction<String> {

  @Getter
  private Set<String> alreadyAct = new TreeSet<>();

  @Getter
  @Setter
  private List<String> seekFor;

  @Getter
  @Setter
  private List<String> ignored;

  @Override
  public void setup() {
    try {
      this.alreadyAct = (Set<String>) TaskerboxFileUtils.deserializeMemory(this);
    } catch (Exception e) {
      logWarn(log,
          "Error occurred while deserializing data for " + this.getId() + ": " + e.getMessage());
    }
  }


  @Override
  public void action(final String entry) {


    final String fullLog = entry;

    File saveResultFile = ((LogWatchChannel) getChannel()).getSaveResultFile();

    if (saveResultFile != null && !fullLog.contains("404 - File or directory not found")) {
      try {

        if (saveResultFile.length() != fullLog.length()) {
          logInfo(log, "Saving content to file " + saveResultFile.getAbsolutePath()
              + " [updated- was " + saveResultFile.length() + " now " + fullLog.length() + "]...");
          FileWriter out = new FileWriter(saveResultFile);
          out.write(fullLog);
          out.close();
        } else {
          log.debug("Same size from content var and log (" + fullLog.length() + "). Ignoring...");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    boolean needSerialize = false;
    List<String> newLines = new ArrayList<>();

    String[] logLines = fullLog.split("(\r?\n)+");

    line: for (String logLine : logLines) {
      logLine = logLine.replaceAll("\\s+", " ").trim();

      boolean validLine = false;

      if (this.ignored != null && !this.ignored.isEmpty()) {
        for (String ignoredStr : this.ignored) {
          if (logLine.toLowerCase().contains(ignoredStr.toLowerCase())) {
            continue line;
          }
        }
      }

      for (String seek : this.seekFor) {
        if (logLine.toLowerCase().contains(seek.toLowerCase())) {

          validLine = true;
          break;
        }
      }

      if (!validLine) {
        continue;
      }

      if (!this.alreadyAct.contains(logLine)) {
        this.alreadyAct.add(logLine);
        needSerialize = true;

        newLines.add(logLine);

        spreadToaster(getChannel().getProperty("url"), logLine);
        logInfo(log, "Found new line --> " + logLine);
      } else {
        log.debug("Found valid line, but already alerted --> " + logLine);
      }


    }

    if (!newLines.isEmpty()) {

      EmailAction email = getEmailAction();
      EmailValueVO emailVO = new EmailValueVO();
      emailVO.setTitle("Log Watcher [" + getChannel().getId() + "]");

      StringBuffer sb = new StringBuffer();

      sb.append(getChannel().getProperty("url")).append("<br>");
      for (String line : newLines) {
        sb.append(line).append("<br>");
      }
      emailVO.setBody(sb.toString());

      email.action(emailVO);

    }

    if (needSerialize) {
      try {
        TaskerboxFileUtils.serializeMemory(this, this.alreadyAct);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

  }

  public void spreadToaster(final String url, String postTitle) {
    if (!GraphicsEnvironment.isHeadless()) {
      StringToasterAction toasterAction = new StringToasterAction();
      toasterAction.setActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            Desktop.getDesktop().browse(new URI(url));
          } catch (IOException e1) {
            e1.printStackTrace();
          } catch (URISyntaxException e1) {
            e1.printStackTrace();
          }

        }
      });

      toasterAction.setTitle("Log Watcher Alert");
      toasterAction.action(postTitle);
    }

  }


}
