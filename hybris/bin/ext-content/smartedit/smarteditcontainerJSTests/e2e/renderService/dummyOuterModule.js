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
(function() {
    angular.module('outerModule', ['renderServiceModule'])
        .controller("DefaultCtrl", defaultCtrl);

    function defaultCtrl($log, renderService) {
        var vm = this;
        vm.renderNewContent = function() {
            renderService.renderComponent("smartEditComponent1", "outerContentType", "New Content From Outside")
                .then(
                    function() {
                        $log.info("Content updated. ");
                    },
                    function(result) {
                        $log.error("Couldn't update content. Error: " + result);
                    });
        };
    }

})();
