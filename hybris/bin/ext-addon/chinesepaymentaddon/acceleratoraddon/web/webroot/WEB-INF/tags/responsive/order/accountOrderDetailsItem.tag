<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="order" required="true" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="consignment" required="true" type="de.hybris.platform.commercefacades.order.data.ConsignmentData" %>
<%@ attribute name="inProgress" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/addons/chinesepaymentaddon/responsive/order" %>

<div class="well well-tertiary">
	<ycommerce:testId code="orderDetail_itemHeader_section">			
		<div class="well-headline">
            <ycommerce:testId code="orderDetail_consignmentStatus_label">
                <spring:theme code="text.account.order.consignment.status.${consignment.statusDisplay}" />
            </ycommerce:testId>

			<ycommerce:testId code="orderDetail_consignmentStatusDate_label">
				<span class="well-headline-sub">
                    <fmt:formatDate value="${consignment.statusDate}" dateStyle="medium" timeStyle="short" type="both"/>
                </span>
			</ycommerce:testId>
		</div>

        <div class="well-content col-sm-12 col-md-9">
            <c:choose>
                <c:when test="${consignment.deliveryPointOfService ne null}">
                    <ycommerce:testId code="orderDetail_storeDetails_section">
                        <order:storeAddressItem deliveryPointOfService="${consignment.deliveryPointOfService}" inProgress="${inProgress}" statusDate="${consignment.statusDate}"/>
                    </ycommerce:testId>
                </c:when>
                <c:otherwise>
                    <div class="row">
                        <div class="col-sm-6 col-md-4 order-ship-to">
                            <ycommerce:testId code="orderDetail_deliveryAddress_section">
                                <div class="label-order"><spring:theme code="text.account.order.shipto"/></div>
                                <div class="value-order"><order:addressItem address="${orderData.deliveryAddress}"/></div>
                            </ycommerce:testId>
                        </div>

                        <div class="col-sm-6 col-md-4 order-shipping-method">
                            <ycommerce:testId code="orderDetail_deliveryMethod_section">
                                <order:deliveryMethodItem order="${orderData}"/>
                            </ycommerce:testId>
                        </div>
                    </div>

                   <c:if test="${not inProgress}">
                        <c:choose>
                            <c:when test="${consignment.status.code eq 'SHIPPED' and not empty consignment.trackingID}" >
                                <div class="col-sm-4 order-tracking-no">
                                    <ycommerce:testId code="orderDetail_trackingId_label">
                                        <span class="label-order"><spring:theme code="text.account.order.tracking" text="Tracking No." /></span>
                                        <br>
                                        <span class="order-track-number">${consignment.trackingID}</span>
                                    </ycommerce:testId>
                                </div>
                            </c:when>
                        </c:choose>
                   </c:if>
                </c:otherwise>
            </c:choose>
        </div>
	</ycommerce:testId>
</div>
	
<ul class="product-list">
	<ycommerce:testId code="orderDetail_itemBody_section">
		<c:forEach items="${consignment.entries}" var="entry" varStatus="loop">
			<order:orderEntryDetails orderEntry="${entry.orderEntry}" consignmentEntry="${entry}" order="${order}" itemIndex="${loop.index}"/>
		</c:forEach>
	</ycommerce:testId>
</ul>