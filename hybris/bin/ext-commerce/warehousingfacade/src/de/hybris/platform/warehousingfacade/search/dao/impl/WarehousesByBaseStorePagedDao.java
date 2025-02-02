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
package de.hybris.platform.warehousingfacade.search.dao.impl;



import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.core.model.ItemModel;

import java.util.Map;


/**
 *
 *  WarehousesByPointOfServicePagedDao is a specific dao that will get {@link de.hybris.platform.ordersplitting.model.WarehouseModel} per {@link de.hybris.platform.store.BaseStoreModel}
 *
 */

public class WarehousesByBaseStorePagedDao<M extends ItemModel> extends DefaultPagedGenericDao<M>
{
	protected final String typeCode;

	public WarehousesByBaseStorePagedDao(String typeCode)
	{
		super(typeCode);
		this.typeCode = typeCode;
	}

	@Override
	protected void appendWhereClausesToBuilder(final StringBuilder builder, final Map<String, ?> params)
	{
		if (params != null && !params.isEmpty())
		{
			builder.append("WHERE ");
			boolean firstParam = true;
			for (final String paramName : params.keySet())
			{
				if (!firstParam)
				{
					builder.append("AND ");
				}
				builder.append("{r:source}=?").append(paramName).append(' ');
				firstParam = false;
			}
		}
	}

	@Override
	protected StringBuilder createQueryString()
	{
		final StringBuilder builder = new StringBuilder(25);
		builder.append("SELECT {c:").append(ItemModel.PK).append("} ");
		builder.append("FROM {").append(typeCode).append(" AS c JOIN ").append("BaseStore2WarehouseRel as r ")
				.append("ON {c.pk} = {r.target}} ");
		return builder;
	}
}
