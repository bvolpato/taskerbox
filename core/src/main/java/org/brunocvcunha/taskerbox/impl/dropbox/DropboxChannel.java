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
package org.brunocvcunha.taskerbox.impl.dropbox;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxDelta;
import com.dropbox.core.v1.DbxEntry;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.brunocvcunha.taskerbox.core.TaskerboxChannel;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.log4j.Log4j;

@Log4j
public class DropboxChannel extends TaskerboxChannel<DbxDelta.Entry<DbxEntry>> {

  @Getter
  @Setter
  @NotNull
  private String appKey;

  @Getter
  @Setter
  @NotNull
  private String appSecret;

  @Getter
  @Setter
  @NotNull
  private String accessToken;

  @Getter
  @Setter
  @NotNull
  private String path;

  private String lastDelta;

  public static void main(String[] args) throws Exception {
    DropboxChannel channel = new DropboxChannel();
    channel.setId("DropboxMonitor");
    channel.setPath("/");
    channel.execute();
  }

  @Override
  protected void execute() throws Exception {

    DbxRequestConfig config = new DbxRequestConfig("Taskerbox/0.1", Locale.getDefault().toString());
    DbxClientV1 client = new DbxClientV1(config, this.accessToken);

    String cursor = this.lastDelta;
    if (this.lastDelta == null) {
      cursor = getStoredProperty("cursor");
    }
    logInfo(log, "Current Delta: " + cursor);

    DbxDelta<DbxEntry> delta = client.getDeltaWithPathPrefix(cursor, this.path);
    handleDelta(client, delta);
  }

  private void handleDelta(DbxClientV1 client, DbxDelta<DbxEntry> delta) throws DbxException {
    this.lastDelta = delta.cursor;
    addStoredProperty("cursor", this.lastDelta);
    logInfo(log, "Saving current delta: " + this.lastDelta);

    for (val entry : delta.entries) {
      performUnique(entry);
    }

    if (delta.hasMore) {
      handleDelta(client, client.getDeltaWithPathPrefix(delta.cursor, this.path));
    }

  }

  @Override
  protected String getItemFingerprint(DbxDelta.Entry<DbxEntry> entry) {
    return entry.toString();
  }

}
