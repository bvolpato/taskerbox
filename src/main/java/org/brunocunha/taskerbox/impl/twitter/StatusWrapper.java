package org.brunocunha.taskerbox.impl.twitter;

import java.util.Properties;

import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxVelocityUtils;
import org.brunocunha.taskerbox.core.utils.TaskerboxFileUtils;

import twitter4j.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Twitter Status Wrapper - Emailable
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class StatusWrapper implements ITaskerboxEmailable {

	@Getter @Setter
	private Status value;
	
	public StatusWrapper(Status value) {
		this.value = value;
	}

	@Override
	public String getEmailTitle(TaskerboxChannel<?> channel) {
		return "@" + value.getUser().getScreenName() + " Tweet";
	}

	@Override
	public String getEmailBody(TaskerboxChannel<?> channel) {
		
		Properties templateProps = new Properties();
		templateProps.put("status", value);

		return TaskerboxVelocityUtils.processTemplate("email/tweet.html", templateProps);
	}
}
