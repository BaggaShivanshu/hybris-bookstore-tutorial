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
package de.hybris.platform.warehousingfacade.stocklevel.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.warehousing.atp.services.WarehouseStockService;
import de.hybris.platform.warehousingfacade.product.data.StockLevelData;
import de.hybris.platform.warehousingfacade.storelocator.data.WarehouseData;
import org.springframework.beans.factory.annotation.Required;


/**
 * Warehousing Converter for converting {@link StockLevelModel}
 */
public class WarehousingStockLevelPopulator implements Populator<StockLevelModel, StockLevelData>
{
	private Converter<WarehouseModel, WarehouseData> warehouseConverter;
	private WarehouseStockService warehouseStockService;

	@Override
	public void populate(final StockLevelModel source, final StockLevelData target) throws ConversionException
	{
		if(source !=null && target !=null)
		{
			target.setInStockStatus(source.getInStockStatus());
			target.setReleaseDate(source.getReleaseDate());
			target.setProductCode(source.getProductCode());
			target.setWarehouse(getWarehouseConverter().convert(source.getWarehouse()));
			target.setBin(source.getBin());
			target.setQuantityOnHand(getWarehouseStockService().getStockLevelForProductCodeAndWarehouse(source.getProductCode(),source.getWarehouse()));
		}
	}

	protected Converter<WarehouseModel, WarehouseData> getWarehouseConverter()
	{
		return warehouseConverter;
	}

	@Required
	public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter)
	{
		this.warehouseConverter = warehouseConverter;
	}

	protected WarehouseStockService getWarehouseStockService()
	{
		return warehouseStockService;
	}

	@Required
	public void setWarehouseStockService(final WarehouseStockService warehouseStockService)
	{
		this.warehouseStockService = warehouseStockService;
	}
}
