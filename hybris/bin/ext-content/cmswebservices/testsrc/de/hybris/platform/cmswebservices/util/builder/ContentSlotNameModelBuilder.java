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
package de.hybris.platform.cmswebservices.util.builder;

import de.hybris.platform.cms2.model.ComponentTypeGroupModel;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;


public class ContentSlotNameModelBuilder
{

	private final ContentSlotNameModel model;

	private ContentSlotNameModelBuilder()
	{
		model = new ContentSlotNameModel();
	}

	private ContentSlotNameModelBuilder(ContentSlotNameModel model)
	{
		this.model = model;
	}

	private ContentSlotNameModel getModel()
	{
		return this.model;
	}

	public static ContentSlotNameModelBuilder aModel()
	{
		return new ContentSlotNameModelBuilder();
	}

	public static ContentSlotNameModelBuilder fromModel(ContentSlotNameModel model)
	{
		return new ContentSlotNameModelBuilder(model);
	}

	public ContentSlotNameModelBuilder withName(String name)
	{
		getModel().setName(name);
		return this;
	}

	public ContentSlotNameModelBuilder withTemplate(PageTemplateModel template)
	{
		getModel().setTemplate(template);
		return this;
	}

	public ContentSlotNameModelBuilder withCompTypeGroup(ComponentTypeGroupModel compTypeGroup)
	{
		getModel().setCompTypeGroup(compTypeGroup);
		return this;
	}

	public ContentSlotNameModel build()
	{
		return this.getModel();
	}
}
