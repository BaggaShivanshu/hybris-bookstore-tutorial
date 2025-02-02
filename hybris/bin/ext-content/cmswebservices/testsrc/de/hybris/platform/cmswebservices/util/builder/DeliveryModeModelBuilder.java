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
package de.hybris.platform.cmswebservices.util.builder;

import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import java.util.Locale;


public class DeliveryModeModelBuilder
{
	private final DeliveryModeModel model;

	private DeliveryModeModelBuilder()
	{
		model = new DeliveryModeModel();
	}

	private DeliveryModeModel getModel()
	{
		return this.model;
	}

	public static DeliveryModeModelBuilder aModel()
	{
		return new DeliveryModeModelBuilder();
	}

	public DeliveryModeModel build()
	{
		return getModel();
	}

	public DeliveryModeModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public DeliveryModeModelBuilder withActive(final Boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public DeliveryModeModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

}