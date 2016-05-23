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
package org.brunovccunha.taskerbox.impl.crawler;

import java.util.ArrayList;
import java.util.List;

import org.brunocvcunha.taskerbox.impl.crawler.PastebinAction;
import org.junit.Assert;
import org.junit.Test;

public class PastebinTest {

  @Test
  public void testAction() {
    PastebinAction action = new PastebinAction();

    List<String> filters = new ArrayList<>();
    filters.add("bruno");
    action.setFilters(filters);

    List<String> ignores = new ArrayList<>();
    ignores.add("viagr");
    action.setIgnored(ignores);

    List<String> patterns = new ArrayList<>();
    patterns
        .add("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\\b");
    action.setPatterns(patterns);

    action.setup();

    Assert.assertTrue(action.isBounded("371059304809541"));
    Assert.assertTrue(action.isBounded("a 371059304809541 a"));
    Assert.assertTrue(action.isBounded("371059304809541 a"));
    Assert.assertTrue(action.isBounded("a 371059304809541"));
    Assert.assertFalse(action.isBounded("1371059304809541"));
    Assert.assertFalse(action.isBounded("a371059x04809541a"));
    Assert.assertFalse(action.isBounded("bruna"));
    Assert.assertFalse(action.isBounded("zcbrunadda"));
    Assert.assertFalse(action.isValid("", "brunoviagr"));
    Assert.assertTrue(action.isBounded("xxbrunozz"));
  }
}
