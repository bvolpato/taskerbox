package org.brunocunha.taskerbox.core;

import java.awt.GraphicsEnvironment;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.utils.TaskerboxReflectionUtils;
import org.brunocunha.taskerbox.core.utils.validation.TaskerboxValidationUtils;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

/**
 * Class that centralizes the creation of channels and actions, based on XML.
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
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TaskerboxChannel<?> buildChannel(Element xmlChannel)
			throws Exception {
		
		log.info("Building channel with Class " + xmlChannel.getName());
		
		Class<TaskerboxChannel> channelClass = (Class<TaskerboxChannel>) Class
				.forName(xmlChannel.getName());
		final TaskerboxChannel channel = channelClass.newInstance();

		for (Object attrObj : xmlChannel.attributes()) {
			DefaultAttribute attrib = (DefaultAttribute) attrObj;

			setObjectFieldValue(channel, attrib.getName(), attrib.getValue());
			log.debug("Adding Property in bag: " + attrib.getName() + " = "
					+ attrib.getValue());
			channel.addProperty(attrib.getName(), attrib.getValue());

		}

		List<ITaskerboxAction> actions = new ArrayList<ITaskerboxAction>();
		for (Element channelChildren : (List<Element>) xmlChannel.elements()) {
			Class<? extends ITaskerboxAction> actionClass = (Class<? extends ITaskerboxAction>) Class
					.forName(channelChildren.getName());
			ITaskerboxAction action = TaskerboxFactory.createElementInstance(actionClass, channelChildren);

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
			throw new IllegalArgumentException(
					"Not defined actions for channel " + channel.getId());
		}

		channel.setActions(actions);

		TaskerboxValidationUtils.validate(channel);

		return channel;
	}
	
	public static void setObjectFieldValue(Object obj, String var, String value) {

		Method setter;
		try {
			log.debug("Finding setter for: " + var);

			if (var.equalsIgnoreCase("paused") && GraphicsEnvironment.isHeadless()) {
				log.info("Ignoring paused for headless running");
				return;
			}
			
			setter = new PropertyDescriptor(var, obj.getClass())
					.getWriteMethod();

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
