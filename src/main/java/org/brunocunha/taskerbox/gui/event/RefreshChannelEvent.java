package org.brunocunha.taskerbox.gui.event;

import org.brunocunha.taskerbox.core.TaskerboxChannel;

public class RefreshChannelEvent {
	private TaskerboxChannel<?> channel;
	
	private boolean running;
	
	public RefreshChannelEvent(TaskerboxChannel<?> channel, boolean running) {
		super();
		this.channel = channel;
		this.running = running;
	}

	public TaskerboxChannel<?> getChannel() {
		return channel;
	}

	public void setChannel(TaskerboxChannel<?> channel) {
		this.channel = channel;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	
}
