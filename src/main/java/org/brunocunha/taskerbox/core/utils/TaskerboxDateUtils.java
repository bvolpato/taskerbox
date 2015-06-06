package org.brunocunha.taskerbox.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utilities for Taskerbox
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class TaskerboxDateUtils {
	/**
	 * @return Current Timestamp
	 */
	public static String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return "[".concat(sdf.format(new Date())).concat("]");
	}
}
