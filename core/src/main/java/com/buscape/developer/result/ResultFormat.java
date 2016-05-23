/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.buscape.developer.result;

import com.buscape.developer.util.Messages;

/**
 * Format of API call response.
 *
 * @author neto
 */
public enum ResultFormat {
  /**
   * JSON format
   */
  JSON(Messages.getString("ResultFormat.json")), //$NON-NLS-1$

  /**
   * XML format
   */
  XML(Messages.getString("ResultFormat.xml")); //$NON-NLS-1$


  private String format;

  private ResultFormat(String format) {
    this.format = format;
  }

  @Override
  public String toString() {
    return this.format;
  }

  /**
   * Returns a instance equivalent to the value.
   *
   * @param value the value of {@link ResultFormat}.
   * @return a {@link ResultFormat} equivalent to value.
   */
  public static ResultFormat fromString(String value) {
    if (Messages.getString("ResultFormat.json").equals(value)) { //$NON-NLS-1$
      return JSON;
    } else if (Messages.getString("ResultFormat.xml").equals(value)) { //$NON-NLS-1$
      return XML;
    } else {
      throw new IllegalArgumentException(String.format(
          "The string '%s' is not a valid result format.", value)); //$NON-NLS-1$
    }
  }
}
