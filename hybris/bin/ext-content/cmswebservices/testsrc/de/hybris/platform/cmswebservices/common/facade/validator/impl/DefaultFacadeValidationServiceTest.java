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
package de.hybris.platform.cmswebservices.common.facade.validator.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmswebservices.exception.ValidationException;
import de.hybris.platform.cmswebservices.factory.ErrorFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFacadeValidationServiceTest
{
	@InjectMocks
	private DefaultFacadeValidationService validationService;

	@Mock
	private ErrorFactory validatorErrorFactory;
	@Mock
	private Errors errors;
	@Mock
	private Validator validator;

	@Before
	public void setUp()
	{
		when(validatorErrorFactory.createInstance(Mockito.any())).thenReturn(errors);
		when(errors.hasErrors()).thenReturn(Boolean.FALSE);
		when(validator.supports(Mockito.any())).thenReturn(Boolean.TRUE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailValidation_UnsupportedType()
	{
		when(validator.supports(Mockito.any())).thenReturn(Boolean.FALSE);

		validationService.validate(validator, new Object());
	}

	@Test(expected = ValidationException.class)
	public void shouldFailValidation_ValidationErrors()
	{
		when(errors.hasErrors()).thenReturn(Boolean.TRUE);

		validationService.validate(validator, new Object());
	}
}
