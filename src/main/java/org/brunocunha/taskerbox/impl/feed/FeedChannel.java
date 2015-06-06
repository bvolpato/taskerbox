package org.brunocunha.taskerbox.impl.feed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.extern.log4j.Log4j;

import org.apache.http.HttpEntity;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.annotation.TaskerboxField;
import org.brunocunha.taskerbox.core.http.TaskerboxHttpBox;
import org.hibernate.validator.constraints.URL;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Feed/Atom Input Channel
 * 
 * @author Bruno Candido Volpato da Cunha
 * 
 */
@Log4j
public class FeedChannel extends TaskerboxChannel<SyndEntry> {

	@TaskerboxField("Feed URL")
	@Getter @Setter
	@URL
	private String feedUrl;

	@TaskerboxField("Filter")
	@Getter @Setter
	private String filter;

	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws IOException, IllegalArgumentException, FeedException, IllegalStateException, URISyntaxException {
		logInfo(log, "Checking #"+checkCount+"... [" + feedUrl + " / '" + filter + "']");

		@Cleanup XmlReader reader = null;
		HttpEntity responseBody = TaskerboxHttpBox.getInstance().getEntityForURL(feedUrl);
		reader = new XmlReader(responseBody.getContent());
		SyndFeed feed = new SyndFeedInput().build(reader);

		List<SyndEntry> list = feed.getEntries();
		for (val entry : list) {

			if (filter != null && !entry.getTitle().contains(filter)) {
				continue;
			}

			performUnique(entry);

		}
	}

	public String getItemFingerprint(SyndEntry entry) {
		return entry.getUri();
	}


}
