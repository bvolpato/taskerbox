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
package org.brunocvcunha.taskerbox.impl.custom.slickdeals;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.brunocvcunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.brunocvcunha.taskerbox.impl.email.EmailDelegateAction;
import org.brunocvcunha.taskerbox.impl.toaster.StringToasterAction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.extern.log4j.Log4j;

@Log4j
public class SlickDealsAction extends EmailDelegateAction<Document> {

  private static final String HOST = "http://slickdeals.net";

  private Set<String> alreadyAct = new TreeSet<>();

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
  public void action(final Document entry) {

    post: for (Element el : entry.select("tr[id^=sdpostrow]").select(".threadtitleline")) {

      final String url = el.select("a[id^=thread_title]").attr("href");
      final String postTitle = el.select("a[id^=thread_title]").text();

      if (!this.alreadyAct.contains(postTitle)) {
        this.alreadyAct.add(postTitle);

        if (this.ignored != null) {
          for (String ignoredString : this.ignored) {
            if (postTitle.toLowerCase().contains(ignoredString.toLowerCase())) {
              continue post;
            }
          }
        }


        spreadAction(HOST + url, postTitle);

        try {
          TaskerboxFileUtils.serializeMemory(this, this.alreadyAct);
        } catch (IOException e1) {
          e1.printStackTrace();
        }

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
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

      toasterAction.setTitle("SlickDeals Alert");
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

  public List<String> getIgnored() {
    return this.ignored;
  }

  public void setIgnored(List<String> ignored) {
    this.ignored = ignored;
  }


}
