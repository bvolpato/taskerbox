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
package org.brunocvcunha.taskerbox.impl.custom.hardmob;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.TreeSet;

import org.brunocvcunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.brunocvcunha.taskerbox.impl.email.EmailDelegateAction;
import org.brunocvcunha.taskerbox.impl.toaster.StringToasterAction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.extern.log4j.Log4j;

@Log4j
public class HardmobAction extends EmailDelegateAction<Document> {

  private String id;

  private Set<String> alreadyAct = new TreeSet<>();

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
  public void action(final Document entry) {

    for (Element el : entry.select(".threadinfo")) {
      final String url = el.select(".title").attr("href");
      final String postTitle = el.select(".title").text();

      if (!this.alreadyAct.contains(postTitle)) {
        this.alreadyAct.add(postTitle);

        spreadAction(url, postTitle);

        try {
          TaskerboxFileUtils.serializeMemory(this, this.alreadyAct);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
      }


    }

  }

  public void spreadAction(final String url, String postTitle) {
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

      toasterAction.setTitle("Hardmob Alert");
      toasterAction.action(postTitle);
    }

  }

  @Override
public String getId() {
    return this.id;
  }

  @Override
public void setId(String id) {
    this.id = id;
  }

}
