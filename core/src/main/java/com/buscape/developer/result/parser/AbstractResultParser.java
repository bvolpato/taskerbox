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
package com.buscape.developer.result.parser;

import com.buscape.developer.result.type.Result;


public abstract class AbstractResultParser {

  protected Result result;
  protected final String data;

  /**
   * Default constructor that indicates the raw data of parser.
   *
   * @param data the raw data that will be parsed.
   */
  public AbstractResultParser(String data) {
    this.data = data;
  }

  /**
   * Parses the raw data of a single API call into a {@link Result} object and return it.
   *
   * @return a {@link Result} object populated with information of API call.
   */
  public abstract Result getResult();
}
