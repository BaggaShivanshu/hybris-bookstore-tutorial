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
package de.hybris.platform.subscriptionfacades.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.subscriptionfacades.converters.populator.SearchResultSubscriptionProductPopulator;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link SearchResultSubscriptionProductPopulator}
 */
@UnitTest
public class SearchResultSubscriptionProductPopulatorTest
{
	private SearchResultSubscriptionProductPopulator searchResultSubscriptionProductPopulator;

	@Mock
	private ProductService productService;
	@Mock
	private SubscriptionProductService subscriptionProductService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		given(subscriptionProductService.isSubscription(any(ProductModel.class))).willAnswer(invocationOnMock -> {
			final ProductModel product = (ProductModel) invocationOnMock.getArguments()[0];
			return product != null && product.getSubscriptionTerm() != null;
		});
		searchResultSubscriptionProductPopulator = new SearchResultSubscriptionProductPopulator();
		searchResultSubscriptionProductPopulator.setProductService(productService);
		searchResultSubscriptionProductPopulator.setSubscriptionProductService(subscriptionProductService);
	}

	@Test
	public void testPopulate()
	{
		// create search result values
		final SearchResultValueData searchResultValueData = new SearchResultValueData();
		final Map<String, Object> searchValueMap = new HashMap<String, Object>();
		searchValueMap.put(BillingPlanModel.BILLINGFREQUENCY, "monthly");
		searchValueMap.put(SubscriptionTermModel.TERMOFSERVICERENEWAL, "yearly");
		searchValueMap.put("termLimit", "18 months");
		searchResultValueData.setValues(searchValueMap);

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("USD");
		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(1.99));
		priceData.setCurrencyIso(currency.getIsocode());

		final ProductData productData = new ProductData();
		searchResultSubscriptionProductPopulator.populate(searchResultValueData, productData);

		assertNotNull("", productData.getSubscriptionTerm());
		assertNotNull("", productData.getSubscriptionTerm().getBillingPlan());
		assertNotNull("", productData.getSubscriptionTerm().getBillingPlan().getBillingTime());
		assertEquals("", searchResultSubscriptionProductPopulator.getValue(searchResultValueData, "billingTime"), productData
				.getSubscriptionTerm().getBillingPlan().getBillingTime().getName());
		assertNotNull("", productData.getSubscriptionTerm().getTermOfServiceFrequency());
		assertEquals("", searchResultSubscriptionProductPopulator.getValue(searchResultValueData, "termLimit"), productData
				.getSubscriptionTerm().getTermOfServiceFrequency().getName());
	}
}
