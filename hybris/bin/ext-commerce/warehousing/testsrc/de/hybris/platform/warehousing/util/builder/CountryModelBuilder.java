/*
 *  
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
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.core.model.c2l.CountryModel;

import java.util.Locale;

public class CountryModelBuilder 
{
	private final CountryModel model;

	private CountryModelBuilder() 
	{
		model = new CountryModel();
	}

	private CountryModel getModel() 
	{
		return this.model;
	}

	public static CountryModelBuilder aModel() 
	{
		return new CountryModelBuilder();
	}
	
	public static CountryModelBuilder fromModel(final CountryModel model)
	{
		return new CountryModelBuilder();
	}


	public CountryModel build() 
	{
		return getModel();
	}

	public CountryModelBuilder withIsoCode(final String isoCode) 
	{
		getModel().setIsocode(isoCode);
		return this;
	}

	public CountryModelBuilder withName(final String name, final Locale locale) 
	{
		getModel().setName(name, locale);
		return this;
	}

	public CountryModelBuilder withActive(final Boolean active) 
	{
		getModel().setActive(active);
		return this;
	}
}
