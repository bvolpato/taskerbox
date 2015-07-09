package org.brunocunha.taskerbox.impl.twitter;

import java.util.Properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.brunocunha.taskerbox.core.ITaskerboxEmailable;
import org.brunocunha.taskerbox.core.ITaskerboxMessageable;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.TaskerboxVelocityUtils;

import twitter4j.Status;

/**
 * Twitter Status Wrapper - Emailable
 * @author Bruno Candido Volpato da Cunha
 *
 */
@RequiredArgsConstructor
public class StatusWrapper implements ITaskerboxEmailable, ITaskerboxMessageable {

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

  @Override
  public String getMessageTitle(TaskerboxChannel<?> channel) {
    return "Tweet de @" + value.getUser().getScreenName();
  }

  @Override
  public String getMessageBody(TaskerboxChannel<?> channel) {
    return value.getText();
  }

  @Override
  public String toString() {
    return "StatusWrapper [value=" + value + "]";
  }
  
  
}
