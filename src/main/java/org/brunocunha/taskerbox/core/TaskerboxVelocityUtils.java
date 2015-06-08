package org.brunocunha.taskerbox.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Utilities class for Velocity Templates
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxVelocityUtils {

	public static String processTemplate(String template, Properties props) {

		Properties veProps = new Properties();
		veProps.setProperty(Velocity.RESOURCE_LOADER, "classpath");
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
