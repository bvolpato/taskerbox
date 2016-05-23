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



/**
 * Default Interface for Actions
 *
 * @author Bruno Candido Volpato da Cunha
 *
 * @param <T> The Action Input Class
 */
public interface ITaskerboxAction<T> {

  /**
   * Default join method for actions
   *
   * @param input
   */
  public void action(T input);

  /**
   * Default exception method for actions
   *
   * @param input
   */
  public void exception(Throwable input);

  /**
   * Method used to initial setup of action
   */
  public void setup();

  /**
   * Ties a channel into an Action
   *
   * @param channel
   */
  public void setChannel(TaskerboxChannel<T> channel);

  /**
   * Returns the ID for action
   *
   * @return
   */
  public String getId();

  /**
   * Sets the ID for action
   *
   * @param id
   */
  public void setId(String id);

}
