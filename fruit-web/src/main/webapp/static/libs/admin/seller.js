define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});

	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var $http = this.$injector.get('$http');
		$scope.query = {
			name : null
		};
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			var url = utils.url('/admin/seller/find.json',{
				name:$scope.query.name
			});
			$http.get(url).success(function(data) {
				$scope.result = data;
			});
		},
		removeItem : function(index) {
			if (!window.confirm('确认删除')) {
				return;
			}
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var item = $scope.result.records[index];
			var sellerId = item.sellerId;
			var name = item.name;
			var url = utils.url('/admin/seller/remove.json', {
				sellerId : sellerId,
				name : name
			});
			$http['delete'](url).success(function(data) {
				if (data.success) {
					$scope.result.records.splice(index, 1);
					utils.notifySuccess('删除成功。');
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