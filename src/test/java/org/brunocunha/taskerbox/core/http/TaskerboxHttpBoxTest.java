package org.brunocunha.taskerbox.core.http;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for one of the core classes of Taskerbox
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class TaskerboxHttpBoxTest {

	private TaskerboxHttpBox httpBox;
	
	@Before
	public void setUp() throws IOException {
		httpBox = TaskerboxHttpBox.getInstance();
	}
	@Test
	public void testGet() throws ClientProtocolException, IllegalStateException, IOException, URISyntaxException {
		String content = httpBox.getStringBodyForURL("https://www.java.com/js/deployJava.txt");
		
		Assert.assertTrue(content.contains("deployJava.js"));
	}
	
	@Test(expected=IOException.class)
	public void testGetFail() throws ClientProtocolException, IllegalStateException, IOException, URISyntaxException {
		httpBox.getStringBodyForURL("https://unknownhost/js/deployJava.txt");
	}
}
