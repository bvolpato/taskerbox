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
package org.brunocvcunha.taskerbox.impl.feed;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;
import org.brunocvcunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.hibernate.validator.constraints.URL;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.log4j.Log4j;

/**
 * Feed/Atom Input Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class FeedChannel extends TaskerboxChannel<SyndEntryWrapper> {

  @TaskerboxField("Feed URL")
  @Getter
  @Setter
  @URL
  private String feedUrl;

  @TaskerboxField("Filter")
  @Getter
  @Setter
  private String filter;

  @SuppressWarnings("unchecked")
  @Override
  protected void execute() throws IOException, IllegalArgumentException, FeedException,
      IllegalStateException, URISyntaxException {
    logInfo(log, "Checking #" + this.checkCount + "... [" + this.feedUrl + " / '" + this.filter + "']");

    @Cleanup
    XmlReader reader = null;
    HttpEntity responseBody = TaskerboxHttpBox.getInstance().getEntityForURL(this.feedUrl);
    reader = new XmlReader(responseBody.getContent());
    SyndFeed feed = new SyndFeedInput().build(reader);

    List<SyndEntry> list = feed.getEntries();
    for (val entry : list) {

      if (this.filter != null && !entry.getTitle().contains(this.filter)) {
        continue;
      }

      performUnique(new SyndEntryWrapper(entry));

    }
  }

  @Override
public String getItemFingerprint(SyndEntryWrapper entry) {
    return entry.getValue().getUri();
  }


}
