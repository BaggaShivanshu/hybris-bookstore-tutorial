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
package de.hybris.platform.accountsummaryaddon.facade;

import de.hybris.platform.accountsummaryaddon.data.AccountSummaryInfoData;
import de.hybris.platform.accountsummaryaddon.document.criteria.DefaultCriteria;
import de.hybris.platform.accountsummaryaddon.document.criteria.FilterByCriteriaData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BAmountBalanceData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentPaymentInfoData;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDragAndDropData;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentModel;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Map;


/**
 *
 * Used to provide unique interface to all B2BDocuments business logic/domain
 *
 */
public interface B2BAccountSummaryFacade
{
	/**
	 * Finds documents filtered by given query.
	 *
	 * @param queryParameters
	 *           the criteria parameters used to build a query
	 * @return all B2BDocumentData filtered by the criteria parameters
	 */
	SearchPageData<B2BDocumentData> findDocuments(final Map<String, String> queryParameters);

	/**
	 * Gets the balance for a given B2B Unit.
	 *
	 * @param unit
	 *           the B2BUnit
	 * @return a B2BAmountBalanceData object.
	 */
	B2BAmountBalanceData getAmountBalance(final B2BUnitModel unit);

	/**
	 * Gets all document types.
	 *
	 * @return all document types: SearchResult<B2BDocumentTypeModel>
	 */
	SearchResult<B2BDocumentTypeModel> getAllDocumentTypes();

	/**
	 * Gets open documents for a given Media Model.
	 *
	 * @return all B2BDocumentModel: SearchResult<B2BDocumentModel>
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel);

	/**
	 *
	 *
	 * Returns a list of document payment info
	 *
	 * @param documentNumber
	 *           the document number identification
	 * @return SearchResult<B2BDocumentPaymentInfoData>
	 */
	List<B2BDocumentPaymentInfoData> getDocumentPaymentInfo(final String documentNumber);


	/**
	 *
	 * Applies a list of drag&drop actions
	 *
	 * @param lstActions
	 */
	public void applyDragAndDropActions(final List<B2BDragAndDropData> lstActions);


	/**
	 * Returns account summary for the given unit
	 *
	 * @param b2bUnitCode
	 *           the B2BUnit
	 * @return a AccountSummaryInfoData object.
	 */
	AccountSummaryInfoData getAccountSummaryInfoData(final String b2bUnitCode);

	/**
	 * @param b2bUnitCode
	 * @param pageableData
	 * @param filterByCriteriaData
	 * @param criteria
	 * @return result : a SeachPageData<B2BDocumentData> containing documents of the given unit & criteria
	 */
	SearchPageData<B2BDocumentData> getPagedDocumentsForUnit(final String b2bUnitCode, final PageableData pageableData,
			final FilterByCriteriaData filterByCriteriaData, final DefaultCriteria criteria);
}
