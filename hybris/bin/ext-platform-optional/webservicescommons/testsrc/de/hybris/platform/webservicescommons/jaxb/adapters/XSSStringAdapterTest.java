/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.webservicescommons.jaxb.adapters;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class XSSStringAdapterTest
{


	private XSSStringAdapter xssStringAdapter;


	@Before
	public void setUp() throws Exception
	{
		xssStringAdapter = Mockito.spy(new XSSStringAdapter());
	}

	protected Map<String, String> createDefaultRulePatternDefinitions()
	{
		final Map<String, String> patterns = new LinkedHashMap<>();
		patterns.put("xss.filter.rule.script_fragments", "(?i)<script>(.*?)</script>");
		patterns.put("xss.filter.rule.src", "(?ims)[\\s\r\n]{1}src[\\s\r\n]*=[\r\n\\s]*'(.*?)'");
		patterns.put("xss.filter.rule.lonely_script_tags", "(?i)</script>");
		patterns.put("xss.filter.rule.lonely_script_tags2", "(?ims)<script(.*?)>");
		patterns.put("xss.filter.rule.eval", "(?ims)eval\\((.*?)\\)");
		patterns.put("xss.filter.rule.expression", "(?ims)expression\\((.*?)\\)");
		patterns.put("xss.filter.rule.javascript", "(?i)javascript:");
		patterns.put("xss.filter.rule.vbscript", "(?i)vbscript:");
		patterns.put("xss.filter.rule.onload", "(?ims)onload(.*?)=");
		return patterns;
	}

	@Test
	public void testLegalValues() throws ParseException
	{
		doReturn(Boolean.TRUE).when(xssStringAdapter).isXSSFilterEnabled();
		doReturn(createDefaultRulePatternDefinitions()).when(xssStringAdapter).getPatternDefinitions();

		final String legalValue1 = "hello world! How about src script eval-uation ?\", \"I've can see your \n"
				+ "expression onload that bloody vbscript !";
		final String legalValue2 = "\n\r\tasldkl sad asjdnalsd";
		final String legalValue3 = "single value as well";

		assertEquals(xssStringAdapter.unmarshal(legalValue1), legalValue1);
		assertEquals(xssStringAdapter.unmarshal(legalValue2), legalValue2);
		assertEquals(xssStringAdapter.unmarshal(legalValue3), legalValue3);
	}

	@Test
	public void testIllegalValues() throws ParseException
	{
		doReturn(Boolean.TRUE).when(xssStringAdapter).isXSSFilterEnabled();
		doReturn(createDefaultRulePatternDefinitions()).when(xssStringAdapter).getPatternDefinitions();

		final String illegalValue1 = ">>><script>whatever <p/> in here is not legal (how about newline?) </script><<<";
		final String expectedValue1 = ">>><<<";
		final String illegalValue2 = ">>></ScRiPt><<<";
		final String expectedValue2 = ">>><<<";
		final String illegalValue3 = ">>> src\n \r =\n 'http://google.de'<<<";
		final String expectedValue3 = ">>><<<";
		final String illegalValue4 = ">>><ScRiPtwhat ever\n may come ou\n way><<<";
		final String expectedValue4 = ">>><<<";

		assertEquals(xssStringAdapter.unmarshal(illegalValue1), expectedValue1);
		assertEquals(xssStringAdapter.unmarshal(illegalValue2), expectedValue2);
		assertEquals(xssStringAdapter.unmarshal(illegalValue3), expectedValue3);
		assertEquals(xssStringAdapter.unmarshal(illegalValue4), expectedValue4);
	}

}
