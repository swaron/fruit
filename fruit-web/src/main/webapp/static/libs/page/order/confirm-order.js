define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ]);
	var ConfirmController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var me = this;
		// $scope._cache = this.$injector.get('_cache');
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

	angular.extend(ConfirmController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if ($scope.query.orderId) {
				var url = utils.url('/page/order/order-detail.json', {
					orderId : $scope.query.orderId
				});
				$http.get(url).success(function(data) {
					$scope.result.order = data.result;
				}).error(function(data) {

				});
			}
		},
		submitOrder : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/page/order/submit-order.json', {

			});
			var sdata = {
				order : $scope.result.order.order
			};
			$http.post(url, sdata).success(function(data) {
				if (data.success) {

				} else {
					alert(data.message);
				}
				if (data.result && data.result.orderId) {
					// 订单提交成功了
					window.location.href = utils.url('/page/order/order-submitted.html', {
						orderId : data.result.orderId
					})
				}
			}).error(function(data) {

			});
		},
		getTotalMoney : function() {
			var $scope = this.$scope;
			var items = $scope.$eval('result.order.products');
			if (items == null || !items.length) {
				return "";
			}
			var sum = 0.0;
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				sum += (item.quantity * item.price)
			}
			return sum;
		},
	});
	module.controller('ConfirmController', [ '$injector', '$scope', ConfirmController ]);

	var body = document.getElementsByTagName('body')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;

});