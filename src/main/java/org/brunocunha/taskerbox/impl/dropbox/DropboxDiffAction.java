package org.brunocunha.taskerbox.impl.dropbox;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.DefaultTaskerboxAction;

import com.dropbox.core.DbxDelta;
import com.dropbox.core.DbxEntry;

@Log4j
public class DropboxDiffAction extends DefaultTaskerboxAction<DbxDelta.Entry<DbxEntry>>{

	@Override
	public void action(DbxDelta.Entry<DbxEntry> entry) {
		logInfo(log, "Detected change: " + entry);
	}

}
