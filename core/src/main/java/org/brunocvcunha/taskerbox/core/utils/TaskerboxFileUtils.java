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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.brunocvcunha.taskerbox.core.ITaskerboxAction;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;

import lombok.extern.log4j.Log4j;

/**
 * File Utilities for Taskerbox
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxFileUtils {

  /**
   * @return Directory for saving working data
   */
  private static File getPerformedDirectory() {
    String dir = System.getProperty("taskerbox.performed.dir");
    if (dir == null) {
      return FileUtils.getTempDirectory();
    }

    return new File(dir);
  }

  /**
   * Get File that stores information for channels in performed dir
   *
   * @param channel
   * @return
   */
  private static File getPerformedFileForChannel(TaskerboxChannel<?> channel) {
    File performedFile =
        new File(getPerformedDirectory(), "channel_" + channel.getId() + ".performed");
    log.debug("Using performed file: " + performedFile.getAbsolutePath());
    return performedFile;
  }

  /**
   * Get File that stores persistent storage information
   *
   * @param channel
   * @return
   */
  private static File getPersistentStorageFileForChannel(TaskerboxChannel<?> channel) {
    File stateFile = new File(getPerformedDirectory(), "state_" + channel.getId() + ".bin");
    log.debug("Using performed file: " + stateFile.getAbsolutePath());
    return stateFile;
  }

  /**
   * Get File that stores information for actions in performed dir
   *
   * @param action
   * @return
   */
  private static File getPerformedFileForAction(ITaskerboxAction<?> action) {
    File performedFile =
        new File(getPerformedDirectory(), "action_" + action.getId() + ".performed");
    log.debug("Using performed file: " + performedFile.getAbsolutePath());
    return performedFile;
  }

  /**
   * Save lines to file
   *
   * @param channel
   * @throws IOException
   */
  public static void serializeMemory(TaskerboxChannel<?> channel) throws IOException {
    log.debug("Serializing history for channel " + channel.getId());

    synchronized (channel.getAlreadyPerformed()) {
      FileWriter out = new FileWriter(getPerformedFileForChannel(channel));
      for (String str : channel.getAlreadyPerformed()) {
        out.write(str.replaceAll("\r?\n", "") + "\r\n");
      }
      out.close();
    }

    if (channel.getStoredPropertyBag() != null && !channel.getStoredPropertyBag().isEmpty()) {
      synchronized (channel.getStoredPropertyBag()) {
        ObjectOutputStream objOut =
            new ObjectOutputStream(
                new FileOutputStream(getPersistentStorageFileForChannel(channel)));
        objOut.writeObject(channel.getStoredPropertyBag());
        objOut.close();
      }

    }
  }

  /**
   * Save lines to file
   *
   * @param action
   * @throws IOException
   */
  public static void serializeMemory(ITaskerboxAction<?> action, Collection<String> values)
      throws IOException {
    log.debug("Serializing history for action " + action.getId());

    FileWriter out = new FileWriter(getPerformedFileForAction(action));
    for (String str : values) {
      out.write(str.replaceAll("\r?\n", "") + "\r\n");
    }
    out.close();
  }

  /**
   * Imports lines to set
   *
   * @param action
   * @throws IOException
   */
  public static Collection<String> deserializeMemory(ITaskerboxAction<?> action) throws IOException {
    if (getPerformedFileForAction(action).exists()) {
      log.debug("Deserializing history for channel " + action.getId());

      FileReader fr = new FileReader(getPerformedFileForAction(action));
      BufferedReader br = new BufferedReader(fr);

      Set<String> alreadyPerformed = new TreeSet<>();
      while (br.ready()) {
        alreadyPerformed.add(br.readLine());
      }
      br.close();

      return alreadyPerformed;
    } else {
      throw new IllegalArgumentException("There's nothing to deserialize for action "
          + action.getId());
    }
  }

  /**
   * Imports lines to set
   *
   * @param channel
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public static void deserializeMemory(TaskerboxChannel<?> channel) throws IOException,
      ClassNotFoundException {
    if (getPerformedFileForChannel(channel).exists()) {
      log.debug("Deserializing history for channel " + channel.getId());

      FileReader fr = new FileReader(getPerformedFileForChannel(channel));
      BufferedReader br = new BufferedReader(fr);

      synchronized (channel.getAlreadyPerformed()) {
        Set<String> alreadyPerformed = channel.getAlreadyPerformed();
        while (br.ready()) {
          alreadyPerformed.add(br.readLine());
        }
        br.close();
      }
    }

    if (getPersistentStorageFileForChannel(channel).exists()) {
      log.debug("Deserializing persistent storage for channel " + channel.getId());

      ObjectInputStream in =
          new ObjectInputStream(new FileInputStream(getPersistentStorageFileForChannel(channel)));
      channel.setStoredPropertyBag((Map<String, String>) in.readObject());
      in.close();
    }

  }

  /**
   * Save temp file in performed dir
   *
   * @param name
   * @param content
   */
  public static void saveTempFile(String name, String content) {
    try {
      FileWriter out = new FileWriter(new File(getPerformedDirectory(), name));
      out.write(content);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
