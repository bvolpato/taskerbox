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
package org.brunocvcunha.taskerbox.core.utils;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import org.brunocvcunha.taskerbox.core.TaskerboxConstants;

import lombok.extern.log4j.Log4j;

/**
 * System Tray integration with Taskerbox
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxTrayUtils {

  /**
	 *
	 */
  private static TrayIcon trayIconInstance;

  /**
   * Gets the {@link TrayIcon} reference
   *
   * @return
   */
  public static TrayIcon getTrayIcon() {
    if (trayIconInstance == null) {

      SystemTray tray = SystemTray.getSystemTray();
      Image image =
          Toolkit.getDefaultToolkit().getImage(TaskerboxTrayUtils.class.getResource("/tasker.png"));

      trayIconInstance = new TrayIcon(image, TaskerboxConstants.APP_NAME);
      try {
        tray.add(trayIconInstance);
      } catch (AWTException e) {
        e.printStackTrace();
      }
    }

    return trayIconInstance;
  }

  /**
   * Displays a message at the system tray
   *
   * @param caption
   * @param text
   * @param messageType
   * @param listener
   */
  public static void displayMessage(String caption, String text, MessageType messageType,
      ActionListener listener) {

    log.info(messageType + ": " + text);

    if (SystemTray.isSupported()) {

      removeAllListeners();
      if (listener != null) {
        getTrayIcon().addActionListener(listener);
      }

      getTrayIcon().displayMessage(caption, text, messageType);
    }
  }

  /**
   * Remove tray listeners
   */
  public static void removeAllListeners() {
    for (ActionListener listener : getTrayIcon().getActionListeners()) {
      getTrayIcon().removeActionListener(listener);
    }
  }
}
