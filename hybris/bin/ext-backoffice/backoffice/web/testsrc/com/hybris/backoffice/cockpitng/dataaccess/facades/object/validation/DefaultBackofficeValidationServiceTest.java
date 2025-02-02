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
 */

package com.hybris.backoffice.cockpitng.dataaccess.facades.object.validation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.validation.enums.Severity;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.model.constraints.ConstraintGroupModel;
import de.hybris.platform.validation.services.ConstraintService;
import de.hybris.platform.validation.services.ValidationService;
import de.hybris.platform.validation.services.impl.LocalizedHybrisConstraintViolation;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.validation.impl.DefaultBackofficeValidationService;
import com.hybris.backoffice.daos.BackofficeValidationDao;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.type.impl.DefaultTypeFacade;
import com.hybris.cockpitng.validation.impl.DefaultValidationContext;
import com.hybris.cockpitng.validation.impl.DefaultValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationGroup;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationResult;
import com.hybris.cockpitng.validation.model.ValidationSeverity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.ObjectUtils;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Equals;

import com.google.common.collect.Lists;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeValidationServiceTest
{
	@InjectMocks
	private final ConstraintGroupModel defaultGroup = new ConstraintGroupModel();

	@Mock private ValidationService platformValidationService;
	@Mock private DefaultBackofficeValidationService backofficeValidationService;
	@Mock
	private ConstraintService constraintService;
	@Mock private BackofficeValidationDao validationDao;

	final private List<ConstraintGroupModel> platformConstraintGroups = Lists.newArrayList();
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void before() throws TypeNotFoundException
	{
		when(validationDao.getConstraintGroups(any())).thenAnswer(invocation -> {
			final List<String> ids = (List<String>) invocation.getArguments()[0];
			return getPlatformConstraintGroups(ids.stream().map(ValidationGroup::new).collect(Collectors.toList()));
		});

		when(platformValidationService.getDefaultConstraintGroup()).thenReturn(defaultGroup);

		backofficeValidationService = new DefaultBackofficeValidationService();
		backofficeValidationService.setValidationService(platformValidationService);
		backofficeValidationService.setValidationDao(validationDao);

		final DefaultTypeFacade typeFacade = Mockito.mock(DefaultTypeFacade.class);
		final DataType dataType = Mockito.mock(DataType.class);
		final DataAttribute attr = new DataAttribute.Builder("code").build();
		when(dataType.getAttribute(Mockito.anyString())).thenReturn(attr);

		final String testDataType = "testDataType";
		when(typeFacade.getType(Mockito.anyObject())).thenReturn(testDataType);
		when(typeFacade.load(testDataType)).thenReturn(dataType);

		backofficeValidationService.setTypeFacade(typeFacade);
	}


	@Test
	public void testOnNullObject()
	{
		final List<ValidationInfo> validate = backofficeValidationService.validate(null, null);
		Assertions.assertThat(validate).hasSize(0);
	}

	@Test
	public void testOnNullObjectProperty()
	{
		final List<ValidationInfo> validate = backofficeValidationService.validate(null, Lists.newArrayList("code"), null);
		Assertions.assertThat(validate).hasSize(0);
	}

	@Test
	public void testOnObjectWithNullContext()
	{
		defaultMockForObject(Severity.WARN);
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), null);
		Assertions.assertThat(validate).hasSize(1);
	}

	@Test
	public void testViolationWithoutProperty()
	{
		mockViolationWithoutProperty();
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), null);
		Assertions.assertThat(validate).hasSize(1);
	}

	@Test
	public void testOnObjectPropertyWithNullContext()
	{
		defaultMockForObjectProperty();
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), Lists.newArrayList("code"), null);
		Assertions.assertThat(validate).hasSize(1);
	}

	@Test
	public void testOnObjectWithEmptyContext()
	{
		defaultMockForObject(Severity.INFO);
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), new DefaultValidationContext());
		Assertions.assertThat(validate).hasSize(1);
		Assertions.assertThat(validate.get(0).isConfirmed()).isFalse();
		Assertions.assertThat(validate.get(0).getInvalidPropertyPath()).isEqualToIgnoringCase("code");
		Assertions.assertThat(validate.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.INFO);
		Assertions.assertThat(validate.get(0).getValidationMessage()).isEqualToIgnoringCase("code too short");
	}

	@Test
	public void testOnLocalizedObjectWithEmptyContext()
	{
		defaultMockForLocalizedObject();
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), new DefaultValidationContext());
		Assertions.assertThat(validate).hasSize(1);
		Assertions.assertThat(validate.get(0).isConfirmed()).isFalse();
		Assertions.assertThat(validate.get(0).getInvalidPropertyPath()).isEqualToIgnoringCase(
				String.format("name[%s]", Locale.CHINA.toLanguageTag()));
		Assertions.assertThat(validate.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.WARN);
		Assertions.assertThat(validate.get(0).getValidationMessage()).isEqualToIgnoringCase("name too short");
	}

	@Test
	public void testOnObjectWithContextThatHasConfirmedWarnings()
	{
		defaultMockForObject(Severity.WARN);

		final DefaultValidationInfo validationInfo = getConfirmedWarningOnCodeWarning();
		final DefaultValidationContext context = new DefaultValidationContext(new ValidationResult(
				Collections.singletonList(validationInfo)));
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), context);
		Assertions.assertThat(validate).hasSize(1);
		Assertions.assertThat(validate.get(0).isConfirmed()).isTrue();
	}

	@Test
	public void testOnObjectWithSpecifiedConstraintGroup()
	{
		defaultMockForObject(Severity.WARN);

		final List<ValidationGroup> groups = getCockpitConstraintGroups();
		final List<ConstraintGroupModel> platformGroups = getPlatformConstraintGroups(groups);

		Assertions.assertThat(new Equals(platformGroups).matches(getPlatformConstraintGroups(groups)));

		final Set<HybrisConstraintViolation> platformViolations = new HashSet<>();
		platformViolations.add(getPlatformErrorOnPrice());
		platformViolations.add(getPlatformViolationOnCode(Severity.WARN));
		when(platformValidationService.validate(any(), eq(platformGroups))).thenReturn(platformViolations);

		final DefaultValidationContext context = new DefaultValidationContext();
		context.setConstraintGroups(groups);
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), context);
		Assertions.assertThat(validate).hasSize(2);
	}

	@Test
	public void testOnObjectWithContextThatHasConfirmedWarningsAndSpecifiedConstraintGroup()
	{
		final List<ValidationGroup> groups = getCockpitConstraintGroups();
		final List<ConstraintGroupModel> platformGroups = getPlatformConstraintGroups(groups);

		final Set<HybrisConstraintViolation> platformViolations = new HashSet<>();
		platformViolations.add(getPlatformErrorOnPrice());
		platformViolations.add(getPlatformViolationOnCode(Severity.WARN));
		when(platformValidationService.validate(any(), eq(platformGroups))).thenReturn(platformViolations);


		final ValidationInfo validationInfo = getConfirmedWarningOnCodeWarning();
		final DefaultValidationContext context = new DefaultValidationContext(new ValidationResult(
				Collections.singletonList(validationInfo)));
		context.setConstraintGroups(groups);

		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), context);
		Assertions.assertThat(validate).hasSize(2);
		Assertions.assertThat(validate.stream().anyMatch(val -> val.isConfirmed())).isTrue();
	}


	@Test
	public void testOnObjectPropertyWithEmptyContext()
	{
		defaultMockForObjectProperty();
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), Lists.newArrayList("code"),
				new DefaultValidationContext());
		Assertions.assertThat(validate).hasSize(1);
		Assertions.assertThat(validate.get(0).isConfirmed()).isFalse();
		Assertions.assertThat(validate.get(0).getInvalidPropertyPath()).isEqualToIgnoringCase("code");
		Assertions.assertThat(validate.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.WARN);
		Assertions.assertThat(validate.get(0).getValidationMessage()).isEqualToIgnoringCase("code too short");
	}

	@Test
	public void testOnLocalizedObjectPropertyWithEmptyContext()
	{
		defaultMockForLocalizedObjectProperty();
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), Lists.newArrayList("name"),
				new DefaultValidationContext());
		Assertions.assertThat(validate).hasSize(1);
		Assertions.assertThat(validate.get(0).isConfirmed()).isFalse();
		Assertions.assertThat(validate.get(0).getInvalidPropertyPath()).isEqualToIgnoringCase(
				String.format("name[%s]", Locale.CHINA.toLanguageTag()));
		Assertions.assertThat(validate.get(0).getValidationSeverity()).isEqualTo(ValidationSeverity.ERROR);
		Assertions.assertThat(validate.get(0).getValidationMessage()).isEqualToIgnoringCase("name too short");
	}

	@Test
	public void testOnObjectPropertyWithContextThatHasConfirmedWarnings()
	{
		defaultMockForObjectProperty();
		final DefaultValidationInfo validationInfo = getConfirmedWarningOnCodeWarning();
		final DefaultValidationContext context = new DefaultValidationContext(new ValidationResult(
				Collections.singletonList(validationInfo)));
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(), Lists.newArrayList("code"),
				context);

		Assertions.assertThat(validate).hasSize(1);
		Assertions.assertThat(validate.get(0).isConfirmed()).isTrue();
	}

	@Test
	public void testOnObjectPropertiesWithContextThatHasConfirmedWarningsAndSpecifiedConstraintGroup()
	{
		final List<ValidationGroup> groups = getCockpitConstraintGroups();
		final List<ConstraintGroupModel> platformGroups = getPlatformConstraintGroups(groups);

		final Set<HybrisConstraintViolation> codeResult = Collections.singleton(getPlatformViolationOnCode(Severity.WARN));
		when(platformValidationService.validateProperty(any(), eq("code"), eq(platformGroups))).thenReturn(codeResult);

		final Set<HybrisConstraintViolation> priceResult = Collections.singleton(getPlatformErrorOnPrice());
		when(platformValidationService.validateProperty(any(), eq("priceQuantity"), eq(platformGroups))).thenReturn(
				priceResult);


		final ValidationInfo validationInfo = getConfirmedWarningOnCodeWarning();
		final DefaultValidationContext context = new DefaultValidationContext(new ValidationResult(
				Collections.singletonList(validationInfo)));

		context.setConstraintGroups(groups);
		final List<ValidationInfo> validateCode = backofficeValidationService.validate(new Object(), Lists.newArrayList("code"),
				context);
		Assertions.assertThat(validateCode).hasSize(1);
		Assertions.assertThat(validateCode.get(0).isConfirmed()).isTrue();
		final List<ValidationInfo> validatePrice = backofficeValidationService.validate(new Object(),
				Lists.newArrayList("priceQuantity"), context);
		Assertions.assertThat(validatePrice).hasSize(1);
	}

	@Test
	public void testOnObjectPropertiesWithSpecifiedConstraintGroup()
	{
		final List<ValidationGroup> groups = getCockpitConstraintGroups();
		final List<ConstraintGroupModel> platformGroups = getPlatformConstraintGroups(groups);

		final Set<HybrisConstraintViolation> platformViolations = new HashSet<>();
		platformViolations.add(getPlatformViolationOnCode(Severity.WARN));
		when(platformValidationService.validateProperty(any(), any(), eq(platformGroups))).thenReturn(platformViolations);

		final DefaultValidationContext context = new DefaultValidationContext();
		context.setConstraintGroups(groups);
		final List<ValidationInfo> validateCode = backofficeValidationService.validate(new Object(), Lists.newArrayList("code"),
				context);
		Assertions.assertThat(validateCode).hasSize(1);
	}

	@Test
	public void testOnVariantObjectPropertyWithEmptyContext()
	{
		defaultMockForObjectProperty();
		final List<ValidationInfo> validate = backofficeValidationService.validate(new Object(),
				Lists.newArrayList("variantAttributesMapModel.field"), new DefaultValidationContext());
		Assertions.assertThat(validate).hasSize(0);
	}

	@Test
	public void testValidateGroups()
	{
		defaultMockForObject(Severity.WARN);
		backofficeValidationService.setValidateGroups(Arrays.asList("test"));
		backofficeValidationService.validate(new Object(), null);
		Mockito.verify(backofficeValidationService.getValidationDao()).getConstraintGroups(Arrays.asList("test"));
	}


	private HybrisConstraintViolation getPlatformViolationOnCode(final Severity severity)
	{
		final ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
		when(constraintViolation.getInvalidValue()).thenReturn("1");


		final HybrisConstraintViolation violation = Mockito.mock(HybrisConstraintViolation.class);
		when(violation.getProperty()).thenReturn("code");
		when(violation.getLocalizedMessage()).thenReturn("code too short");
		when(violation.getViolationSeverity()).thenReturn(severity);
		when(violation.getConstraintViolation()).thenReturn(constraintViolation);
		return violation;
	}

	private HybrisConstraintViolation getPlatformErrorOnPrice()
	{
		final ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
		when(constraintViolation.getInvalidValue()).thenReturn(Integer.valueOf(5));


		final HybrisConstraintViolation violation = Mockito.mock(HybrisConstraintViolation.class);
		when(violation.getProperty()).thenReturn("priceQuantity");
		when(violation.getLocalizedMessage()).thenReturn("price too low");
		when(violation.getViolationSeverity()).thenReturn(Severity.ERROR);
		when(violation.getConstraintViolation()).thenReturn(constraintViolation);
		return violation;
	}

	private LocalizedHybrisConstraintViolation getPlatformLocalizedWarningOnName()
	{
		final ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
		when(constraintViolation.getInvalidValue()).thenReturn("1");


		final LocalizedHybrisConstraintViolation violation = Mockito.mock(LocalizedHybrisConstraintViolation.class);
		when(violation.getProperty()).thenReturn("name");
		when(violation.getLocalizedMessage()).thenReturn("name too short");
		when(violation.getViolationSeverity()).thenReturn(Severity.WARN);
		when(violation.getConstraintViolation()).thenReturn(constraintViolation);
		when(violation.getViolationLanguage()).thenReturn(Locale.CHINA);
		return violation;
	}

	private LocalizedHybrisConstraintViolation getPlatformLocalizedErrorOnName()
	{
		final ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
		when(constraintViolation.getInvalidValue()).thenReturn(Integer.valueOf(5));


		final LocalizedHybrisConstraintViolation violation = Mockito.mock(LocalizedHybrisConstraintViolation.class);
		when(violation.getProperty()).thenReturn("name");
		when(violation.getLocalizedMessage()).thenReturn("name too short");
		when(violation.getViolationSeverity()).thenReturn(Severity.ERROR);
		when(violation.getConstraintViolation()).thenReturn(constraintViolation);
		when(violation.getViolationLanguage()).thenReturn(Locale.CHINA);
		return violation;
	}


	private DefaultValidationInfo getConfirmedWarningOnCodeWarning()
	{
		final DefaultValidationInfo validationInfo = new DefaultValidationInfo();
		validationInfo.setConfirmed(true);
		validationInfo.setValidationSeverity(ValidationSeverity.WARN);
		validationInfo.setInvalidValue("1");
		validationInfo.setInvalidPropertyPath("code");
		validationInfo.setValidationMessage("code too short");
		return validationInfo;
	}

	private List<ConstraintGroupModel> getPlatformConstraintGroups(final List<ValidationGroup> cockpitGroups)
	{
		return cockpitGroups.stream().map(group -> {

			final ConstraintGroupModel model = new ConstraintGroupModel()
			{

				@Override
				public boolean equals(final Object obj)
				{
					return obj instanceof ConstraintGroupModel && ObjectUtils.equals(getId(), ((ConstraintGroupModel) obj).getId());
				}
			};
			model.setId(group.getId());
			return model;

		}).collect(Collectors.toList());
	}

	private List<ValidationGroup> getCockpitConstraintGroups()
	{
		final ValidationGroup adminGroup = new ValidationGroup();
		adminGroup.setId("adminGroup");
		final ValidationGroup lameGroup = new ValidationGroup();
		lameGroup.setId("lameGroup");
		final List<ValidationGroup> list = new ArrayList<>();
		list.add(adminGroup);
		list.add(lameGroup);
		return list;
	}

	private void defaultMockForObjectProperty()
	{
		// default behaviour on object property
		final Set<HybrisConstraintViolation> platformPropertyViolations = new HashSet<>();
		platformPropertyViolations.add(getPlatformViolationOnCode(Severity.WARN));
		when(platformValidationService.validateProperty(any(), any(), eq(Collections.singletonList(defaultGroup))))
				.thenReturn(platformPropertyViolations);
	}

	private void defaultMockForObject(final Severity severity)
	{
		// default behaviour for whole object
		final Set<HybrisConstraintViolation> platformViolations = new HashSet<>();
		platformViolations.add(getPlatformViolationOnCode(severity));
		when(platformValidationService.validate(any(), eq(Collections.singletonList(defaultGroup)))).thenReturn(
				platformViolations);
	}

	private void defaultMockForLocalizedObjectProperty()
	{
		// default behaviour on object property
		final Set<HybrisConstraintViolation> platformPropertyViolations = new HashSet<>();
		platformPropertyViolations.add(getPlatformLocalizedErrorOnName());
		when(platformValidationService.validateProperty(any(), any(), eq(Collections.singletonList(defaultGroup))))

		.thenReturn(platformPropertyViolations);
	}

	private void defaultMockForLocalizedObject()
	{
		// default behaviour for whole object
		final Set<HybrisConstraintViolation> platformViolations = new HashSet<>();
		platformViolations.add(getPlatformLocalizedWarningOnName());
		when(platformValidationService.validate(any(), eq(Collections.singletonList(defaultGroup)))).thenReturn(
				platformViolations);
	}

	private void mockViolationWithoutProperty()
	{
		// default behaviour for whole object
		final Set<HybrisConstraintViolation> platformViolations = new HashSet<>();
		platformViolations.add(getPlatformWarningWithoutProperty());
		when(platformValidationService.validate(any(), eq(Collections.singletonList(defaultGroup)))).thenReturn(
				platformViolations);
	}

	private HybrisConstraintViolation getPlatformWarningWithoutProperty()
	{
		final ConstraintViolation constraintViolation = Mockito.mock(ConstraintViolation.class);
		when(constraintViolation.getInvalidValue()).thenReturn("1");


		final HybrisConstraintViolation violation = Mockito.mock(HybrisConstraintViolation.class);
		when(violation.getProperty()).thenReturn("");
		when(violation.getLocalizedMessage()).thenReturn("code too short");
		when(violation.getViolationSeverity()).thenReturn(Severity.WARN);
		when(violation.getConstraintViolation()).thenReturn(constraintViolation);
		return violation;
	}
}
