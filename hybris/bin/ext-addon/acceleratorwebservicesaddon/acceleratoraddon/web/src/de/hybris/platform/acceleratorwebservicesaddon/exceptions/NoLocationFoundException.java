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
package de.hybris.platform.acceleratorwebservicesaddon.exceptions;

/**
 * Thrown when location was not found while setting user location
 */

public class NoLocationFoundException extends Exception
{
	/**
	 * @param location
	 */
	public NoLocationFoundException(final String location)
	{
		super("Location: " + location + " could not be found");
	}

	public NoLocationFoundException(final String location, final Throwable rootCause)
	{
		super("Location: " + location + " could not be found", rootCause);
	}
}
