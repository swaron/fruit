define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});

	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {};
		$scope.query = {};
		$scope.result = {};
//		this.load();
	};

	angular.extend(PageController.prototype, {
		submitRedirect : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/system/console/submit-redirect.json', $scope.view);
			$http.get(url).success(function(data) {
				if (data.success) {
					utils.notifySuccess('提交成功');
				} else {
				}
			});
		},
		cancelRedirect : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/system/console/cancel-redirect.json', $scope.view);
			$http.get(url).success(function(data) {
				if (data.success) {
					utils.notifySuccess('取消成功');
				} else {
				}
			});
		}
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	return module;
});