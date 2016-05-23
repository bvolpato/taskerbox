/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brunovccunha.taskerbox.core.http;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.taskerbox.core.http.TaskerboxHttpBox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for one of the core classes of Taskerbox
 *
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class TaskerboxHttpBoxTest {

  private TaskerboxHttpBox httpBox;

  @Before
  public void setUp() throws IOException {
    this.httpBox = TaskerboxHttpBox.getInstance();
  }

  @Test
  public void testGet() throws ClientProtocolException, IllegalStateException, IOException,
      URISyntaxException {
    String content = this.httpBox.getStringBodyForURL("https://www.java.com/js/deployJava.txt");

    Assert.assertTrue(content.contains("deployJava.js"));
  }

  @Test(expected = IOException.class)
  public void testGetFail() throws ClientProtocolException, IllegalStateException, IOException,
      URISyntaxException {
    this.httpBox.getStringBodyForURL("https://unknownhost/js/deployJava.txt");
  }
}
