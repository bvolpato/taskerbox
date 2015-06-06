package org.brunocunha.taskerbox.impl.dropbox;

import java.util.Locale;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.TaskerboxChannel;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxDelta;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

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
		DbxClient client = new DbxClient(config, accessToken);

		String cursor = lastDelta;
		if (lastDelta == null) {
			cursor = getStoredProperty("cursor");
		}
		logInfo(log, "Current Delta: " + cursor);
		
		DbxDelta<DbxEntry> delta = client.getDeltaWithPathPrefix(cursor, path);
		handleDelta(client, delta);
	}

	private void handleDelta(DbxClient client, DbxDelta<DbxEntry> delta) throws DbxException {
		lastDelta = delta.cursor;
		addStoredProperty("cursor", lastDelta);
		logInfo(log, "Saving current delta: " + lastDelta);
		
		for (val entry : delta.entries) {
			performUnique(entry);
		}
		
		if (delta.hasMore) {
			handleDelta(client, client.getDeltaWithPathPrefix(delta.cursor, path));
		}
		
	}

	@Override
	protected String getItemFingerprint(DbxDelta.Entry<DbxEntry> entry) {
		return entry.toString();
	}

}
