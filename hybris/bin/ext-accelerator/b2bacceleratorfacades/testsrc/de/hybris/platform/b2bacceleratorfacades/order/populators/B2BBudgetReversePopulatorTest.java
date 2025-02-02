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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BBudgetData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("deprecation")
@UnitTest
public class B2BBudgetReversePopulatorTest
{

	private final static String BUDGET_NAME = "Test Budget";
	private final static String BUDGET_CODE = "TestCode";
	private final static String UNIT_ID = "testUnit";
	private final static String CURRENCY_ID = "testCurrency";

	private B2BBudgetReversePopulator b2BBudgetReversePopulator;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private LocaleProvider localeProvider;

	@Mock
	private B2BBudgetData source;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private CurrencyModel testCurrency;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		setupSourceData();

		// Setup Locale
		localeProvider = Mockito.mock(LocaleProvider.class);
		final Locale locale = new Locale("EN");
		BDDMockito.given(localeProvider.getCurrentDataLocale()).willReturn(locale);

		// Setup Services
		BDDMockito.given(testUnit.getUid()).willReturn(UNIT_ID);
		BDDMockito.given(testCurrency.getIsocode()).willReturn(CURRENCY_ID);
		BDDMockito.given(b2bUnitService.getUnitForUid(UNIT_ID)).willReturn(testUnit);
		BDDMockito.given(commonI18NService.getCurrency(CURRENCY_ID)).willReturn(testCurrency);

		b2BBudgetReversePopulator = new B2BBudgetReversePopulator();
		b2BBudgetReversePopulator.setB2BUnitService(b2bUnitService);
		b2BBudgetReversePopulator.setCommonI18NService(commonI18NService);
	}

	public void setupSourceData()
	{
		BDDMockito.given(source.getName()).willReturn(BUDGET_NAME);
		BDDMockito.given(source.getCode()).willReturn(BUDGET_CODE);
		BDDMockito.given(source.getBudget()).willReturn(BigDecimal.TEN);

		final B2BUnitData testUnit = Mockito.mock(B2BUnitData.class);
		BDDMockito.given(testUnit.getUid()).willReturn(UNIT_ID);
		BDDMockito.given(source.getUnit()).willReturn(testUnit);

		final CurrencyData currency = Mockito.mock(CurrencyData.class);
		BDDMockito.given(currency.getIsocode()).willReturn(CURRENCY_ID);
		BDDMockito.given(source.getCurrency()).willReturn(currency);

		final Date date = new Date();
		BDDMockito.given(source.getStartDate()).willReturn(date);
		BDDMockito.given(source.getEndDate()).willReturn(date);
	}

	@Test
	public void testShouldPopulate()
	{
		final B2BBudgetModel target = new B2BBudgetModel();
		getContext(target).setLocaleProvider(localeProvider);

		b2BBudgetReversePopulator.populate(source, target);
		Assert.assertNotNull("target name should not be null", target.getName());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertNotNull("target code should not be null", target.getCode());
		Assert.assertEquals("source and target code should match", source.getCode(), target.getCode());
		Assert.assertNotNull("target budget should not be null", target.getBudget());
		Assert.assertEquals("source and target budget should match", source.getBudget(), target.getBudget());
		Assert.assertNotNull("target unit should not be null", target.getUnit());
		Assert.assertNotNull("target unit id should not be null", target.getUnit().getUid());
		Assert.assertEquals("source and target unit should match", source.getUnit().getUid(), target.getUnit().getUid());
		Assert.assertNotNull("target currency should not be null", target.getCurrency());
		Assert.assertNotNull("target currency ISO should not be null", target.getCurrency().getIsocode());
		Assert.assertEquals("source and target currency should match", source.getCurrency().getIsocode(), target.getCurrency()
				.getIsocode());
		Assert.assertNotNull("target start date should not be null", target.getDateRange().getStart());
		Assert.assertEquals("source and target start date should match", source.getStartDate(), target.getDateRange().getStart());
		Assert.assertNotNull("target end date should not be null", target.getDateRange().getEnd());
		Assert.assertEquals("source and target end date should match", source.getEndDate(), target.getDateRange().getEnd());
	}


	@Test
	public void testShouldPopulateNullUnit()
	{
		BDDMockito.given(b2bUnitService.getUnitForUid(UNIT_ID)).willReturn(null);

		final B2BBudgetModel target = new B2BBudgetModel();
		getContext(target).setLocaleProvider(localeProvider);

		b2BBudgetReversePopulator.populate(source, target);
		Assert.assertNotNull("target name should not be null", target.getName());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertNotNull("target code should not be null", target.getCode());
		Assert.assertEquals("source and target code should match", source.getCode(), target.getCode());
		Assert.assertNotNull("target budget should not be null", target.getBudget());
		Assert.assertEquals("source and target budget should match", source.getBudget(), target.getBudget());
		Assert.assertNull("target unit should not be null", target.getUnit());
		Assert.assertNotNull("target currency should not be null", target.getCurrency());
		Assert.assertNotNull("target currency ISO should not be null", target.getCurrency().getIsocode());
		Assert.assertEquals("source and target currency should match", source.getCurrency().getIsocode(), target.getCurrency()
				.getIsocode());
		Assert.assertNotNull("target start date should not be null", target.getDateRange().getStart());
		Assert.assertEquals("source and target start date should match", source.getStartDate(), target.getDateRange().getStart());
		Assert.assertNotNull("target end date should not be null", target.getDateRange().getEnd());
		Assert.assertEquals("source and target end date should match", source.getEndDate(), target.getDateRange().getEnd());
	}

	private ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}
}
