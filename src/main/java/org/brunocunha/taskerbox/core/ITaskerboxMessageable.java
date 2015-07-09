package org.brunocunha.taskerbox.core;

/**
 * Interface that represents short message objects
 * @author Bruno Candido Volpato da Cunha
 *
 */
public interface ITaskerboxMessageable {

	String getMessageTitle(TaskerboxChannel<?> channel);
	String getMessageBody(TaskerboxChannel<?> channel);
	
}
