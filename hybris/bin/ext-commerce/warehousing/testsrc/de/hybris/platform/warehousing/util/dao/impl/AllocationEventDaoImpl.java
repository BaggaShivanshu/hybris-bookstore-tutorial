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
package de.hybris.platform.warehousing.util.dao.impl;

import de.hybris.platform.warehousing.model.AllocationEventModel;


public class AllocationEventDaoImpl extends AbstractWarehousingDao<AllocationEventModel>
{
	@Override
	protected String getQuery()
	{
		return "SELECT {a.pk} FROM {AllocationEvent as a " //
				+ "JOIN ConsignmentEntry as e ON {a.consignmentEntry} = {e.pk} " //
				+ "JOIN Consignment as c ON {e.consignment} = {c.pk}} " //
				+ "WHERE {c.code}=?code";
	}

}
