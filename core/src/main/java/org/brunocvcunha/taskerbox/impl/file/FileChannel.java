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
package org.brunocvcunha.taskerbox.impl.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.brunocvcunha.taskerbox.core.ITaskerboxAction;
import org.brunocvcunha.taskerbox.core.TaskerboxChannel;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * File/Directory Input Channel
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class FileChannel extends TaskerboxChannel<File> {

  @Getter
  @Setter
  private Set<String> alreadyChecked = new TreeSet<>();

  @NotNull
  @Getter
  @Setter
  private File file;

  @Getter
  @Setter
  private boolean lookChildren;

  @Getter
  @Setter
  private boolean recursive;

  @Getter
  @Setter
  private boolean deleteAfterAction;

  @Override
  protected void execute() {
    logInfo(log, "Checking... [" + this.file + " / " + this.lookChildren + "]");

    if (this.file == null) {
      return;
    }

    if (this.lookChildren) {
      for (File child : this.file.listFiles()) {
        doAction(child);
      }
    } else {
      doAction(this.file);
    }

  }

  private void doAction(File file) {

    if (!this.singleItemAction || (this.singleItemAction && !alreadyPerformedAction(file))) {
      log.debug("doAction for " + file + ", singleItemAction? " + this.singleItemAction);
      for (ITaskerboxAction<File> action : this.getActions()) {
        action.action(file);
      }

      addAlreadyPerformedAction(file);

      if (this.deleteAfterAction) {
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
