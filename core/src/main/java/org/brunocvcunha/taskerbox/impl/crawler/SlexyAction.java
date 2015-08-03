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

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.extern.log4j.Log4j;

@Log4j
public class SlexyAction extends CrawlerAction {

  private static final String URL_DOWNLOAD = "http://slexy.org/raw/{id}";

  private static long FETCH_INTERVAL = 1000L;

  @Override
  public void action(final Document entry) {

    log.debug("Validating " + entry.title());

    for (Element el : entry.select(".main").select("a")) {
      final String id = el.attr("href").replace("/view/", "");

      final String title = id;

      if (canAct(id)) {
        addAct(id);

        spreadAction(id, title);
        serializeAlreadyAct();
        sleep(FETCH_INTERVAL);
      }

    }

  }

  public void spreadAction(final String id, String postTitle) {

    try {
      log.debug("Getting " + URL_DOWNLOAD.replace("{id}", id));

      HttpGet get = new HttpGet(URL_DOWNLOAD.replace("{id}", id));
      get.addHeader("Referer", "http://slexy.org/recent");

      HttpResponse response = TaskerboxHttpBox.getInstance().getHttpClient().execute(get);

      String content = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());

      if (isConsiderable(id, content)) {
        if (isValid(id, content)) {
          logInfo(log, "[+] Bound: [" + postTitle + "]");
          doValid("slexy_" + id, content);
        } else {
          logInfo(log, "[-] Not Bound: [" + postTitle + "]");
          doInvalid("slexy_" + id, content);
        }
      }

    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
