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
package de.hybris.platform.webservices.price.impl;

/**
 *
 */
public class IncompatibleDiscountException extends RuntimeException
{
	/**
	 * 
	 */
	public IncompatibleDiscountException(final String message, final Throwable exception)
	{
		super(message, exception);
	}

	public IncompatibleDiscountException(final String message)
	{
		super(message);
	}
}
