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
 *
 */
package de.hybris.platform.chinaaccelerator.facades.location;



import de.hybris.platform.chinaaccelerator.facades.data.CityData;

import java.util.List;


public interface CityFacade
{

	List<CityData> getCitiesByRegionCode(final String regionIsocode);

	CityData getCityForCode(final String cityCode);
}
