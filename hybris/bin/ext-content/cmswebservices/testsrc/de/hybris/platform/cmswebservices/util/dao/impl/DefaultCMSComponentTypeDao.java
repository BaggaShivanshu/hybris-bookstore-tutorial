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
package de.hybris.platform.cmswebservices.util.dao.impl;

import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cmswebservices.util.dao.CMSComponentTypeDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCMSComponentTypeDao implements CMSComponentTypeDao
{
	private FlexibleSearchService flexibleSearchService;

	@Override
	public CMSComponentTypeModel getCMSComponentTypeByCode(String code)
	{
		final String queryString = "SELECT {pk} FROM {CMSComponentType} WHERE {code}=?code";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("code", code);
		return getFlexibleSearchService().searchUnique(query);
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
