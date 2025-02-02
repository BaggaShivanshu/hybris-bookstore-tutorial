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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;


import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayUtil;
import de.hybris.platform.chinesepspalipayservices.alipay.HttpProtocolHandler;
import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.data.HttpRequest;
import de.hybris.platform.chinesepspalipayservices.data.HttpResponse;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayResponseValidationStrategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;



public class DefaultAlipayResponseValidationStrategy implements AlipayResponseValidationStrategy
{

	private AlipayConfiguration alipayConfiguration;

	@Override
	public boolean validateResponse(final Map<String, String> params)
	{
		return validateNotifyId(params, alipayConfiguration.getWebPartner())
				&& validateSign(params, alipayConfiguration.getWebKey());
	}


	protected boolean validateSign(final Map<String, String> params, final String key)
	{
		final String mySign = AlipayUtil.buildMysign(AlipayUtil.paraFilter(params), key, "MD5");
		final String currentSign = params.get("sign");
		return mySign.equals(currentSign);

	}


	protected boolean validateNotifyId(final Map<String, String> params, final String partner)
	{
		final String notifyId = params.get("notify_id");
		final String validateUrl = getAlipayConfiguration().getHttpsVerifyUrl() + "partner=" + partner + "&" + "notify_id="
				+ notifyId;

		final HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance(alipayConfiguration.getTrustGateway());
		final HttpRequest request = new HttpRequest();
		request.setUrl(validateUrl);
		request.setMethod(PaymentConstants.HTTP.METHOD_GET);
		final HttpResponse response = httpProtocolHandler.execute(request);
		return Boolean.parseBoolean(response.getStringResult());


	}



	protected AlipayConfiguration getAlipayConfiguration()
	{
		return alipayConfiguration;
	}


	@Required
	public void setAlipayConfiguration(final AlipayConfiguration alipayConfiguration)
	{
		this.alipayConfiguration = alipayConfiguration;
	}


}
