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
package de.hybris.platform.warehousingwebservices.controllers.basestore;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.warehousingwebservices.controllers.WarehousingBaseController;
import de.hybris.platform.warehousingwebservices.dto.store.PointOfServiceSearchPageWsDTO;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseSearchPageWsDto;
import de.hybris.platform.warehousingfacade.basestore.WarehousingBaseStoreFacade;
import de.hybris.platform.warehousingfacade.storelocator.data.WarehouseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;


/**
 * WebResource exposing {@link WarehousingBaseStoreFacade}
 * http://host:port/warehousingwebservices/basestores
 */
@Controller
@RequestMapping(value = "/basestores")
public class WarehousingBaseStoresController extends WarehousingBaseController
{
	@Resource
	private WarehousingBaseStoreFacade warehousingBaseStoreFacade;

	/**
	 * Request to get all {@link de.hybris.platform.ordersplitting.model.WarehouseModel} for the given {@value de.hybris.platform.store.BaseStoreModel#UID}
	 *
	 * @param uid
	 * 		baseStore uid to get its warehouses
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return all warehouses for details for the given baseStore's uid
	 */
	@RequestMapping(value = "/{uid}/warehouses", method = RequestMethod.GET)
	@ResponseBody
	public WarehouseSearchPageWsDto getWarehousesForBaseStoreId(@PathVariable final String uid,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<WarehouseData> warehouseSearchPageData = warehousingBaseStoreFacade
				.getWarehousesForBaseStoreId(pageableData, uid);

		return dataMapper.map(warehouseSearchPageData, WarehouseSearchPageWsDto.class, fields);
	}

	/**
	 * Request to get all {@link de.hybris.platform.storelocator.model.PointOfServiceModel} for the given {@value de.hybris.platform.store.BaseStoreModel#UID}
	 *
	 * @param uid
	 * 		baseStore uid to get its stores (Points Of Service)
	 * @param fields
	 * 		defaulted to DEFAULT but can be FULL or BASIC
	 * @param currentPage
	 * 		number of the current page
	 * @param pageSize
	 * 		number of items in a page
	 * @param sort
	 * 		sorting the results ascending or descending
	 * @return all points of service for the given baseStore's uid
	 */
	@RequestMapping(value = "/{uid}/pointofservices", method = RequestMethod.GET)
	@ResponseBody
	public PointOfServiceSearchPageWsDTO getPointsOfServiceForBaseStoreId(@PathVariable final String uid,
			@RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@RequestParam(required = false, defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@RequestParam(required = false, defaultValue = DEFAULT_SORT) final String sort)
	{
		final PageableData pageableData = createPageable(currentPage, pageSize, sort);
		final SearchPageData<PointOfServiceData> pointOfServiceSearchPageData = warehousingBaseStoreFacade
				.getPointsOfServiceForBaseStoreId(pageableData, uid);

		return dataMapper.map(pointOfServiceSearchPageData, PointOfServiceSearchPageWsDTO.class, fields);
	}

}
