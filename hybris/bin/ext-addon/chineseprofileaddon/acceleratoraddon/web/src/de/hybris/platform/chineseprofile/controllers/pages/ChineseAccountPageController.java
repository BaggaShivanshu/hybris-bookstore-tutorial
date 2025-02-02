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
 *
 */
package de.hybris.platform.chineseprofile.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateEmailForm;
import de.hybris.platform.chineseprofile.constants.WebConstants;
import de.hybris.platform.chineseprofile.controllers.ControllerConstants;
import de.hybris.platform.chineseprofile.controllers.pages.imported.AccountPageController;
import de.hybris.platform.chineseprofile.forms.ChineseAddressForm;
import de.hybris.platform.chineseprofile.handlers.ChineseAddressHandler;
import de.hybris.platform.chineseprofilefacades.address.AddressFacade;
import de.hybris.platform.chineseprofilefacades.customer.CustomerFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Scope("tenant")
@RequestMapping("/my-account")
public class ChineseAccountPageController extends AccountPageController
{
	private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";
	private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";
	private static final String REDIRECT_TO_EDIT_ADDRESS_PAGE = AbstractController.REDIRECT_PREFIX + "/my-account/edit-address/";

	@Resource(name = "chineseAddressFacade")
	private AddressFacade chineseAddressFacade;

	@Resource(name = "chineseAddressHandler")
	private ChineseAddressHandler chineseAddressHandler;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "chineseCustomerFacade")
	private CustomerFacade chineseCustomerFacade;

	@Override
	@RequestMapping(value = "/addressform", method = RequestMethod.GET)
	public String getCountryAddressForm(@RequestParam("addressCode") final String addressCode,
			@RequestParam("countryIsoCode") final String country, final Model model)
	{
		super.getCountryAddressForm(addressCode, country, model);
		final ChineseAddressForm chineseAddressForm = chineseAddressHandler.setChineseAddressFormInModel(model);
		chineseAddressHandler.prepareAddressForm(model, chineseAddressForm);
		return ControllerConstants.Views.Fragments.Account.CountryAddressForm;
	}

	@Override
	@RequestMapping(value = "/add-address", method = RequestMethod.GET)
	@RequireHardLogIn
	public String addAddress(final Model model) throws CMSItemNotFoundException
	{
		final String view = super.addAddress(model);
		chineseAddressHandler.setChineseAddressFormInModel(model);
		return view;
	}

	@Override
	@RequireHardLogIn
	@RequestMapping(value = "/edit-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String editAddress(@PathVariable("addressCode") final String addressCode, final Model model)
			throws CMSItemNotFoundException
	{
		final String returnView = super.editAddress(addressCode, model);
		final ChineseAddressForm chineseAddressForm = chineseAddressHandler.setChineseAddressFormInModel(model);
		chineseAddressHandler.prepareAddressForm(model, chineseAddressForm);
		return returnView;
	}

	@RequestMapping(value = "/add-address", method = RequestMethod.POST, params = "countryIso=" + WebConstants.CHINA_ISOCODE)
	@RequireHardLogIn
	public String addAddress(@ModelAttribute("addressForm") final ChineseAddressForm addressForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException
	{
		chineseAddressHandler.validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			prepareAddressFormAfterError(addressForm, model);
			return getViewForPage(model);
		}

		prepareModel(addressForm, model);

		final AddressData newAddress = chineseAddressHandler.prepareAddressData(addressForm);
		if (userFacade.isAddressBookEmpty())
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}
		else
		{
			newAddress.setDefaultAddress(addressForm.getDefaultAddress() != null && addressForm.getDefaultAddress().booleanValue());
		}

		userFacade.addAddress(newAddress);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.added",
				null);
		return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
	}

	@RequestMapping(value = "/edit-address/"
			+ ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.POST, params = "countryIso=" + WebConstants.CHINA_ISOCODE)
	@RequireHardLogIn
	public String editAddress(@ModelAttribute("addressForm") final ChineseAddressForm addressForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
					throws CMSItemNotFoundException
	{
		chineseAddressHandler.validate(addressForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			prepareAddressFormAfterError(addressForm, model);
			return getViewForPage(model);
		}

		prepareModel(addressForm, model);

		final AddressData newAddress = chineseAddressHandler.prepareAddressData(addressForm);
		if (Boolean.TRUE.equals(addressForm.getDefaultAddress()) || userFacade.getAddressBook().size() <= 1)
		{
			newAddress.setDefaultAddress(true);
			newAddress.setVisibleInAddressBook(true);
		}

		userFacade.editAddress(newAddress);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, "account.confirmation.address.updated",
				null);
		return REDIRECT_TO_EDIT_ADDRESS_PAGE + newAddress.getId();
	}

	@Override
	@RequestMapping(value = "/update-email", method = RequestMethod.POST, params = "emailLanguage")
	@RequireHardLogIn
	public String updateEmail(final UpdateEmailForm updateEmailForm, final BindingResult bindingResult, final Model model,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final String emailLanguage = request.getParameter("emailLanguage");
		if (StringUtils.isNotEmpty(emailLanguage))
		{
			chineseCustomerFacade.saveEmailLanguageForCurrentUser(emailLanguage);
		}
		return super.updateEmail(updateEmailForm, bindingResult, model, redirectAttributes);
	}

	protected void prepareAddressFormAfterError(final ChineseAddressForm addressForm, final Model model)
			throws CMSItemNotFoundException
	{
		super.setUpAddressFormAfterError(addressForm, model);
		GlobalMessages.addErrorMessage(model, "form.global.error");
		storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
		if (addressForm.getRegionIso() != null)
		{
			model.addAttribute("cities", chineseAddressFacade.getCitiesForRegion(addressForm.getRegionIso()));
		}
		if (addressForm.getCityIso() != null)
		{
			model.addAttribute("districts", chineseAddressFacade.getDistrictsForCity(addressForm.getCityIso()));
		}
	}

	protected void prepareModel(final ChineseAddressForm addressForm, final Model model)
	{
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
		model.addAttribute("country", addressForm.getCountryIso());
		model.addAttribute("edit", Boolean.TRUE);
		model.addAttribute("isDefaultAddress", Boolean.valueOf(isDefaultAddress(addressForm.getAddressId())));
	}

}
