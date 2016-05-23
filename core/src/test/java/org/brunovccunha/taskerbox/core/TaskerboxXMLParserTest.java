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
package org.brunovccunha.taskerbox.core;

import org.brunocvcunha.taskerbox.Taskerbox;
import org.junit.Assert;
import org.junit.Test;

public class TaskerboxXMLParserTest {

  @Test
  public void testSimple() throws Exception {
    Assert.assertNotEquals("/tmp", System.getProperty("taskerbox.performed.dir"));

    Taskerbox tasker = new Taskerbox();
    tasker.handleTaskerbox(getClass().getResourceAsStream("/taskerbox-simple-properties-test.xml"));

    Assert.assertEquals("/tmp", System.getProperty("taskerbox.performed.dir"));

    Assert.assertEquals("/tmp/Taskerbox", tasker.getDefaultProperties().get("workingDir"));
  }
}
