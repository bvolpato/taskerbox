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
package org.brunocvcunha.taskerbox.impl.twitter;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.brunocvcunha.taskerbox.core.DefaultTaskerboxAction;
import org.brunocvcunha.taskerbox.core.TaskerboxConstants;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxTrayUtils;

import lombok.extern.log4j.Log4j;

/**
 * Action that shows Tweets in a Toaster Popup
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TwitterToasterAction extends DefaultTaskerboxAction<StatusWrapper> {

  private static long toasterTime = 3000L;

  @Override
  public void action(final StatusWrapper entry) {
    if (!GraphicsEnvironment.isHeadless()) {
      TaskerboxTrayUtils.displayMessage(TaskerboxConstants.TITLE, "@"
          + entry.getValue().getUser().getScreenName() + ": " + entry.getValue().getText(),
          MessageType.INFO, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              try {
                Desktop.getDesktop().browse(
                    new URI("http://twitter.com/" + entry.getValue().getUser().getScreenName()
                        + "/status/" + entry.getValue().getId()));
              } catch (IOException e1) {
                e1.printStackTrace();
              } catch (URISyntaxException e1) {
                e1.printStackTrace();
              }
            }
          });

      try {
        Thread.sleep(toasterTime);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }

  }

}
