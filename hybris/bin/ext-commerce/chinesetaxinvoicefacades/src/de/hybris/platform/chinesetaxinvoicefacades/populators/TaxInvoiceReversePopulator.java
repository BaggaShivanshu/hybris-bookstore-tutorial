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
package de.hybris.platform.chinesetaxinvoicefacades.populators;

import de.hybris.platform.chinesetaxinvoicefacades.data.TaxInvoiceData;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceCategory;
import de.hybris.platform.chinesetaxinvoiceservices.enums.InvoiceRecipientType;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;


@Component("taxInvoiceReversePopulator")
public class TaxInvoiceReversePopulator implements Populator<TaxInvoiceData, TaxInvoiceModel>
{

	@Override
	public void populate(final TaxInvoiceData source, final TaxInvoiceModel target) throws ConversionException
	{

		if (StringUtils.isNotBlank(source.getCategory()))
		{
			target.setCategory(InvoiceCategory.valueOf(source.getCategory()));
		}
		else
		{
			target.setCategory(InvoiceCategory.GENERAL);
		}

		if (StringUtils.isNotBlank(source.getRecipientType()))
		{
			target.setRecipientType(InvoiceRecipientType.valueOf(source.getRecipientType()));
		}
		else
		{
			target.setRecipientType(InvoiceRecipientType.INDIVIDUAL);
		}

		target.setRecipient(source.getRecipient());
	}

}
