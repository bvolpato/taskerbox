package org.brunocunha.taskerbox.utils.validation;

import org.brunocunha.taskerbox.core.utils.validation.TaskerboxValidationUtils;
import org.brunocunha.taskerbox.impl.feed.FeedChannel;
import org.junit.Test;

public class TaskerboxValidationTest {

	@Test(expected = IllegalArgumentException.class)
	public void testFeed() {
		FeedChannel feed = new FeedChannel();
		TaskerboxValidationUtils.validate(feed);
	}

}
