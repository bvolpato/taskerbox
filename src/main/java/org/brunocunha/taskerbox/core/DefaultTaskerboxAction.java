package org.brunocunha.taskerbox.core;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;



/**
 * Implements some boilerplate for actions
 * @author Bruno Candido Volpato da Cunha
 *
 * @param <T>
 */
public abstract class DefaultTaskerboxAction<T> implements ITaskerboxAction<T> {

	@Getter @Setter
	protected TaskerboxChannel<T> channel;

	@Getter @Setter
	public String id;
	
	@Override
	public void setup() {
	}
	
	@Override
	public void exception(Throwable error) {
		error.printStackTrace();
	}
	
	
	private String getLabel() {
		if (this.getChannel() == null) {
			return "NoChannel:" + this.getId();
		}
		return this.getChannel().getId() + ":" + this.getId();
	}
	/**
	 * Default to log.info displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logInfo(Logger logger, String msg) {
		logger.info("[" + getLabel() + "] - " + msg);
	}
	
	/**
	 * Default to log.warn displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logWarn(Logger logger, String msg) {
		logger.warn("[" + getLabel() + "] - " + msg);
	}
	
	/**
	 * Default to log.error displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logError(Logger logger, String msg) {
		logger.error("[" + getLabel() + "] - " + msg);
	}
	
	/**
	 * Default to log.error displaying the channel id
	 * 
	 * @param logger
	 * @param msg
	 */
	protected void logError(Logger logger, String msg, Throwable error) {
		logger.error("[" + getLabel() + "] - " + msg, error);
	}

	
}
