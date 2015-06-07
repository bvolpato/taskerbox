package org.brunocunha.taskerbox.core.utils;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;

import lombok.extern.log4j.Log4j;

import org.brunocunha.taskerbox.core.TaskerboxConstants;

/**
 * System Tray integration with Taskerbox
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Log4j
public class TaskerboxTrayUtils {

	/**
	 * 
	 */
	private static TrayIcon trayIconInstance;

	/**
	 * Gets the {@link TrayIcon} reference
	 * @return
	 */
	public static TrayIcon getTrayIcon() {
		if (trayIconInstance == null) {

			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(TaskerboxTrayUtils.class.getResource("/tasker.png"));

			trayIconInstance = new TrayIcon(image, TaskerboxConstants.APP_NAME);
			try {
				tray.add(trayIconInstance);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}

		return trayIconInstance;
	}

	/**
	 * Displays a message at the system tray
	 * @param caption
	 * @param text
	 * @param messageType
	 * @param listener
	 */
	public static void displayMessage(String caption, String text, MessageType messageType, ActionListener listener) {

		log.info(messageType + ": " + text);

		if (SystemTray.isSupported()) {

			removeAllListeners();
			if (listener != null) {
				getTrayIcon().addActionListener(listener);
			}

			getTrayIcon().displayMessage(caption, text, messageType);
		}
	}

	/**
	 * Remove tray listeners
	 */
	public static void removeAllListeners() {
		for (ActionListener listener : getTrayIcon().getActionListeners()) {
			getTrayIcon().removeActionListener(listener);
		}
	}
}
