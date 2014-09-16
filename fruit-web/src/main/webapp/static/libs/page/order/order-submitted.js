define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var me = this;
		$scope.view = {};
		$scope.model = {};
		$scope.query = {
			orderId : utils.getParam('orderId')
		};
		$scope.result = {
			order : null
		};
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if ($scope.query.orderId) {
				var url = utils.url('/page/order/order-result.json', {
					orderId : $scope.query.orderId
				});
				$http.get(url).success(function(data) {
					$scope.result.order = data;
				}).error(function(data) {

				});
			}
		}
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var body = document.getElementsByTagName('body')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;

});