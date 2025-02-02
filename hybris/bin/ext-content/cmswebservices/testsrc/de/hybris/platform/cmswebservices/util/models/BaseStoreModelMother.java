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
package de.hybris.platform.cmswebservices.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmswebservices.util.builder.BaseStoreModelBuilder;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.daos.BaseStoreDao;

import org.springframework.beans.factory.annotation.Required;


public class BaseStoreModelMother extends AbstractModelMother<BaseStoreModel>
{
	public static final String UID_NORTH_AMERICA = "north-america";
	public static final String CODE_RETURN_PROCESS = "return-process";

	private BaseStoreDao baseStoreDao;
	private LanguageModelMother languageModelMother;
	private CatalogVersionModelMother catalogVersionModelMother;
	private CurrencyModelMother currencyModelMother;
	private CountryModelMother countryModelMother;
	private DeliveryModeModelMother deliveryModeModelMother;

	public BaseStoreModel createNorthAmerica(CatalogVersionModel catalogVersionModel)
	{
		return getFromCollectionOrSaveAndReturn(() -> getBaseStoreDao().findBaseStoresByUid(UID_NORTH_AMERICA), 
				() -> BaseStoreModelBuilder.aModel().withCatalogs(catalogVersionModel.getCatalog())
						.withCurrencies(getCurrencyModelMother().createUSDollar())
						.withDefaultCurrency(getCurrencyModelMother().createUSDollar())
						.withDefaultLanguage(getLanguageModelMother().createEnglish())
						.withLanguages(getLanguageModelMother().createEnglish(), getLanguageModelMother().createFrench())
						.withDeliveryCountries(getCountryModelMother().UnitedStates(), getCountryModelMother().Canada())
						.withLanguages(getLanguageModelMother().createEnglish())
						.withNet(Boolean.FALSE) 
						.withPaymentProvider("Mockup") 
						.withSubmitOrderProcessCode("order-process") 
						.withUid(UID_NORTH_AMERICA) 
						.withDeliveryModes(getDeliveryModeModelMother().Pickup(), getDeliveryModeModelMother().Regular())
						.build());
	}

	public BaseStoreDao getBaseStoreDao()
	{
		return baseStoreDao;
	}

	@Required
	public void setBaseStoreDao(final BaseStoreDao baseStoreDao)
	{
		this.baseStoreDao = baseStoreDao;
	}

	public LanguageModelMother getLanguageModelMother()
	{
		return languageModelMother;
	}

	@Required
	public void setLanguageModelMother(final LanguageModelMother languageModelMother)
	{
		this.languageModelMother = languageModelMother;
	}

	public CatalogVersionModelMother getCatalogVersionModelMother()
	{
		return catalogVersionModelMother;
	}
	@Required
	public void setCatalogVersionModelMother(final CatalogVersionModelMother catalogVersionModelMother)
	{
		this.catalogVersionModelMother = catalogVersionModelMother;
	}

	public CurrencyModelMother getCurrencyModelMother()
	{
		return currencyModelMother;
	}
	@Required
	public void setCurrencyModelMother(final CurrencyModelMother currencyModelMother)
	{
		this.currencyModelMother = currencyModelMother;
	}

	public CountryModelMother getCountryModelMother()
	{
		return countryModelMother;
	}
	@Required
	public void setCountryModelMother(final CountryModelMother countryModelMother)
	{
		this.countryModelMother = countryModelMother;
	}

	public DeliveryModeModelMother getDeliveryModeModelMother()
	{
		return deliveryModeModelMother;
	}
	@Required
	public void setDeliveryModeModelMother(final DeliveryModeModelMother deliveryModeModelMother)
	{
		this.deliveryModeModelMother = deliveryModeModelMother;
	}
}