package org.brunocunha.taskerbox.core.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lombok.extern.log4j.Log4j;

/**
 * Reflection utilities for Taskerbox. Centralizes values from XML to object
 * setter, casting when it is needed
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxReflectionUtils {

	public static void invokeSetter(Method setter, Object obj, String value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		if (setter.getParameterTypes()[0] == String.class) {
			log.debug("Invoking String setter");
			setter.invoke(obj, value);
		} else if (setter.getParameterTypes()[0] == File.class) {
			log.debug("Invoking File setter");
			setter.invoke(obj, new File(value));
		} else if (setter.getParameterTypes()[0] == Boolean.class || setter.getParameterTypes()[0] == boolean.class) {
			log.debug("Invoking Boolean setter");
			setter.invoke(obj, Boolean.valueOf(value));
		} else if (setter.getParameterTypes()[0] == Integer.class || setter.getParameterTypes()[0] == int.class) {

			log.debug("Invoking Integer setter");
			setter.invoke(obj, Integer.valueOf(value));
		} else if (setter.getParameterTypes()[0] == Double.class || setter.getParameterTypes()[0] == double.class) {
			log.debug("Invoking Double setter");
			setter.invoke(obj, Double.valueOf(value));
		} else if (setter.getParameterTypes()[0] == Long.class || setter.getParameterTypes()[0] == long.class) {
			log.debug("Invoking Long setter");
			setter.invoke(obj, Long.valueOf(value));
		} else if (setter.getParameterTypes()[0] == Class.class) {
			log.debug("Invoking Class setter");
			setter.invoke(obj, Class.forName(value));
		} else if (setter.getParameterTypes()[0] == List.class) {
			log.debug("Invoking List setter");
			setter.invoke(obj, Arrays.asList(value.split(",")));
		} else if (setter.getParameterTypes()[0] == String[].class) {
			log.debug("Invoking String Array setter");
			setter.invoke(obj, new Object[] { value.split(",") });
		} else {
			throw new RuntimeException("NOT FOUND ATTRIBUTE CAST FOR " + setter.getName() + " - "
					+ setter.getParameterTypes()[0]);
		}
	}
}
