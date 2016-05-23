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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.brunocvcunha.inutils4j.MyNumberUtils;

import lombok.extern.log4j.Log4j;

/**
 * Reflection utilities for Taskerbox. Centralizes values from XML to object setter, casting when it
 * is needed
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxReflectionUtils {

  public static void invokeSetter(Method setter, Object obj, String value)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      ClassNotFoundException {
    if (setter.getParameterTypes()[0] == String.class) {
      log.debug("Invoking String setter");
      setter.invoke(obj, value);
    } else if (setter.getParameterTypes()[0] == File.class) {
      log.debug("Invoking File setter");
      setter.invoke(obj, new File(value));
    } else if (setter.getParameterTypes()[0] == Boolean.class
        || setter.getParameterTypes()[0] == boolean.class) {
      log.debug("Invoking Boolean setter");
      setter.invoke(obj, Boolean.valueOf(value));
    } else if (setter.getParameterTypes()[0] == Integer.class
        || setter.getParameterTypes()[0] == int.class) {

      log.debug("Invoking Integer setter");

      if (value.contains("-")) {
        int randomMin = Integer.valueOf(value.split("-")[0]).intValue();
        int randomMax = Integer.valueOf(value.split("-")[1]).intValue();

        int randomValue = MyNumberUtils.randomIntBetween(randomMin, randomMax);
        log.info("Setting random value: " + randomValue);

        setter.invoke(obj, randomValue);
      } else {
        setter.invoke(obj, Integer.valueOf(value));
      }

    } else if (setter.getParameterTypes()[0] == Double.class
        || setter.getParameterTypes()[0] == double.class) {
      log.debug("Invoking Double setter");
      setter.invoke(obj, Double.valueOf(value));
    } else if (setter.getParameterTypes()[0] == Long.class
        || setter.getParameterTypes()[0] == long.class) {
      log.debug("Invoking Long setter");

      if (value.contains("-")) {
        long randomMin = Long.valueOf(value.split("-")[0]);
        long randomMax = Long.valueOf(value.split("-")[1]);

        long randomValue = MyNumberUtils.randomLongBetween(randomMin, randomMax);
        log.info("Setting random value: " + randomValue);

        setter.invoke(obj, randomValue);
      } else {
        setter.invoke(obj, Long.valueOf(value));
      }

    } else if (setter.getParameterTypes()[0] == Class.class) {
      log.debug("Invoking Class setter");
      setter.invoke(obj, Class.forName(value));
    } else if (setter.getParameterTypes()[0] == List.class) {
      log.debug("Invoking List setter");
      setter.invoke(obj, Arrays.asList(value.split(",")));
    } else if (setter.getParameterTypes()[0] == String[].class) {
      log.debug("Invoking String Array setter");
      setter.invoke(obj, new Object[] {value.split(",")});
    } else {
      throw new RuntimeException("NOT FOUND ATTRIBUTE CAST FOR " + setter.getName() + " - "
          + setter.getParameterTypes()[0]);
    }
  }
}
