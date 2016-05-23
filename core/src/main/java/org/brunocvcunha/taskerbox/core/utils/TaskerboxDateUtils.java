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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utilities for Taskerbox
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class TaskerboxDateUtils {
  /**
   * @return Current Timestamp
   */
  public static String getTimestamp() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    return "[".concat(sdf.format(new Date())).concat("]");
  }
}
