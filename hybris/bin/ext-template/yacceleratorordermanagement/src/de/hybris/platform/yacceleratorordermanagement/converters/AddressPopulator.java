/*
 *  
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
 */
package de.hybris.platform.yacceleratorordermanagement.converters;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.data.AddressData;
import org.springframework.util.Assert;

/**
 * Converter implementation for {@link de.hybris.platform.core.model.user.AddressModel} as source and
 * {@link de.hybris.platform.storelocator.data.AddressData} as target type.
 */
public class AddressPopulator implements Populator<AddressModel, AddressData>
{
    @Override
    public void populate(final AddressModel source, final AddressData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getCountry() != null)
        {
            target.setCountryCode(source.getCountry().getIsocode());
        }
        target.setCity(source.getDistrict());
        target.setStreet(source.getStreetname());
        target.setZip(source.getPostalcode());
    }
}
