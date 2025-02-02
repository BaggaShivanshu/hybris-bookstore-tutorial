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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.returns.dao.ReturnRequestDao;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.junit.Test;


public class DefaultReturnRequestDaoTest extends ServicelayerTest
{

	@Resource
	private ReturnRequestDao returnRequestDao;

	@Test
	public void shouldThrowNoSuchElementExceptionWhenGetingReturnRequest()
	{
		returnRequestDao.getReturnRequests("IDONTEXIST");

	}
}
