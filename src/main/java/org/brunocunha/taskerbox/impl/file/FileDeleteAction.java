package org.brunocunha.taskerbox.impl.file;

import java.io.File;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;

/**
 * Action that deletes files when detected
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class FileDeleteAction extends DefaultTaskerboxAction<File> {

	@Override
	public void action(File file) {
		logInfo(log, "Deleting file " + file.getAbsolutePath());
		
		boolean result = file.delete();
		log.debug("Delete file result: " + result);
	}

}
