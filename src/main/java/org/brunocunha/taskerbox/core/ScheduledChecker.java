package org.brunocunha.taskerbox.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * Thread that is created by a scheduler
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
@RequiredArgsConstructor
public class ScheduledChecker extends Thread {

	@NonNull
	@Getter @Setter
	private TaskerboxChannel<?> channel;

	public void run() {
		try {
			if (!channel.isPaused()) {
				
				if (channel.getTimeout() <= 0) {
					channel.check();
				} else {
					
					ExecutorService executor = Executors.newCachedThreadPool();
					Callable<Object> task = new Callable<Object>() {
					   public Object call() throws Exception {
						  channel.check();
					      return channel.getCheckCount();
					   }
					};

					Future<Object> future = executor.submit(task);
					try {
					   future.get(channel.getTimeout(), TimeUnit.MILLISECONDS); 
					} catch (Exception e) {
					   log.warn("Timeout reached on scheduler for " + channel.getId());
					} finally {
					   future.cancel(true); 
					}

					
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
