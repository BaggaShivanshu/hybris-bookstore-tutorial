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
package de.hybris.platform.smarteditwebservices.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * It has a composite of {@link Validator} <br>
 * Iterates and executes validate for each {@link Validator}
 */
public class CompositeValidator implements Validator
{

	private List<Validator> validators;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return getValidators().stream().map(v -> v.supports(clazz)).reduce(true, (a, b) -> a && b);
	}

	@Override
	public void validate(final Object value, final Errors errors)
	{
		getValidators().forEach(validator -> validator.validate(value, errors));
	}

	protected List<Validator> getValidators()
	{
		return validators;
	}

	@Required
	public void setValidators(final List<Validator> validators)
	{
		this.validators = validators;
	}
}
