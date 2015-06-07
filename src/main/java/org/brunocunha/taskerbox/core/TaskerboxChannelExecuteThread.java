package org.brunocunha.taskerbox.core;

import lombok.Getter;

public class TaskerboxChannelExecuteThread extends Thread {

	@Getter
	private TaskerboxChannel<?> channel;
	
	@Getter
	private boolean finished;
	
	@Getter
	private boolean success;
	
	@Getter
	private Exception exception;
	
	/**
	 * @param channel
	 */
	public TaskerboxChannelExecuteThread(TaskerboxChannel<?> channel) {
		super();
		this.channel = channel;
	}


	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			channel.execute();
			
			this.success = true;
		} catch(Exception e) {
			this.exception = e;
			this.success = false;
		} finally {
			this.finished = true;
		}
	}
	
	
	
}
