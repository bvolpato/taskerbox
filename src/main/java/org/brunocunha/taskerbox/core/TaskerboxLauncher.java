package org.brunocunha.taskerbox.core;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.gui.TaskerboxControlFrame;

/**
 * Class responsible for launching channels. It creates threads that are called
 * in intervals for timed threads, or a new single-thread for daemons.
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxLauncher {

	public static void startChannel(final TaskerboxChannel<?> channel,
			final TaskerboxControlFrame frame,
			final Collection<TaskerboxChannel<?>> daemons,
			final Collection<TaskerboxChannel<?>> channels) throws Exception {

		if (channel.isDaemon()) {

			try {
				Thread daemonThread = new Thread() {
					public void run() {
						try {
							channel.setup();

							synchronized (channels) {
								channels.add(channel);
							}

							if (frame != null) {
								frame.updateChannels();
							}

							while (true) {
								try {
									channel.check();
								} catch (Exception e) {
									e.printStackTrace();
								}

								try {
									Thread.sleep(channel.getEvery());
								} catch (InterruptedException ex) {
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				daemonThread.setName("daemon-" + channel.getId());
				daemonThread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}

			daemons.add(channel);
		} else {
			try {

				Thread startThread = new Thread() {

					public void run() {
						try {
							channel.setup();
						} catch (Exception e) {
							e.printStackTrace();
						}

						synchronized (channels) {
							channels.add(channel);
						}
						if (frame != null) {
							frame.updateChannels();
						}

						channel.scheduleTask(0, channel.getEvery(),
								TimeUnit.MILLISECONDS);

					}
				};
				startThread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		log.info("Channel " + channel + " created.");
	}

}
