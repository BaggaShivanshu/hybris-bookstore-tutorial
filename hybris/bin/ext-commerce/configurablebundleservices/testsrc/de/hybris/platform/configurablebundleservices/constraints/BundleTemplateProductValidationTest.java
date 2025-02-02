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
package de.hybris.platform.configurablebundleservices.constraints;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@IntegrationTest
public class BundleTemplateProductValidationTest extends AbstractBundleValidationTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldWarnAboutEmptyLeafs() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];$catalogversion;parentTemplate(id);status(id)[default='testBundleStatus']\n"
                + ";NestedComponent3;;;NestedGroup1;"
        );
        final Set<HybrisConstraintViolation> violations = validate("NestedComponent3", null);
        assertThat(violations, hasItem(hasProperty(FIELD_MESSAGE, is("Every bundle template should either have child bundle templates or products assigned. Please add either of them."))));
    }

    @Test
    public void shouldAllowLeafWithProducts() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];$catalogversion;parentTemplate(id);products(code, $catalogversion);status(id)[default='testBundleStatus']\n"
                + ";NestedComponent3;;;NestedGroup1;PRODUCT01\n"
        );
    }

    @Test
    public void shouldAllowNonLeafWithoutProducts() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];parentTemplate(id);$catalogversion;version[default=1.0][unique=true];status(id)[default='testBundleStatus']\n"
                + ";NestedComponent3;NestedGroup1\n"
        );
    }

    @Test
    public void shouldWarnAboutEmptyProductListOfLeaf() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];parentTemplate(id);$catalogversion;version[default=1.0][unique=true];status(id)[default='testBundleStatus']\n"
                + ";NestedComponent3;NestedGroup1\n"
        );
        final Set<HybrisConstraintViolation> violations = validate("NestedComponent3", null);
        assertThat(violations, hasItem(hasProperty(FIELD_MESSAGE,
                is("Every bundle template should either have child bundle templates or products assigned. Please add either of them."))));
    }

    @Test
    public void shouldRejectNonLeafWithProducts() throws ImpExException
    {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("Only leaf bundle templates can have products assigned. As this bundle template is not a leaf, please remove the assigned products.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];products(code, $catalogversion);$catalogversion\n"
                + ";NestedGroup1;;PRODUCT01;\n"
        );
    }

}
