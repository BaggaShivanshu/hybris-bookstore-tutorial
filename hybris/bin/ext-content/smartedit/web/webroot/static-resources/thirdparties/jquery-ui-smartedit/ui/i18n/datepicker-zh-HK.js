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
/* Written by SCCY (samuelcychan@gmail.com). */
(function( factory ) {
	if ( typeof define === "function" && define.amd ) {

		// AMD. Register as an anonymous module.
		define([ "../datepicker" ], factory );
	} else {

		// Browser globals
		factory( jQuery.datepicker );
	}
}(function( datepicker ) {

datepicker.regional['zh-HK'] = {
	closeText: '關閉',
	prevText: '&#x3C;上月',
	nextText: '下月&#x3E;',
	currentText: '今天',
	monthNames: ['一月','二月','三月','四月','五月','六月',
	'七月','八月','九月','十月','十一月','十二月'],
	monthNamesShort: ['一月','二月','三月','四月','五月','六月',
	'七月','八月','九月','十月','十一月','十二月'],
	dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
	dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
	dayNamesMin: ['日','一','二','三','四','五','六'],
	weekHeader: '周',
	dateFormat: 'dd-mm-yy',
	firstDay: 0,
	isRTL: false,
	showMonthAfterYear: true,
	yearSuffix: '年'};
datepicker.setDefaults(datepicker.regional['zh-HK']);

return datepicker.regional['zh-HK'];

}));
