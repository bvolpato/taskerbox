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
package com.buscape.developer;

import com.buscape.developer.result.ResultFormat;
import com.buscape.developer.result.parser.AbstractResultParser;
import com.buscape.developer.result.parser.JsonResultParser;
import com.buscape.developer.result.parser.XmlResultParser;

/**
 * Factory of objects used by the API wrapper
 *
 * @author neto
 */
final class BuscapeFactory {

  /**
   * Creates an instance of {@link AbstractResultParser} used to parse, from chosen format, results
   * from requests.
   *
   * @param data raw data, in chosen format, that will be parsed.
   * @param format the format of data.
   * @return an instance equivalent to format chosen.
   */
  public AbstractResultParser createParser(String data, ResultFormat format) {
    AbstractResultParser builder = null;

    switch (format) {
      case JSON:
        builder = JsonResultParser.createInstance(data);
        break;
      case XML:
        builder = XmlResultParser.createInstance(data);
    }

    return builder;
  }
}
