/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */
package de.hybris.platform.chineselogisticfacades.delivery;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.chineselogisticfacades.delivery.data.DeliveryTimeSlotData;

import java.util.List;


/**
 * Facade for DeliveryTimeSlot
 */
public interface DeliveryTimeSlotFacade extends AcceleratorCheckoutFacade
{
	/**
	 * Getting all of the DeliveryTimeSlots
	 *
	 * @return List<DeliveryTimeSlotData>
	 */
	List<DeliveryTimeSlotData> getAllDeliveryTimeSlots();

	/**
	 * Setting the DeliveryTimeSlot into the cartmodel
	 *
	 * @param deliveryTimeSlot
	 *           the code of the DeliveryTimeSlotModel
	 */
	void setDeliveryTimeSlot(String deliveryTimeSlot);
}
