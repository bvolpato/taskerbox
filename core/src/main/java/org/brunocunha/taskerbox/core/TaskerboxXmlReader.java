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
package org.brunocunha.taskerbox.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringEscapeUtils;
import org.brunocunha.taskerbox.Taskerbox;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

/**
 * Centralized taskerbox*.xml Files Parsing
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxXmlReader {

  private Taskerbox tasker;

  public TaskerboxXmlReader(Taskerbox tasker) {
    super();
    this.tasker = tasker;
  }

  @SuppressWarnings("unchecked")
  public void handleTaskerboxStream(InputStream stream) throws Exception {

    Document dom = new SAXReader().read(stream);

    for (Element xmlChannel : (List<Element>) dom.getRootElement().elements()) {
      try {
        if (xmlChannel.getName().equalsIgnoreCase("defaultProperties")) {
          handleDefaultPropertiesNode(xmlChannel);
        } else if (xmlChannel.getName().equalsIgnoreCase("systemProperties")) {
          handleSystemPropertiesNode(xmlChannel);
        } else if (xmlChannel.getName().equalsIgnoreCase("macros")) {
          handleMacrosNode(xmlChannel);
        } else if (xmlChannel.getName().equalsIgnoreCase("macro")) {
          handleMacroUsageNode(xmlChannel);
        } else {
          handleConcreteChannelNode(xmlChannel);
        }
      } catch (Exception e) {
        log.error("Exception Handling node", e);
      }
    }

  }

  private void handleConcreteChannelNode(Element xmlChannel) throws Exception {
    log.info("Found channel: " + xmlChannel.getName() + ": " + xmlChannel.toString());

    StringWriter sw = new StringWriter();
    XMLWriter writer = new XMLWriter(sw);
    writer.write(xmlChannel);

    String macroElement = sw.toString();

    for (String defaultAttr : tasker.getDefaultProperties().keySet()) {
      macroElement =
          macroElement.replace("{" + defaultAttr + "}",
              tasker.getDefaultProperties().get(defaultAttr));
    }
    for (String defaultAttr : tasker.getDefaultProperties().keySet()) {
      macroElement =
          macroElement.replace("default-" + defaultAttr + "=\"\"", defaultAttr + "=\""
              + StringEscapeUtils.escapeXml(tasker.getDefaultProperties().get(defaultAttr)) + "\"");
    }

    log.debug("Creating channel: " + macroElement);
    Element replacedXmlChannel =
        new SAXReader().read(new StringReader(macroElement)).getRootElement();

    TaskerboxChannel<?> channel = TaskerboxFactory.buildChannel(replacedXmlChannel);
    TaskerboxLauncher.startChannel(channel, tasker.getFrame(), tasker.getDaemons(),
        tasker.getChannels());

  }

  private void handleMacroUsageNode(Element xmlChannel) throws Exception {

    String macroName = xmlChannel.attributeValue("name");
    log.debug("Found macro! " + macroName);

    if (!tasker.getMacros().containsKey(macroName)) {
      throw new IllegalArgumentException("Using unexistent macro " + macroName);
    }

    String macroElement = tasker.getMacros().get(macroName);

    for (Object attrObj : xmlChannel.attributes()) {
      DefaultAttribute a = (DefaultAttribute) attrObj;
      macroElement = macroElement.replace("{" + a.getName() + "}", a.getValue());
    }

    for (Object attrMacro : tasker.getMacroAttrs().get(macroName)) {
      DefaultAttribute a = (DefaultAttribute) attrMacro;
      macroElement = macroElement.replace("{" + a.getName() + "}", a.getValue());
    }

    for (String defaultAttr : tasker.getDefaultProperties().keySet()) {
      macroElement =
          macroElement.replace("{" + defaultAttr + "}",
              tasker.getDefaultProperties().get(defaultAttr));
    }
    for (String defaultAttr : tasker.getDefaultProperties().keySet()) {
      macroElement =
          macroElement.replace("default-" + defaultAttr + "=\"\"", defaultAttr + "=\""
              + StringEscapeUtils.escapeXml(tasker.getDefaultProperties().get(defaultAttr)) + "\"");
    }

    log.debug("Creating Macro channel: " + macroElement);

    Element el = new SAXReader().read(new StringReader(macroElement)).getRootElement();

    TaskerboxChannel<?> channel = TaskerboxFactory.buildChannel(el);
    TaskerboxLauncher.startChannel(channel, tasker.getFrame(), tasker.getDaemons(),
        tasker.getChannels());

  }

  private void handleMacrosNode(Element xmlChannel) throws IOException {

    for (Object attrObj : xmlChannel.elements()) {
      DefaultElement e = (DefaultElement) attrObj;

      StringWriter sw = new StringWriter();
      XMLWriter writer = new XMLWriter(sw);
      writer.write(e.elements());

      if (tasker.getMacros().containsKey(e.attributeValue("name"))) {
        throw new RuntimeException("Macro " + e.attributeValue("name") + " already exists in map.");
      }

      tasker.getMacros().put(e.attributeValue("name"), sw.toString());
      tasker.getMacroAttrs().put(e.attributeValue("name"), e.attributes());
    }

  }

  private void handleSystemPropertiesNode(Element xmlChannel) {
    for (Object attrObj : xmlChannel.elements()) {
      DefaultElement e = (DefaultElement) attrObj;
      System.setProperty(e.attributeValue("name"), e.attributeValue("value"));
      handleDefaultPropertiesNode(xmlChannel);
    }
  }

  private void handleDefaultPropertiesNode(Element xmlChannel) {
    for (Object attrObj : xmlChannel.elements()) {
      DefaultElement e = (DefaultElement) attrObj;

      String propertyValue = e.attributeValue("value");

      for (String defaultAttr : tasker.getDefaultProperties().keySet()) {
        propertyValue =
            propertyValue.replace("{" + defaultAttr + "}",
                tasker.getDefaultProperties().get(defaultAttr));
      }

      tasker.getDefaultProperties().put(e.attributeValue("name"), propertyValue);

    }
  }

}
