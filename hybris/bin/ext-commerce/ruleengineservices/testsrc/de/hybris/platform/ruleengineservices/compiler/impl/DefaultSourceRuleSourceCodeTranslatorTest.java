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
package de.hybris.platform.ruleengineservices.compiler.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleActionsTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionsTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAction;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNoOpAction;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariablesGenerator;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.services.RuleActionsService;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSourceRuleSourceCodeTranslatorTest
{
	private static final String RULE_CODE = "testrule";
	private static final String RULE_CONDITIONS = "[{\"definitionId\":\"y_qualifying_products\"}]";
	private static final String RULE_ACTIONS = "[{\"definitionId\":\"y_order_percentage_discount\"}]";

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private RuleIrVariablesGenerator variablesGenerator;

	@Mock
	private RuleCompilerContext compilerContext;

	@Mock
	private SourceRuleModel sourceRule;

	@Mock
	private RuleConditionsService ruleConditionsService;

	@Mock
	private RuleActionsService ruleActionsService;

	@Mock
	private RuleConditionsTranslator ruleConditionsTranslator;

	@Mock
	private RuleActionsTranslator ruleActionsTranslator;

	private DefaultSourceRuleSourceCodeTranslator sourceRuleSourceCodeTranslator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(compilerContext.getVariablesGenerator()).thenReturn(variablesGenerator);

		sourceRuleSourceCodeTranslator = new DefaultSourceRuleSourceCodeTranslator();
		sourceRuleSourceCodeTranslator.setRuleConditionsService(ruleConditionsService);
		sourceRuleSourceCodeTranslator.setRuleActionsService(ruleActionsService);
		sourceRuleSourceCodeTranslator.setRuleConditionsTranslator(ruleConditionsTranslator);
		sourceRuleSourceCodeTranslator.setRuleActionsTranslator(ruleActionsTranslator);
	}

	@Test
	public void translateRule() throws Exception
	{
		// given
		final RuleConditionData ruleConditionData = new RuleConditionData();
		ruleConditionData.setChildren(Collections.EMPTY_LIST);
		ruleConditionData.setParameters(Collections.EMPTY_MAP);
		final List<RuleConditionData> ruleConditions = Arrays.asList(ruleConditionData);

		final RuleActionData ruleActionData = new RuleActionData();
		ruleActionData.setParameters(Collections.EMPTY_MAP);
		final List<RuleActionData> ruleActions = Arrays.asList(ruleActionData);

		final List<RuleIrCondition> ruleIrConditions = Arrays.asList(new RuleIrFalseCondition());
		final List<RuleIrAction> ruleIrActions = Arrays.asList(new RuleIrNoOpAction());

		when(sourceRule.getCode()).thenReturn(RULE_CODE);
		when(sourceRule.getConditions()).thenReturn(RULE_CONDITIONS);
		when(sourceRule.getActions()).thenReturn(RULE_ACTIONS);

		when(ruleConditionsService.convertConditionsFromString(RULE_CONDITIONS)).thenReturn(ruleConditions);
		when(ruleActionsService.convertActionsFromString(RULE_ACTIONS)).thenReturn(ruleActions);

		when(compilerContext.getRule()).thenReturn(sourceRule);

		when(ruleConditionsTranslator.translate(compilerContext, ruleConditions)).thenReturn(ruleIrConditions);
		when(ruleActionsTranslator.translate(compilerContext, ruleActions)).thenReturn(ruleIrActions);

		// when
		final RuleIr ruleIr = sourceRuleSourceCodeTranslator.translate(compilerContext);

		// then
		assertNotNull(ruleIr);
		assertSame(ruleIrConditions, ruleIr.getConditions());
		assertSame(ruleIrActions, ruleIr.getActions());
	}

	@Test
	public void failToConvertConditions() throws Exception
	{
		// given
		final List<RuleActionData> ruleActions = Arrays.asList(new RuleActionData());

		when(sourceRule.getCode()).thenReturn(RULE_CODE);
		when(sourceRule.getConditions()).thenReturn(RULE_CONDITIONS);
		when(sourceRule.getActions()).thenReturn(RULE_ACTIONS);

		when(compilerContext.getRule()).thenReturn(sourceRule);

		when(ruleConditionsService.convertConditionsFromString(RULE_CONDITIONS)).thenThrow(new RuleConverterException());
		when(ruleActionsService.convertActionsFromString(RULE_ACTIONS)).thenReturn(ruleActions);

		// expect
		expectedException.expect(RuleCompilerException.class);

		// when
		sourceRuleSourceCodeTranslator.translate(compilerContext);
	}

	@Test
	public void failToConvertActions() throws Exception
	{
		// given
		final List<RuleConditionData> ruleConditions = Arrays.asList(new RuleConditionData());

		when(sourceRule.getCode()).thenReturn(RULE_CODE);
		when(sourceRule.getConditions()).thenReturn(RULE_CONDITIONS);
		when(sourceRule.getActions()).thenReturn(RULE_ACTIONS);

		when(compilerContext.getRule()).thenReturn(sourceRule);

		when(ruleConditionsService.convertConditionsFromString(RULE_CONDITIONS)).thenReturn(ruleConditions);
		when(ruleActionsService.convertActionsFromString(RULE_ACTIONS)).thenThrow(new RuleConverterException());

		// expect
		expectedException.expect(RuleCompilerException.class);

		// when
		sourceRuleSourceCodeTranslator.translate(compilerContext);
	}

	@Test
	public void failToTranslateConditions() throws Exception
	{
		// given
		final RuleConditionData ruleConditionData = new RuleConditionData();
		ruleConditionData.setChildren(Collections.EMPTY_LIST);
		ruleConditionData.setParameters(Collections.EMPTY_MAP);
		final List<RuleConditionData> ruleConditions = Arrays.asList(ruleConditionData);

		final RuleActionData ruleActionData = new RuleActionData();
		ruleActionData.setParameters(Collections.EMPTY_MAP);
		final List<RuleActionData> ruleActions = Arrays.asList(ruleActionData);

		final List<RuleIrAction> ruleIrActions = Arrays.asList(new RuleIrNoOpAction());

		when(sourceRule.getCode()).thenReturn(RULE_CODE);
		when(sourceRule.getConditions()).thenReturn(RULE_CONDITIONS);
		when(sourceRule.getActions()).thenReturn(RULE_ACTIONS);

		when(compilerContext.getRule()).thenReturn(sourceRule);

		when(ruleConditionsService.convertConditionsFromString(RULE_CONDITIONS)).thenReturn(ruleConditions);
		when(ruleActionsService.convertActionsFromString(RULE_ACTIONS)).thenReturn(ruleActions);

		when(ruleConditionsTranslator.translate(compilerContext, ruleConditions)).thenThrow(new RuleCompilerException());
		when(ruleActionsTranslator.translate(compilerContext, ruleActions)).thenReturn(ruleIrActions);

		// expect
		expectedException.expect(RuleCompilerException.class);

		// when
		sourceRuleSourceCodeTranslator.translate(compilerContext);
	}

	@Test
	public void failToTranslateActions() throws Exception
	{
		// given
		final RuleConditionData ruleConditionData = new RuleConditionData();
		ruleConditionData.setChildren(Collections.EMPTY_LIST);
		ruleConditionData.setParameters(Collections.EMPTY_MAP);
		final List<RuleConditionData> ruleConditions = Arrays.asList(ruleConditionData);

		final RuleActionData ruleActionData = new RuleActionData();
		ruleActionData.setParameters(Collections.EMPTY_MAP);
		final List<RuleActionData> ruleActions = Arrays.asList(ruleActionData);

		final List<RuleIrCondition> ruleIrConditions = Arrays.asList(new RuleIrFalseCondition());

		when(sourceRule.getCode()).thenReturn(RULE_CODE);
		when(sourceRule.getConditions()).thenReturn(RULE_CONDITIONS);
		when(sourceRule.getActions()).thenReturn(RULE_ACTIONS);

		when(compilerContext.getRule()).thenReturn(sourceRule);

		when(ruleConditionsService.convertConditionsFromString(RULE_CONDITIONS)).thenReturn(ruleConditions);
		when(ruleActionsService.convertActionsFromString(RULE_ACTIONS)).thenReturn(ruleActions);

		when(ruleConditionsTranslator.translate(compilerContext, ruleConditions)).thenReturn(ruleIrConditions);
		when(ruleActionsTranslator.translate(compilerContext, ruleActions)).thenThrow(new RuleCompilerException());

		// expect
		expectedException.expect(RuleCompilerException.class);

		// when
		sourceRuleSourceCodeTranslator.translate(compilerContext);
	}
}
