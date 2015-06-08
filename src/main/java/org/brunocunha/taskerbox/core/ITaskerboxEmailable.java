package org.brunocunha.taskerbox.core;

/**
 * Interface that represents emailable objects
 * @author Bruno Candido Volpato da Cunha
 *
 */
public interface ITaskerboxEmailable {

	String getEmailTitle(TaskerboxChannel<?> channel);
	String getEmailBody(TaskerboxChannel<?> channel);
	
}
