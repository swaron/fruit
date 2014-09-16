define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {
			cancelResultMessage:null
		};
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
			var dbName = this.$injector.get('db_name');
			if ($scope.query.orderId) {
				var url = utils.url('/page/order/order-result.json', {
					orderId : $scope.query.orderId
				});
				$http.get(url).success(function(data) {
					$scope.result.order = data;
					if(data.result.orderStatus == dbName.OrderStatus["已取消"]){
						$scope.model.cancelResultMessage = "您好，订单取消成功。";
					}else{
						$scope.model.cancelResultMessage = "订单目前不是取消状态，如有问题，请联系客服。";
					}
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