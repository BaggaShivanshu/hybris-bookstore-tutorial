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
package de.hybris.platform.cmswebservices.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cmswebservices.util.builder.ParagraphComponentModelBuilder;
import de.hybris.platform.cmswebservices.util.dao.impl.ParagraphComponentDao;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class ParagraphComponentModelMother extends AbstractModelMother<CMSParagraphComponentModel>
{

	public static final String UID_HEADER = "uid-paragraph-header";
	public static final String CONTENT_HEADER_EN = "content-paragraph-header-english";
	public static final String CONTENT_HEADER_DE = "content-paragraph-header-deutsch";
	public static final String CONTENT_HEADER_FR = "content-paragraph-header-francais";
	public static final String NAME_HEADER = "name-paragraph-header";

	public static final String UID_FOOTER = "uid-paragraph-footer";
	public static final String CONTENT_FOOTER_EN = "content-paragraph-footer-english";
	public static final String CONTENT_FOOTER_DE = "content-paragraph-footer-deutsch";
	public static final String CONTENT_FOOTER_FR = "content-paragraph-footer-francais";
	public static final String NAME_FOOTER = "name-paragraph-footer";

	private ParagraphComponentDao paragraphComponentDao;

	public CMSParagraphComponentModel createHeaderParagraphComponentModel(CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> paragraphComponentDao.getByUidAndCatalogVersion(UID_HEADER, catalogVersion), //
				() -> ParagraphComponentModelBuilder.aModel() //
						.withUid(UID_HEADER) //
						.withCatalogVersion(catalogVersion) //
						.withContent(CONTENT_HEADER_EN, Locale.ENGLISH) //
						.withName(NAME_HEADER) //
						.build());
	}

	public CMSParagraphComponentModel createFooterParagraphComponentModel(CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> paragraphComponentDao.getByUidAndCatalogVersion(UID_FOOTER, catalogVersion), //
				() -> ParagraphComponentModelBuilder.aModel() //
						.withUid(UID_FOOTER) //
						.withCatalogVersion(catalogVersion) //
						.withContent(CONTENT_FOOTER_EN, Locale.ENGLISH) //
						.withName(NAME_FOOTER) //
						.build());
	}

	public ParagraphComponentDao getParagraphComponentDao()
	{
		return paragraphComponentDao;
	}

	@Required
	public void setParagraphComponentDao(ParagraphComponentDao paragraphComponentDao)
	{
		this.paragraphComponentDao = paragraphComponentDao;
	}

}
