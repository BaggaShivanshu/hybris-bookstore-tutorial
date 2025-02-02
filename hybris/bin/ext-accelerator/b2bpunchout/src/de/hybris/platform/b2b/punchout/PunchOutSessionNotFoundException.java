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
package de.hybris.platform.b2b.punchout;

/**
 *
 */
public class PunchOutSessionNotFoundException extends PunchOutException
{

	/**
	 * Constructor.
	 *
	 * @param message
	 *           the error message
	 */
	public PunchOutSessionNotFoundException(final String message)
	{
		super(PunchOutResponseCode.CONFLICT, message);
	}

	/**
	 * Constructor.
	 *
	 * @param message
	 *           the error message
	 * @param cause
	 *           the cause
	 */
	public PunchOutSessionNotFoundException(final String message, final Throwable cause)
	{
		super(PunchOutResponseCode.CONFLICT, message, cause);
	}

}
