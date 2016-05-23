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
package org.brunocvcunha.taskerbox.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.brunocvcunha.inutils4j.MyStringUtils;
import org.brunocvcunha.taskerbox.core.utils.TaskerboxFileUtils;
import org.brunocvcunha.taskerbox.gui.TaskerboxControlFrame;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Abstract Class for all Taskerbox Channels
 *
 * @author Bruno Candido Volpato da Cunha
 *
 * @param <T>
 */
@Log4j
@XmlRootElement
public abstract class TaskerboxChannel<T> {

  @NotEmpty
  @Getter
  public String id;

  @Getter
  @Setter
  private boolean daemon;

  @Getter
  @Setter
  private ScheduledExecutorService scheduler;

  @Getter
  @Setter
  protected boolean singleItemAction = true;

  @Getter
  @Setter
  protected List<ITaskerboxAction<T>> actions;

  @Getter
  @Setter
  public Set<String> alreadyPerformed = new TreeSet<>();

  @Getter
  @Setter
  private boolean performActionFirstCheck;

  @Getter
  @Setter
  protected int checkCount;

  @Getter
  @Setter
  private Long every = 60000L;

  @Getter
  @Setter
  private Map<String, String> propertyBag = new LinkedHashMap<>();

  @Getter
  @Setter
  private Map<String, String> storedPropertyBag = new LinkedHashMap<>();

  @Getter
  @Setter
  private ScheduledChecker scheduledCheckerThread;

  @Getter
  @Setter
  private boolean paused;

  @Getter
  private boolean forced;

  @Getter
  private boolean running;

  @Getter
  @Setter
  private long timeout;

  @Getter
  @Setter
  private long lastPerformed;

  @Getter
  @Setter
  private boolean pendingSerializerThread;

  @Getter
  private TaskerboxChannelExecuteThread runningThread;

  @Setter
  private String groupName;

  /**
   * Check if an action for a entry was already called (considering the channel is considered to do
   * so)
   *
   * @param entry
   * @return
   */
  public boolean alreadyPerformedAction(T entry) {
    if (this.singleItemAction) {
      log.debug("Checking if already performed action for " + entry.toString());
    }
    // else {
    // log.debug("Not controlling single items.");
    // return false;
    // }

    if (this.alreadyPerformed.contains(getItemFingerprint(entry).replaceAll("\r?\n", ""))) {
      log.debug("Already performed action for " + getItemFingerprint(entry).replaceAll("\r?\n", ""));
      return true;
    }

    return false;
  }

  /**
   * Add performed action, using entry fingerprint as key
   *
   * @param entry
   */
  public void addAlreadyPerformedAction(T entry) {
    this.lastPerformed = System.currentTimeMillis();

    synchronized (this.alreadyPerformed) {
      this.alreadyPerformed.add(getItemFingerprint(entry).replaceAll("\r?\n", ""));
    }

    if (!this.pendingSerializerThread) {
      this.pendingSerializerThread = true;
      ChannelSerializerThread serializerThread = new ChannelSerializerThread(this);
      serializerThread.start();
    }
  }

  /**
   * Remove entry from already performed
   *
   * @param entry
   */
  public void removeAlreadyPerformedAction(T entry) {
    synchronized (this.alreadyPerformed) {
      this.alreadyPerformed.remove(getItemFingerprint(entry));
    }
  }

  /**
   * Unique performing controller
   *
   * @param entry
   */
  public void performUnique(T entry) {
    if (this.isPaused() && !this.isForced()) {
      return;
    }

    if (!alreadyPerformedAction(entry)) {
      this.perform(entry);
      addAlreadyPerformedAction(entry);
    }
  }

