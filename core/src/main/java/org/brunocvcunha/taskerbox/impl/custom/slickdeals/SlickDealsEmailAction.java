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

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.brunocvcunha.taskerbox.impl.email.EmailAction;
import org.brunocvcunha.taskerbox.impl.email.EmailValueVO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.extern.log4j.Log4j;

@Log4j
public class SlickDealsEmailAction extends SlickDealsAction {

  @Override
  public void spreadAction(final String url, String postTitle) {
    EmailAction email = getEmailAction();

    EmailValueVO emailVO = new EmailValueVO();
    StringBuffer sb = new StringBuffer();
    sb.append(url);

    emailVO.setTitle("SlickDeals - " + postTitle);

    try {
      Document doc = TaskerboxHttpBox.getInstance().getDocumentForURL(url);

      for (Element post : doc.select(".post_message")) {
        sb.append("<br>");
        sb.append(post.html());
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

    emailVO.setBody(sb.toString());

    email.action(emailVO);

  }


}
