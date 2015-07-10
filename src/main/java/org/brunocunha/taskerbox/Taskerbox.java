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
package org.brunocunha.taskerbox;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxXmlReader;
import org.brunocunha.taskerbox.gui.TaskerboxControlFrame;

/**
 * Classe principal do Taskerbox
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class Taskerbox {
	static { 
		//System.setProperty("java.awt.headless", "false");
	}

	@Getter @Setter
	private Map<String, String> macros = new LinkedHashMap<String, String>();
	@Getter @Setter
	private Map<String, List<?>> macroAttrs = new LinkedHashMap<String, List<?>>();
	@Getter @Setter
	private Map<String, String> defaultProperties = new LinkedHashMap<String, String>();

	@Getter @Setter
	private TaskerboxControlFrame frame;

	@Getter @Setter
	private List<TaskerboxChannel<?>> daemons = new ArrayList<TaskerboxChannel<?>>();
	
	@Getter @Setter
	private List<TaskerboxChannel<?>> channels = new ArrayList<TaskerboxChannel<?>>();

	public static void main(String[] args) throws Exception {
		// LogManager.getRootLogger().setLevel(Level.DEBUG);
		log.info("Inicializando Taskerbox...");
		
		Taskerbox tasker = new Taskerbox();
		tasker.handleTaskerbox("macros.xml");
		
		if (!GraphicsEnvironment.isHeadless()) {
			TaskerboxControlFrame frame = TaskerboxControlFrame.buildInstance(tasker);
			tasker.setFrame(frame);
			frame.setVisible(true);
		}
		
		if (args.length == 0) {
	
			String hostName = InetAddress.getLocalHost().getHostName();
			log.info("Host name: " + hostName);
	
			tasker.handleTaskerbox("local-taskerbox-" + hostName + ".xml");
			tasker.handleTaskerbox("local-taskerbox.xml");
			
			//tasker.handleTaskerbox("taskerbox-tst.xml");
		} else {
			for (String arg : args) {
				tasker.handleTaskerbox(arg);
			}
		}
	}

	public void handleTaskerbox(String file) throws Exception {
		File localFile = new File(file);
		
		InputStream resourceIn;
		if (localFile.exists()) {
			resourceIn = new FileInputStream(localFile);
		} else {
			resourceIn = Taskerbox.class.getResourceAsStream("/" + file);
		}
		
		if (resourceIn != null) {
			log.info("Handling " + file);
			new TaskerboxXmlReader(this).handleTaskerboxStream(resourceIn);
		} else {
			log.warn("Not found resource for file " + file);
		}
	}
	
	public void handleTaskerbox(File xmlFile) throws Exception {
		new TaskerboxXmlReader(this).handleTaskerboxStream(new FileInputStream(xmlFile));
	}
	
}
