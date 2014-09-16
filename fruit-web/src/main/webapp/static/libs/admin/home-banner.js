define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});

	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var $http = this.$injector.get('$http');
		$scope.query = {
			globalVisible:null,
			startTime: Date.now(),
			endTime: Date.now(),
			name : null
		};
		$scope.result = {
			banners:null,
			addresses : null
		};
		this.load();
		this.loadAddress();
	};

	angular.extend(PageController.prototype, {
		loadAddress:function(){
			var url = utils.url('/page/pub/address/list.json');
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			$http.get(url).success(function(data) {
				$scope.result.addresses = data.records;
			});
		},
		load : function() {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			var url = utils.url('/admin/home-banner/find-detail.json', $scope.query);
			$http.get(url).success(function(data) {
				$scope.result.banners = data.records;
			});
		},
		removeItem : function(index) {
			if (!window.confirm('确认删除')) {
				return;
			}
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var item = $scope.result.banners[index];
			var homeBannerId = item.homeBannerId;
			var url = utils.url('/admin/home-banner/remove.json', {
				homeBannerId : homeBannerId
			});
			$http['delete'](url).success(function(data) {
				if (data.success) {
					$scope.result.banners.splice(index, 1);
					utils.notifySuccess('删除成功。');
				}
			});
		}
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var ngapp = document.getElementsByTagName('html')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	
	return module;
});