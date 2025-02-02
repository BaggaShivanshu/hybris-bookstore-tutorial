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
package de.hybris.platform.b2ctelcostorefront.controllers.pages;

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.UpdateQuantityForm;
import de.hybris.platform.b2ctelcofacades.bundle.GuidedSellingFacade;
import de.hybris.platform.b2ctelcofacades.data.BundleTabData;
import de.hybris.platform.b2ctelcofacades.product.TelcoProductFacade;
import de.hybris.platform.b2ctelcoservices.model.DeviceModel;
import de.hybris.platform.b2ctelcoservices.model.ServiceAddOnModel;
import de.hybris.platform.b2ctelcoservices.model.ServicePlanModel;
import de.hybris.platform.b2ctelcostorefront.controllers.TelcoControllerConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.enums.BundleTemplateStatusEnum;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for guided selling flows like the extras page.
 */
@Controller
@RequestMapping(value = "/bundle")
public class TelcoGuidedSellingController extends AbstractSearchPageController
{
	private static final String GUIDEDSELLING_SELECT_ADDONS = "bundleselection-extra";
	private static final String GUIDEDSELLING_SELECT_DEVICE = "guidedselling-select-device";
	private static final String GUIDEDSELLING_BUNDLE_PLAN = "bundleselection-plan";

	private static final String TYPE_MISMATCH_CODE = "typeMismatch";

	private static final Logger LOG = LoggerFactory.getLogger(TelcoGuidedSellingController.class);

	/**
	 * Component navigation.
	 */
	public static enum ComponentNavigation
	{
		CURRENT(""), NEXT("next"), PREVIOUS("prev");

		final String code;

		private ComponentNavigation(final String code)
		{
			this.code = code;
		}
	}

	@Resource(name = "bundleTemplateService")
	private BundleTemplateService bundleTemplateService;

	@Resource(name = "guidedSellingFacade")
	private GuidedSellingFacade guidedSellingFacade;

	@Resource(name = "cartFacade")
	private BundleCartFacade cartFacade;

	@Resource(name = "telcoProductFacade")
	private TelcoProductFacade productFacade;

	@RequestMapping(value = "/view-plans/{bundleTemplateId}", method = RequestMethod.GET)
	public String viewPlans(@PathVariable("bundleTemplateId") final String bundleTemplateId, final Model model)
			throws CMSItemNotFoundException
	{
		BundleTemplateModel bundleTemplateModel = null;
		try
		{
			bundleTemplateModel = bundleTemplateService.getBundleTemplateForCode(bundleTemplateId);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("Specified bundleTemplate does not exist: "+bundleTemplateId);
			setupErrorPage(model, GUIDEDSELLING_BUNDLE_PLAN, "guidedselling.viewplans.packagenotavailable");
			return TelcoControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}

		if (!BundleTemplateStatusEnum.APPROVED.equals(bundleTemplateModel.getStatus().getStatus()))
		{
			LOG.error("Specified bundleTemplate is not currently APPROVED: "+bundleTemplateId);
			setupErrorPage(model, GUIDEDSELLING_BUNDLE_PLAN, "guidedselling.viewplans.packagenotavailable");
			return TelcoControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}

		final List<BundleTemplateModel> bundleTemplates = bundleTemplateService.getAllComponentsOfType(bundleTemplateModel,
				ServicePlanModel.class);
		if (bundleTemplates.isEmpty())
		{
			LOG.error("Specified bundleTemplate does not have any active plan "+bundleTemplateId);
			setupErrorPage(model, GUIDEDSELLING_BUNDLE_PLAN, "guidedselling.viewplans.packagenotavailable");
			return TelcoControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}

		final BundleTemplateModel firstComponentModel = bundleTemplates.get(0);
		final List<ProductModel> products = firstComponentModel.getProducts();
		if (products.isEmpty())
		{
			LOG.error("bundleTemplate does not contain any Product "+bundleTemplateId);
			setupErrorPage(model, GUIDEDSELLING_BUNDLE_PLAN, "guidedselling.viewplans.packagenotavailable");
			return TelcoControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}

		ServicePlanModel firstPlan = null;
		try
		{
			firstPlan = (ServicePlanModel) products.get(0);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			setupErrorPage(model, GUIDEDSELLING_BUNDLE_PLAN, "guidedselling.viewplans.packagenotavailable");
			return TelcoControllerConstants.Views.Pages.Error.ErrorNotFoundPage;
		}

		final ProductData productData = productFacade.getProductForOptions(firstPlan,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS,
						ProductOption.CLASSIFICATION, ProductOption.VARIANT_FULL, ProductOption.STOCK,
						ProductOption.SERVICE_PLAN_BUNDLE_TABS));

