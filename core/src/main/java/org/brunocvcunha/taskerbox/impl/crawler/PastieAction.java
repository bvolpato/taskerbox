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
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.extern.log4j.Log4j;

@Log4j
public class PastieAction extends CrawlerAction {

  private static final String DOWNLOAD_URL = "http://pastie.org/pastes/{id}/download";

  private static long FETCH_INTERVAL = 2000L;

  @Override
  public void action(final Document entry) {

    log.debug("Validating " + entry.title());

    for (Element el : entry.select(".pastePreview")) {
      final String id = el.select("a").attr("href").replace("http://pastie.org/pastes/", "");
      String code = el.select("pre").text().replaceAll("\r?\n", " ");
      if (code.length() > 32) {
        code = code.substring(0, 32);
      }

      final String title = id + " - " + code;

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
      log.debug("Getting " + DOWNLOAD_URL.replace("{id}", id));

      HttpResponse response =
          TaskerboxHttpBox.getInstance().getResponseForURL(DOWNLOAD_URL.replace("{id}", id));

      String content = TaskerboxHttpBox.getInstance().readResponseFromEntity(response.getEntity());

      if (isConsiderable(id, content)) {
        if (isValid(id, content)) {
          logInfo(log, "[+] Bound: [" + postTitle + "]");
          doValid("pastie_" + id, content);
        } else {
          log.debug("[-] Not Bound: [" + postTitle + "]");
          doInvalid("pastie_" + id, content);
        }
      }

    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

}
