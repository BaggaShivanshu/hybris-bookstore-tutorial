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
package de.hybris.platform.ycommercewebservices.request.redirection;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


/**
 * This filter was created only because of compatibility reason and is not turn on by default.<br/>
 * It can be add in web.xml to inform occ clients that oauth token request was moved to different location.<br/>
 * Example : <br/>
 *
 * <pre>
 * {@code
 * 	<filter>
 * 		<filter-name>oauthRedirectionFilter</filter-name>
 *    	<filter-class>de.hybris.platform.ycommercewebservices.request.redirection.OAuthRedirectionFilter</filter-class>
 * 	</filter>
 *
 * 	<filter-mapping>
 * 		<filter-name>oauthRedirectionFilter</filter-name>
 * 		<url-pattern>/oauth/*</url-pattern>
 * 	</filter-mapping>
 * }
 * </pre>
 *
 * Filter can be also parameterized : <br/>
 *
 * <pre>
 * {@code
 * 	<filter>
 * 		<filter-name>oauthRedirectionFilter</filter-name>
 * 		<filter-class>de.hybris.platform.ycommercewebservices.request.redirection.OAuthRedirectionFilter</filter-class>
 * 		<init-param>
 * 			<param-name>redirectStatus</param-name>
 * 			<param-value>307</param-value>
 * 		</init-param>
 * 		<init-param>
 * 			<param-name>oauthWebRoot</param-name>
 * 			<param-value>/authorizationserver</param-value>
 * 		</init-param>
 * 		<init-param>
 * 			<param-name>oauthServer</param-name>
 * 			<param-value>localhost</param-value>
 * 		</init-param>
 * 	</filter>
 *
 * 	<filter-mapping>
 * 		<filter-name>oauthRedirectionFilter</filter-name>
 * 		<url-pattern>/oauth/*</url-pattern>
 * 	</filter-mapping>
 * }
 * </pre>
 *
 */
public class OAuthRedirectionFilter implements Filter
{
	private int redirectStatus = HttpServletResponse.SC_TEMPORARY_REDIRECT;
	private String oauthWebRoot = "/authorizationserver";
	private String oauthServer;

	@Override
	public void init(final FilterConfig config) throws ServletException
	{
		final String status = config.getInitParameter("redirectStatus");
		if (status != null)
		{
			redirectStatus = Integer.parseInt(status);
		}

		final String oauthWebRoot = config.getInitParameter("oauthWebRoot");
		if (oauthWebRoot != null)
		{
			this.oauthWebRoot = oauthWebRoot;
		}

		final String oauthServer = config.getInitParameter("oauthServer");
		if (oauthServer != null)
		{
			this.oauthServer = oauthServer;
		}
	}

	@Override
	public void destroy()
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;

		final String newUrl = getFinalUrl(httpRequest);

		httpResponse.setStatus(redirectStatus);
		httpResponse.addHeader("Location", newUrl);
	}

	protected String getFinalUrl(final HttpServletRequest httpRequest)
	{
		String uri = httpRequest.getRequestURI();
		uri = uri.replace(httpRequest.getContextPath(), oauthWebRoot);
		final String queryString = sanitizeQueryString(httpRequest.getQueryString());
		if (queryString != null)
		{
			uri += "?" + queryString;
		}

		final String url = getAbsoluteURL(httpRequest, uri, oauthServer);
		return url;
	}

	protected String getAbsoluteURL(final HttpServletRequest httpRequest, final String url, String serverName)
	{
		if (url == null)
		{
			return null;
		}
		if (url.indexOf("://") != -1)
		{
			return url;
		}

		final String scheme = httpRequest.getScheme();
		if (serverName == null)
		{
			serverName = httpRequest.getServerName();
		}
		final int port = httpRequest.getServerPort();
		final boolean slashLeads = url.startsWith("/");

		String absoluteURL = scheme + "://" + serverName;

		if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443))
		{
			absoluteURL += ":" + port;
		}
		if (!slashLeads)
		{
			absoluteURL += "/";
		}

		absoluteURL += url;

		return absoluteURL;
	}

	protected static String sanitizeQueryString(final String queryString)
	{
		// clean input
		String output = StringUtils.defaultString(queryString).trim();
		// remove CRLF injection
		output = output.replaceAll("(\\r\\n|\\r|\\n|%0D|%0d|%0A|%0a)+", "");
		return output;
	}
}