		// change selected package to the one that is passed as a parameter
		final List<BundleTabData> bundleTabs = productData.getBundleTabs();

		for (final BundleTabData bundleTab : bundleTabs)
		{
			bundleTab.setPreselected(bundleTemplateId.equals(bundleTab.getParentBundleTemplate().getId()));
		}
		model.addAttribute("bundleTabs", bundleTabs);

		storeCmsPageInModel(model, getContentPageForLabelOrId(GUIDEDSELLING_BUNDLE_PLAN));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUIDEDSELLING_BUNDLE_PLAN));
		return TelcoControllerConstants.Views.Pages.GuidedSelling.ViewAllServicePlansPage;
	}

	private void setupErrorPage(final Model model, final String label, final String errorMessage) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(label));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(label));
		GlobalMessages.addErrorMessage(model, errorMessage);
	}

	/**
	 * Edit component page.
	 *
	 * @param bundleNo
	 * @param componentId
	 * @param searchQuery
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param request
	 * @param model
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/edit-component/{bundleNo}/component/{componentId}", method = RequestMethod.GET)
	public String editComponent(@PathVariable("bundleNo") final String bundleNo,
			@PathVariable("componentId") final String componentId,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final HttpServletRequest request,
			final Model model) throws CMSItemNotFoundException
	{
		return internalEditComponent(bundleNo, componentId, searchQuery, page, showMode, sortCode, request, model);
	}

	/**
	 * Edit component page.
	 *
	 * @param bundleNo
	 * @param componentId
	 * @param searchQuery
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/edit-component/{bundleNo}/nextcomponent/{componentId}", method = RequestMethod.GET)
	public String editNextComponent(@PathVariable("bundleNo") final String bundleNo,
			@PathVariable("componentId") final String componentId,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final HttpServletRequest request,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		return editPositionalComponent(bundleNo, componentId, searchQuery, page, showMode, sortCode, request, model, 1,
				redirectAttributes);
	}

	/**
	 * Preview component page.
	 *
	 * @param bundleNo
	 * @param componentId
	 * @param searchQuery
	 * @param page
	 * @param showMode
	 * @param sortCode
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/edit-component/{bundleNo}/prevcomponent/{componentId}", method = RequestMethod.GET)
	public String editPreviousComponent(@PathVariable("bundleNo") final String bundleNo,
			@PathVariable("componentId") final String componentId,
			@RequestParam(value = "q", required = false) final String searchQuery,
			@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final HttpServletRequest request,
			final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		return editPositionalComponent(bundleNo, componentId, searchQuery, page, showMode, sortCode, request, model, -1,
				redirectAttributes);
	}

	/**
	 * Generic method to edit a component at a relative position. It resolves the relative component and checks if it is
	 * valid to navigate there.
	 */
	protected String editPositionalComponent(final String bundleNo, final String componentId, final String searchQuery,
			final int page, final ShowMode showMode, final String sortCode, final HttpServletRequest request, final Model model,
			final int relativeposition, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		String componentIdToNavigateTo = null;

		if (guidedSellingFacade.checkIsComponentSelectionCriteriaMet(bundleNo, componentId))
		{
			componentIdToNavigateTo = guidedSellingFacade.getRelativeComponentId(bundleNo, componentId, relativeposition);
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"basket.next.selection.criteria.not.met");
			componentIdToNavigateTo = componentId;

			final HttpServletRequest previousRequest = (HttpServletRequest) model.asMap().get("request");
			final String previousUrl = previousRequest.getHeader("referer");
			final int pos = previousUrl.indexOf(previousRequest.getContextPath()) + previousRequest.getContextPath().length();
			final String redirectUrl = previousUrl.substring(pos, previousUrl.length());

			return REDIRECT_PREFIX + redirectUrl;
		}

		if (componentIdToNavigateTo == null)
		{
			return REDIRECT_PREFIX + "/cart";
		}

		return internalEditComponent(bundleNo, componentIdToNavigateTo, searchQuery, page, showMode, sortCode, request, model);
	}

	/**
	 * Populates the dashboard and the appropriate step of the guided selling flow.
	 */
	protected String internalEditComponent(final String bundleNo, final String componentId, final String searchQuery,
			final int page, final ShowMode showMode, final String sortCode, final HttpServletRequest request, final Model model)
					throws CMSItemNotFoundException
	{
		model.addAttribute("dashboard", guidedSellingFacade.getDashboard(Integer.parseInt(bundleNo), componentId));

		final String productType = guidedSellingFacade.getComponentProductType(componentId);

		if (productType == null)
		{
			// we have an invalid (empty?) component here: do not try to edit it but go to cart
			return REDIRECT_PREFIX + "/cart";
		}

		model.addAttribute("productType", productType);
		switch (productType)
		{
			case DeviceModel._TYPECODE:
			case ServicePlanModel._TYPECODE:
				final String urlPrefix = "/bundle/edit-component/" + bundleNo + "/component/" + componentId + "?q=";
				final PageableData pageableData = createPageableData(page, getSearchPageSize(), sortCode, showMode);
				final ProductSearchPageData<SearchStateData, ProductData> searchPageData = guidedSellingFacade
						.bundleSearch(pageableData, searchQuery, urlPrefix, componentId, Integer.valueOf(bundleNo));

				model.addAttribute("urlPrefix", urlPrefix);
				model.addAttribute("pageType", PageType.PRODUCTSEARCH);
				model.addAttribute("componentId", componentId);
				populateModel(model, searchPageData, showMode);

				storeCmsPageInModel(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_DEVICE));
				setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_DEVICE));
				return TelcoControllerConstants.Views.Pages.GuidedSelling.EditComponentSolrStylePage;
			case ServiceAddOnModel._TYPECODE:
				model.addAttribute("bundleTemplateData", guidedSellingFacade.getComponentToEdit(bundleNo, componentId));
				storeCmsPageInModel(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_ADDONS));
				setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GUIDEDSELLING_SELECT_ADDONS));
				return TelcoControllerConstants.Views.Pages.GuidedSelling.EditComponentAccordeonStylePage;
			default:
				return null;
		}
	}

	/**
	 * Add product to cart.
	 *
	 * @param code
	 * @param model
	 * @param form
	 * @param bundleNo
	 * @param bundleTemplateId
	 * @param removeCurrentProducts
	 * @param navigation
	 * @param bindingResult
	 * @param redirectModel
	 * @return
	 */
	@RequestMapping(value = "/addEntry", method = RequestMethod.POST)
	public String addToCart(@RequestParam("productCodePost") final String code, final Model model,
			@Valid final UpdateQuantityForm form, @RequestParam(value = "bundleNo", required = false) final Integer bundleNo,
			@RequestParam(value = "bundleTemplateId", required = false) final String bundleTemplateId,
			@RequestParam(value = "removeCurrentProducts", required = false, defaultValue = "false")
				final boolean removeCurrentProducts,
			@RequestParam("navigation") final ComponentNavigation navigation, final BindingResult bindingResult,
			final RedirectAttributes redirectModel)
	{
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				if (TYPE_MISMATCH_CODE.equals(error.getCode()))
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, error.getDefaultMessage());
				}
			}
		}

		int finalBundleNo = addProduct(code, model, form.getQuantity().intValue(), bundleNo, bundleTemplateId,
				removeCurrentProducts, redirectModel);

		if (finalBundleNo < 0)
		{
			finalBundleNo = bundleNo == null ? 0 : bundleNo.intValue();
		}

		return REDIRECT_PREFIX + "/bundle/edit-component/" + finalBundleNo + "/" + navigation.code + "component/"
				+ bundleTemplateId;
	}

	protected int addProduct(final String code, final Model model, final long qty, final Integer bundleNo,
			final String bundleTemplateId, final boolean removeCurrentProducts, final RedirectAttributes redirectModel)
	{

		try
		{
			final List<CartModificationData> cartModifications = cartFacade.addToCart(code, qty, bundleNo.intValue(),
					bundleTemplateId, removeCurrentProducts);
			model.addAttribute("modifiedCartData", cartModifications);

			for (final CartModificationData cartModification : cartModifications)
			{

				if (cartModification.getQuantityAdded() == 0L)
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
				}
				else if (cartModification.getQuantityAdded() < qty)
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
							"basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
				}
				else if (cartModification.getQuantityAdded() > 0)
				{
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
							"guidedselling.basket.page.message.added");

					return cartModification.getEntry().getBundleNo();
				}
			}
		}
		catch (final CommerceCartModificationException ex)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.error.occurred");
			LOG.warn("Couldn't add product of code " + code + " to cart.", ex);
		}

		return -2;
	}


	/**
	 * Remove product from cart.
	 *
	 * @param entryNumber
	 * @param model
	 * @param form
	 * @param bundleNo
	 * @param componentId
	 * @param bindingResult
	 * @param redirectModel
	 * @return
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/removeEntry", method = RequestMethod.POST)
	public String updateCartQuantities(@RequestParam("entryNumber") final long entryNumber, final Model model,
			@Valid final UpdateQuantityForm form, @RequestParam("bundleNo") final String bundleNo,
			@RequestParam("componentId") final String componentId, final BindingResult bindingResult,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		if (bindingResult.hasErrors())
		{
			for (final ObjectError error : bindingResult.getAllErrors())
			{
				if (error.getCode().equals("typeMismatch"))
				{
					GlobalMessages.addErrorMessage(model, "basket.error.quantity.invalid");
				}
				else
				{
					GlobalMessages.addErrorMessage(model, error.getDefaultMessage());
				}
			}
		}
		else if (cartFacade.getSessionCart().getEntries() != null)
		{
			try
			{
				final CartModificationData cartModification = cartFacade.updateCartEntry(entryNumber, form.getQuantity().longValue());
				if (cartModification.getQuantity() == form.getQuantity().longValue())
				{
					// Success

					if (cartModification.getQuantity() == 0)
					{

						// Success in removing entry
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
								"guidedselling.basket.page.message.removed");
					}
					else
					{
						// Success in update quantity
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
								"basket.page.message.update");
					}
				}
				else
				{
					// Less than successful

					if (form.getQuantity().longValue() == 0)
					{
						// Failed to remove entry
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
								"basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
					}
					else
					{
						// Failed to update quantity
						GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
								"basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
					}
				}

				return REDIRECT_PREFIX + "/bundle/edit-component/" + bundleNo + "/component/" + componentId;
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + entryNumber + ".", ex);
				return REDIRECT_PREFIX + "/bundle/edit-component/" + bundleNo + "/component/" + componentId;
			}
		}

		return REDIRECT_PREFIX + "/cart";
	}
}
