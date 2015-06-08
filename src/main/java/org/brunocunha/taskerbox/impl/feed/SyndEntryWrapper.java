package org.brunocunha.taskerbox.impl.feed;

import java.util.Properties;

import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxVelocityUtils;
import org.brunocunha.taskerbox.core.utils.TaskerboxFileUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * SyndEntry Wrapper - Emailable
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class SyndEntryWrapper implements ITaskerboxEmailable {

	@Getter @Setter
	private SyndEntry value;
	
	public SyndEntryWrapper(SyndEntry value) {
		this.value = value;
	}

	@Override
	public String getEmailTitle(TaskerboxChannel<?> channel) {
		return "[" + channel.getId() + "] " + value.getTitle();
	}

	@Override
	public String getEmailBody(TaskerboxChannel<?> channel) {
		
		Properties templateProps = new Properties();
		templateProps.put("entry", value);

		return TaskerboxVelocityUtils.processTemplate("email/synd.html", templateProps);
	}
}
