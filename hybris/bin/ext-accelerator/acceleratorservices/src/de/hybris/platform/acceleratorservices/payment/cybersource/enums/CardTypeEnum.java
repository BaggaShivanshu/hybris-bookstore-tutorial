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
package de.hybris.platform.acceleratorservices.payment.cybersource.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * This Enum represents the credit card types used by the CyberSource Hosted Order Page Service.
 */
public enum CardTypeEnum
{
	// Constant names cannot be changed due to their usage in dependant extensions, thus nosonar
	visa("001"), // NOSONAR
	master("002"), // NOSONAR
	amex("003"), // NOSONAR
	discover("004"), // NOSONAR
	diners("005"), // NOSONAR
	jcb("007"), // NOSONAR
	maestro("024"), // NOSONAR
	SWITCH("switch"); // NOSONAR

	private final String stringValue;

	// Reverse-lookup map for getting a day from an abbreviation
	private static final Map<String, CardTypeEnum> LOOKUP = new HashMap<String, CardTypeEnum>();

	static
	{
		for (final CardTypeEnum cardType : CardTypeEnum.values())
		{
			LOOKUP.put(cardType.getStringValue(), cardType);
		}
	}

	private CardTypeEnum(final String stringValue)
	{
		this.stringValue = stringValue.intern();
	}

	/**
	 * gets a String value of the current enum.
	 * @return - a Sting object.
	 */
	public String getStringValue()
	{
		return this.stringValue;
	}

	/**
	 * Gets a CardTypeEnum for the given String representation.
	 * @param stringValue - a String representing one of the CardTypeEnum.
	 * @return - the CardTypeEnum that is represented by the String value given.
	 */
	public static CardTypeEnum get(final String stringValue)
	{
		return LOOKUP.get(stringValue);
	}
}
