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

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Implementation of AbstractResultParser that parses data in XML format to Result.
 *
 * @author cartagena
 */
public final class XmlResultParser extends AbstractResultParser {

  private XmlResultParser(String data) {
    super(data);
  }

  /**
   * Creates an instance of {@link XmlResultParser} with provided data.
   *
   * @param data the raw data, in XML, that will be parsed.
   * @return a new instance of {@link XmlResultParser},
   */
  public static AbstractResultParser createInstance(String data) {
    return new XmlResultParser(data);
  }

  @Override
  public Result getResult() {

    try {
      JAXBContext jc = JAXBContext.newInstance("com.buscape.developer.result.type"); //$NON-NLS-1$
      Unmarshaller unmarshaller = jc.createUnmarshaller();

      Result result =
          (Result) unmarshaller.unmarshal(new ByteArrayInputStream(this.data.getBytes()));

      return result;
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    return null;
  }


}
