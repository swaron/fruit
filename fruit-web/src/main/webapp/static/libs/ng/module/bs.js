define(['require'], function(require) {
	var module = angular.module('bs', []);
	var tabs = [ function() {
		return {
			restrict : 'AE',
			transclude : true,
			controller : function($scope) {
				var me = this;
				me.tabs = [];
				this.select = function(tab) {
					angular.forEach(me.tabs, function(tab) {
						tab.selected = false;
					});
					tab.selected = true;
				};

//				this.setTabTemplate = function(templateUrl) {
//					$scope.templateUrl = templateUrl;
//				}

				this.addTab = function(tab) {
					if (me.tabs.length == 0) {
						me.select(tab);
					}
					me.tabs.push(tab);
				};
			},
			controllerAs:'tabsCtrl',
			templateUrl: require.toUrl('./bs/tpl/tabs.html')
//			template : '<div class="row-fluid">' + '<div class="row-fluid">'
//					+ '<div class="nav nav-tabs" ng-transclude></div>' + '</div>' + '<div class="row-fluid">'
//					+ '<ng-include src="templateUrl">' + '</ng-include></div>' + '</div>'
		};
	} ];
	var tab = [ function() {
		return {
			restrict : 'AE',
			require : '^bsTabs',
			transclude : true,
			scope : {
				title : '@',
			},
			link : function(scope, element, attrs, tabsController) {
				tabsController.addTab(scope);
				scope.$watch('selected', function() {
					if (scope.selected && scope.templateUrl) {
					}
				});
			},
			template : '<div class="tab-pane" ng-show="selected" ng-transclude></div>'
		};
	} ];
	var tabTpl = [ function() {
		return {
			restrict : 'AE',
			require : '^bsTabs',
			scope : {
				title : '@',
				templateUrl : '@'
			},
			link : function(scope, element, attrs, tabsController) {
				tabsController.addTab(scope);
				scope.$watch('selected', function() {
					if (scope.selected && scope.templateUrl) {
						scope.activeTpl = scope.templateUrl;
					}
				});
			},
			template : '<div class="tab-pane" ng-show="selected" ng-include="activeTpl"></div>'
		};
	} ];
	module.directive('bsTabs', tabs);
	module.directive('bsTab', tab);
	module.directive('bsTabTpl', tabTpl);
});
