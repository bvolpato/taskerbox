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

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;



/**
 * Implements some boilerplate for actions
 *
 * @author Bruno Candido Volpato da Cunha
 *
 * @param <T>
 */
public abstract class DefaultTaskerboxAction<T> implements ITaskerboxAction<T> {

  @Getter
  @Setter
  protected TaskerboxChannel<T> channel;

  @Getter
  @Setter
  public String id;

  @Override
  public void setup() {}

  @Override
  public void exception(Throwable error) {
    error.printStackTrace();
  }


  private String getLabel() {
    if (this.getChannel() == null) {
      return "NoChannel:" + this.getId();
    }
    return this.getChannel().getId() + ":" + this.getId();
  }

  /**
   * Default to log.info displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logInfo(Logger logger, String msg) {
    logger.info("[" + getLabel() + "] - " + msg);
  }

  /**
   * Default to log.warn displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logWarn(Logger logger, String msg) {
    logger.warn("[" + getLabel() + "] - " + msg);
  }

  /**
   * Default to log.error displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logError(Logger logger, String msg) {
    logger.error("[" + getLabel() + "] - " + msg);
  }

  /**
   * Default to log.error displaying the channel id
   *
   * @param logger
   * @param msg
   */
  protected void logError(Logger logger, String msg, Throwable error) {
    logger.error("[" + getLabel() + "] - " + msg, error);
  }


}
