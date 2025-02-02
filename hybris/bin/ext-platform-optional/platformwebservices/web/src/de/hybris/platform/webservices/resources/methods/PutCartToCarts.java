/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP 
 * Hybris ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the 
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.webservices.resources.methods;

import de.hybris.platform.core.dto.order.CartDTO;
import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.webservices.HttpPutResponseBuilder;


/**
 *
 */
public class PutCartToCarts extends HttpPutResponseBuilder<CartModel, CartDTO>
{
	private final KeyGenerator keyGenerator;

	public PutCartToCarts()
	{
		super();
		keyGenerator = (KeyGenerator) ServicelayerUtils.getApplicationContext().getBean("cartEntryCodeGenerator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webservices.HttpPostResponseBuilder#beforeProcessing(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void beforeProcessing(final CartDTO dto, final CartModel model)
	{
		if (dto.getEntries() != null)
		{
			for (final CartEntryDTO entry : dto.getEntries())
			{
				if (entry.getOrder() == null)
				{
					entry.setOrder(dto);
				}
				if (entry.getEntryNumber() == null)
				{
					entry.setEntryNumber(Integer.valueOf((String) keyGenerator.generate()));
				}
			}
		}
	}
}
