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
package org.brunocunha.taskerbox.impl.whatsapp;

import lombok.extern.log4j.Log4j;
import net.sumppen.whatsapi4j.EventManager;
import net.sumppen.whatsapi4j.MessageProcessor;
import net.sumppen.whatsapi4j.WhatsApi;
import net.sumppen.whatsapi4j.example.ExampleEventManager;
import net.sumppen.whatsapi4j.example.ExampleMessageProcessor;

@Log4j
public class WhatsappConnection {

  private static WhatsApi instance;

  public static WhatsApi getOrCreateInstance(String from, String appName, String alias,
      String password) {
    if (instance == null) {
      try {
        log.info("Creating WhatsApp Instance...");
        instance = new WhatsApi(from, appName, alias);
        EventManager eventManager = new ExampleEventManager();
        instance.setEventManager(eventManager);
        MessageProcessor mp = new ExampleMessageProcessor();
        instance.setNewMessageBind(mp);

        Thread.sleep(1000L); // Some delay

        log.info("Connecting...");
        if (!instance.connect()) {
          throw new RuntimeException("Could not connect to WhatsApp.");
        }

        Thread.sleep(1000L); // Some delay

        log.info("Logging in...");
        instance.loginWithPassword(password);

        Thread.sleep(1000L); // Some delay

      } catch (NoSuchMethodError e) {
        log.error("Error connecting in WhatsApp", e);
        // instance = null;
      } catch (Exception e) {
        log.error("Error connecting in WhatsApp", e);
        instance = null;
      }
    }

    return instance;
  }

}
