package org.brunocunha.taskerbox.core;



/**
 * Default Interface for Actions
 * @author Bruno Candido Volpato da Cunha
 *
 * @param <T> The Action Input Class
 */
public interface ITaskerboxAction<T> {

	/**
	 * Default join method for actions
	 * @param input
	 */
	public void action(T input);
	
	/**
	 * Default exception method for actions
	 * @param input
	 */
	public void exception(Throwable input);
	
	/**
	 * Method used to initial setup of action
	 */
	public void setup();
	
	/**
	 * Ties a channel into an Action
	 * @param channel
	 */
	public void setChannel(TaskerboxChannel<T> channel);
	
	/**
	 * Returns the ID for action
	 * @return
	 */
	public String getId();

	/**
	 * Sets the ID for action
	 * @param id
	 */
	public void setId(String id);
	
}
