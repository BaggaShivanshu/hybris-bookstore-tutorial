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

package de.hybris.platform.storefront.checkout.steps.validation.impl;

import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Default implementation of delivery address validator.
 */
public class DefaultDeliveryAddressCheckoutStepValidator extends AbstractCheckoutStepValidator
{
	@Override
	public ValidationResults validateOnEnter(final RedirectAttributes redirectAttributes)
	{
		if (!getCheckoutFlowFacade().hasValidCart())
		{
			return ValidationResults.REDIRECT_TO_CART;
		}

		if (!getCheckoutFacade().hasShippingItems())
		{
			return ValidationResults.REDIRECT_TO_PICKUP_LOCATION;
		}

		return ValidationResults.SUCCESS;
	}
}
