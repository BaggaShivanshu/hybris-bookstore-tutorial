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
package de.hybris.platform.cmswebservices.types.facade.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmswebservices.data.ComponentTypeData;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class I18nComponentTypePopulatorTest
{
	private static final String ITEM_TYPE = "ITEM-TYPE";
	private static final String PREFIX = "PREFIX";
	private static final String SUFFIX = "SUFFIX";

	@InjectMocks
	private I18nComponentTypePopulator i18nComponentTypePopulator;

	@Mock
	private ComposedTypeModel type;

	private ComponentTypeData typeDto;

	@Before
	public void setUp()
	{
		i18nComponentTypePopulator.setPrefix(PREFIX);
		i18nComponentTypePopulator.setSuffix(SUFFIX);
		typeDto = new ComponentTypeData();

		Mockito.when(type.getCode()).thenReturn(ITEM_TYPE);
	}

	@Test
	public void shouldPopulateI18nKey()
	{
		i18nComponentTypePopulator.populate(type, typeDto);

		final String value = PREFIX + "." + ITEM_TYPE.toLowerCase() + "." + SUFFIX;
		Assert.assertEquals(value.toLowerCase(), typeDto.getI18nKey());
	}

}
