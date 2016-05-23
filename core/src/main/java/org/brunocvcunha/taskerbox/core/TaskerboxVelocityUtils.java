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
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import lombok.extern.log4j.Log4j;

/**
 * Utilities class for Velocity Templates
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxVelocityUtils {

  public static String processTemplate(String template, Properties props) {

    Properties veProps = new Properties();
    veProps.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    veProps.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

    // inicializando o velocity
    VelocityEngine ve = new VelocityEngine(veProps);
    ve.init();

    // criando o contexto que liga o java ao template
    VelocityContext context = new VelocityContext();

    log.debug("Using template " + template);
    Template t = ve.getTemplate(template);

    for (Object prop : props.keySet()) {
      context.put(prop.toString(), props.get(prop));
    }

    StringWriter writer = new StringWriter();
    // mistura o contexto com o template
    t.merge(context, writer);

    writer.flush();

    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return writer.getBuffer().toString();
  }
}
