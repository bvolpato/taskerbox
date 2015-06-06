package org.brunocunha.taskerbox.core;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.brunocunha.taskerbox.core.utils.TaskerboxFileUtils;

@RequiredArgsConstructor
public class ChannelSerializerThread extends Thread {
	
	@NonNull
	@Getter @Setter
	private TaskerboxChannel<?> channel;
	
	public void run() {
		
		while (channel.getLastPerformed() > (System.currentTimeMillis() - 1000)) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			TaskerboxFileUtils.serializeMemory(channel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		channel.setPendingSerializerThread(false);

	}
	
}
