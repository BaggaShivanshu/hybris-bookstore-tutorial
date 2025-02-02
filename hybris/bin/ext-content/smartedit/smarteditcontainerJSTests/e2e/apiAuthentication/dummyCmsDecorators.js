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
angular.module('FakeModule', [
        'fake1Decorator',
        'fake2Decorator'
    ])
    .run(function(decoratorService) {

        decoratorService.addMappings({
            'componentType1': ['fake1'],
            'componentType2': ['fake2']
        });
    });
angular.module('fake1Decorator', ['decoratortemplates', 'ngResource'])
    .directive('fake1', ['$timeout', '$resource', function($timeout, $resource) {
        return {
            templateUrl: 'web/features/fake1Decorator/fake1DecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },

            link: function($scope, element, attrs) {
                console.info("fake1Decorator decorator created");

                $scope.found = false;
                var apiUri = "https://api1/somepath/id";
                console.info("fake1Decorator call api: " + apiUri);
                $resource(apiUri).get().$promise.then(function() {
                    $scope.found = true;
                    console.info("fake1Decorator get api1 success");
                }, function() {
                    console.info("fake1Decorator get api1 failed");

                });
                $scope.visible = false;
                $scope.mouseleave = function() {
                    $timeout(function() {
                        $scope.visible = false;
                    }, 1000);
                };
                $scope.mouseenter = function() {
                    $scope.visible = true;
                };

            }
        };
    }]);
angular.module('fake2Decorator', ['decoratortemplates', 'ngResource'])
    .directive('fake2', ['$timeout', '$resource', function($timeout, $resource) {
        return {
            templateUrl: 'web/features/fake2Decorator/fake2DecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            scope: {
                smarteditComponentId: '@',
                smarteditComponentType: '@',
                active: '='
            },

            link: function($scope, element, attrs) {
                console.info("fake2Decorator decorator created");

                $scope.found = false;
                var apiUri = "https://api2/someotherpath/id";
                console.info("fake2Decorator call api: " + apiUri);
                $timeout(function() { //so that we make sure login 2 pops up after login 1
                    $resource(apiUri).get().$promise.then(function() {
                        $scope.found = true;
                        console.info("fake2Decorator get api2 success");

                    }, function() {
                        console.info("fake2Decorator get api2 failed");

                    });
                }, 500);
                $scope.visible = false;
                $scope.mouseleave = function() {
                    $timeout(function() {
                        $scope.visible = false;
                    }, 1000);
                };
                $scope.mouseenter = function() {
                    $scope.visible = true;
                };

            }
        };
    }]);
angular.module('decoratortemplates', []).run(['$templateCache', function($templateCache) {
    'use strict';

    $templateCache.put('web/features/fake1Decorator/fake1DecoratorTemplate.html',
        "<div>\n" +
        "    <div data-ng-mouseleave=\"mouseleave()\">\n" +
        "        <div data-ng-mouseenter=\"mouseenter()\">\n" +
        "            <div data-ng-transclude></div>\n" +
        "               <span id='fake1' data-ng-if='found'>fake1</span>" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>"
    );


    $templateCache.put('web/features/fake2Decorator/fake2DecoratorTemplate.html',
        "<div>\n" +
        "    <div data-ng-mouseleave=\"mouseleave()\">\n" +
        "        <div data-ng-mouseenter=\"mouseenter()\">\n" +
        "            <div data-ng-transclude></div>\n" +
        "                <span id='fake2' data-ng-if='found'>fake2</span>" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>"
    );

}]);
