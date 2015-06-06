package org.brunocunha.taskerbox.impl.crawler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PastebinTest {

	@Test
	public void testAction() {
		PastebinAction action = new PastebinAction();
		
		List<String> filters = new ArrayList<String>();
		filters.add("bruno");
		action.setFilters(filters);
		
		List<String> ignores = new ArrayList<String>();
		ignores.add("viagr");
		action.setIgnored(ignores);
		
		List<String> patterns = new ArrayList<String>();
		patterns.add("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\\b");
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
