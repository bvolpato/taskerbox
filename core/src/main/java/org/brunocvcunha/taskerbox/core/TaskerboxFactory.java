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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.brunocvcunha.taskerbox.core.utils.TaskerboxReflectionUtils;
import org.brunocvcunha.taskerbox.core.utils.validation.TaskerboxValidationUtils;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import lombok.extern.log4j.Log4j;

/**
 * Class that centralizes the creation of channels and actions, based on XML.
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxFactory {

  public static <I> I createElementInstance(Class<? extends I> clazz, Element el)
      throws InstantiationException, IllegalAccessException {
    I instance = clazz.newInstance();

    for (Object attrObj : el.attributes()) {
      DefaultAttribute attrib = (DefaultAttribute) attrObj;
      setObjectFieldValue(instance, attrib.getName(), attrib.getValue());
    }

    return instance;
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  public static TaskerboxChannel<?> buildChannel(Element xmlChannel) throws Exception {

    log.info("Building channel with Class " + xmlChannel.getName());

    Class<TaskerboxChannel> channelClass =
        (Class<TaskerboxChannel>) Class.forName(xmlChannel.getName());
    final TaskerboxChannel channel = channelClass.newInstance();

    for (Object attrObj : xmlChannel.attributes()) {
      DefaultAttribute attrib = (DefaultAttribute) attrObj;

      setObjectFieldValue(channel, attrib.getName(), attrib.getValue());
      log.debug("Adding Property in bag: " + attrib.getName() + " = " + attrib.getValue());
      channel.addProperty(attrib.getName(), attrib.getValue());

    }

    List<ITaskerboxAction> actions = new ArrayList<>();
    for (Element channelChildren : (List<Element>) xmlChannel.elements()) {
      Class<? extends ITaskerboxAction> actionClass =
          (Class<? extends ITaskerboxAction>) Class.forName(channelChildren.getName());
      ITaskerboxAction action =
          TaskerboxFactory.createElementInstance(actionClass, channelChildren);

      try {
        log.info("Validando Action " + action.getClass());
        TaskerboxValidationUtils.validate(action);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        log.error("Erro ao validar action", e);
        continue;
      }

      action.setChannel(channel);
      if (action.getId() == null) {
        action.setId(channel.getId() + "Action");
      }
      action.setup();
      actions.add(action);
    }

    if (actions == null || actions.isEmpty()) {
      throw new IllegalArgumentException("Not defined actions for channel " + channel.getId());
    }

    channel.setActions(actions);

    TaskerboxValidationUtils.validate(channel);

    return channel;
  }

  public static void setObjectFieldValue(Object obj, String var, String value) {

    Method setter;
    try {
      log.debug("Finding setter for: " + var);

      //now that we have web ui, we shouldn't ignore pause for headless

      setter = new PropertyDescriptor(var, obj.getClass()).getWriteMethod();

      log.debug("Setter: " + setter.getName());

      TaskerboxReflectionUtils.invokeSetter(setter, obj, value);

    } catch (IntrospectionException e) {
      log.error("IntrospectionException on " + obj.getClass(), e);
    } catch (InvocationTargetException e) {
      log.error("InvocationTargetException on " + obj.getClass(), e);
    } catch (Exception e) {
      log.error("Exception on " + obj.getClass(), e);
    }

  }
}
