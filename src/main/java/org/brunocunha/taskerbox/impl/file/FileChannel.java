package org.brunocunha.taskerbox.impl.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.apache.commons.codec.digest.DigestUtils;
import org.brunocunha.taskerbox.core.ITaskerboxAction;
import org.brunocunha.taskerbox.core.TaskerboxChannel;

/**
 * File/Directory Input Channel
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class FileChannel extends TaskerboxChannel<File> {

	@Getter @Setter
	private Set<String> alreadyChecked = new TreeSet<String>();

	@NotNull
	@Getter @Setter
	private File file;

	@Getter @Setter
	private boolean lookChildren;

	@Getter @Setter
	private boolean recursive;

	@Getter @Setter
	private boolean deleteAfterAction;

	@Override
	protected void execute() {
		logInfo(log, "Checking... [" + file + " / " + lookChildren + "]");

		if (file == null) {
			return;
		}

		if (lookChildren) {
			for (File child : file.listFiles()) {
				doAction(child);
			}
		} else {
			doAction(file);
		}

	}

	private void doAction(File file) {

		if (!singleItemAction || (singleItemAction && !alreadyPerformedAction(file))) {
			log.debug("doAction for " + file + ", singleItemAction? " + singleItemAction);
			for (ITaskerboxAction<File> action : this.getActions()) {
				action.action(file);
			}

			addAlreadyPerformedAction(file);

			if (deleteAfterAction) {
				log.debug("Deleting " + file);
				removeAlreadyPerformedAction(file);

				if (!file.delete() && file.exists()) {
					logWarn(log, "Failure deleting " + file + "... Marking to delete on exit.");
					file.deleteOnExit();
				}
			}
		}
	};

	@Override
	public String getItemFingerprint(File file) {
		if (file.isFile()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				String md5Hex = new String(DigestUtils.md5Hex(fis));

				log.debug("Calculated MD5 Hash for File '" + file + "': " + md5Hex);

				fis.close();

				// System.out.println(file.getAbsolutePath() + "::" + md5Hex);
				return file.getAbsolutePath() + "::" + md5Hex;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file.getAbsolutePath();
	}

}
