/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */

package de.hybris.platform.chineselogisticservices.delivery.dao.impl;

import de.hybris.platform.chineselogisticservices.delivery.dao.C2LItemZoneDeliveryModeValueDao;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class ChineseC2LItemZoneDeliveryModeValueDao implements C2LItemZoneDeliveryModeValueDao
{
	private FlexibleSearchService flexibleSearchService;

	@Override
	public ZoneDeliveryModeValueModel findDeliveryModeValueByC2LItem(C2LItemModel c2lItem, AbstractOrderModel order,
			DeliveryModeModel deliveryMode)
	{
		String fsq = "SELECT {" + ZoneDeliveryModeValueModel.PK + "} " + "FROM {" + ZoneDeliveryModeValueModel._TYPECODE
				+ " } WHERE {" + ZoneDeliveryModeValueModel.DELIVERYMODE + "} = ?deliveryMode AND {"
				+ ZoneDeliveryModeValueModel.CURRENCY + "} = ?curr AND {" + ZoneDeliveryModeValueModel.ZONE + "} = ?zone";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter("deliveryMode", deliveryMode);
		query.addQueryParameter("curr", order.getCurrency());
		query.addQueryParameter("zone", c2lItem.getZone());
		final SearchResult<ZoneDeliveryModeValueModel> result = flexibleSearchService.search(query);
		final List<ZoneDeliveryModeValueModel> zoneDeliveryModeValueModels = result.getResult();
		if (zoneDeliveryModeValueModels != null && zoneDeliveryModeValueModels.size() > 0)
		{
			return zoneDeliveryModeValueModels.get(0);
		}
		return null;
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