  /**
   * Multiple performing controller
   *
   * @param entry
   */
  public void perform(T entry) {
    if (this.isPaused() && !this.isForced()) {
      return;
    }

    for (ITaskerboxAction<T> action : getActions()) {
      try {
        log.debug("Performing action in " + action.getClass());
        action.action(entry);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Propagate exception for actions
   *
   * @param entry
   */
  public void performException(Throwable entry) {
    if (this.isPaused() && !this.isForced()) {
      return;
    }

    for (ITaskerboxAction<T> action : getActions()) {
      try {
        log.debug("Performing exception in " + action.getClass());
        action.exception(entry);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Creates the scheduler thread for the channel
   *
   * @param initialDelay
   * @param delay
   * @param unit
   */
  public void scheduleTask(long initialDelay, long delay, TimeUnit unit) {
    this.scheduler = Executors.newScheduledThreadPool(1);

    this.scheduledCheckerThread = new ScheduledChecker(this);
    this.scheduledCheckerThread.setName("scheduler-" + getId());

    // was scheduleWithFixedDelay
    this.scheduler.scheduleAtFixedRate(this.scheduledCheckerThread, initialDelay, delay, unit);

  }

  public InputStream getAppResource(String resourceName) {

    InputStream is = getClass().getResourceAsStream("/" + resourceName);
    if (is == null) {
      is = getClass().getResourceAsStream(resourceName);
    }
    if (is == null) {
      is = getClass().getResourceAsStream("/" + resourceName);
    }
    if (is == null) {
      is = getClass().getResourceAsStream("/META-INF/resources/" + resourceName);
    }
    if (is == null) {
      is = getClass().getResourceAsStream("/WEB-INF/classes/" + resourceName);
    }
    if (is == null) {
      is = getClass().getResourceAsStream("/resources/" + resourceName);
    }

    if (is == null) {
      log.error("Not found resource for " + resourceName + " in your application.");
    } else {
      log.info("Stream for '" + resourceName + "' was found!");
    }

    return is;
  }

  /**
   * Gets a {@link List} for the given file in the app
   *
   * @param resourceName
   * @return
   */
  public List<String> listFromResource(String resourceName) {
    List<String> items;
    try {
      items = MyStringUtils.getContentListSplit(getAppResource(resourceName), "\r\n");
      log.info("Returning " + items.size() + " objects on " + resourceName);
      return items;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Main method of channel
   *
   * @throws Exception
   */
  protected abstract void execute() throws Exception;

  /**
   * Runs check without forcing if it is paused
   *
   * @throws Exception
   */
  public void check() throws Exception {
    this.check(false);
  }

  /**
   * Runs check even if the channel is paused (used by UI)
   *
   * @param force
   * @throws Exception
   */
  public final void check(boolean force) throws Exception {
    if (force || !isPaused()) {

      try {
        this.checkCount++;
        this.running = true;

        if (TaskerboxControlFrame.hasFrame()) {
          TaskerboxControlFrame.getInstance().updateChannels();
        }

        this.forced = force;

        this.runningThread = new TaskerboxChannelExecuteThread(this);
        try {
          this.runningThread.start();
          this.runningThread.join();

          if (this.runningThread != null) {
            Exception throwExc = this.runningThread.getException();
            boolean success = this.runningThread.isSuccess();

            this.runningThread = null;

            if (!success) {
              if (throwExc == null) {
                throw new RuntimeException("Thread not ran successfully! " + getId());
              } else {
                throw throwExc;
              }
            }
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      } catch (Exception e) {
        throw e;
      } finally {
        this.running = false;
        if (TaskerboxControlFrame.hasFrame()) {
          TaskerboxControlFrame.getInstance().updateChannels();
        }
      }
    }
  }

  /**
   * Fingerprint implementation - Fingerprint must produce an unique value for different entries
   * because it is used to "already checked" implementation
   *
   * @param entry
   * @return
   */
  protected abstract String getItemFingerprint(T entry);

  /**
   * Default to log.info displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logDebug(Logger logger, String msg) {
    logger.debug("[" + this.getId() + "] - " + msg);
  }

  /**
   * Default to log.info displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logInfo(Logger logger, String msg) {
    logger.info("[" + this.getId() + "] - " + msg);
  }

  /**
   * Default to log.warn displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logWarn(Logger logger, String msg) {
    logger.warn("[" + this.getId() + "] - " + msg);
  }

  /**
   * Default to log.error displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logError(Logger logger, String msg) {
    logger.error("[" + this.getId() + "] - " + msg);
  }

  /**
   * Default to log.error displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logError(Logger logger, String msg, Throwable error) {
    logger.error("[" + this.getId() + "] - " + msg, error);
  }

  /**
   * Set Unique Action to Channel. It creates the list and add the action to collection
   *
   * @param action
   */
  public void setAction(ITaskerboxAction<T> action) {
    if (this.actions == null) {
      this.actions = new ArrayList<>();
    }

    this.actions.add(action);
  }

  /**
   * When the ID is setted, it is possible to import from repository what was performed previously
   * in the channel, avoiding duplicates action performing in case of relaunch.
   *
   * @param id
   */
  public void setId(String id) {
    this.id = id;

    try {
      TaskerboxFileUtils.deserializeMemory(this);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  /**
   * Default implementation to Setup. So the endpoint channels are not forced to implement
   *
   * @throws Exception
   */
  public void setup() throws Exception {}

  /**
   * Add key/value pair to property bag. It is used to work with generic properties (not defined as
   * a member of class)
   *
   * @param key
   * @param value
   */
  public void addProperty(String key, String value) {
    getPropertyBag().put(key, value);
  }

  /**
   * Add key/value pair to stored property bag. It is used to work with generic properties (not
   * defined as a member of class)
   *
   * @param key
   * @param value
   */
  public void addStoredProperty(String key, String value) {
    getStoredPropertyBag().put(key, value);
  }

  /**
   * Delegate method to get property from bag
   *
   * @param key
   * @return
   */
  public String getProperty(String key) {
    return getPropertyBag().get(key);
  }

  /**
   * Delegate method to get property from stored bag
   *
   * @param key
   * @return
   */
  public String getStoredProperty(String key) {
    return getStoredPropertyBag().get(key);
  }

  /**
   * Defaults the display name (name that is shown in UI) to ID
   *
   * @return
   */
  public String getDisplayName() {
    return this.getId();
  }

  /**
   * Gets the Group of the Channel. It is mostly used in case to join channels together in the UI
   *
   * @return
   */
  public String getGroupName() {
    return (this.groupName != null ? this.groupName : this.getClass().getSimpleName().replace("Channel", ""));
  }

}
