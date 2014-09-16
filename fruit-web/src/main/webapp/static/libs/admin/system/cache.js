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
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			// var me = this;
			var url = utils.url('/admin/system/cache/list-caches.json', {
			});
			$http.get(url).success(function(data) {
				if (data.success && data.result != null) {
					$scope.result.caches = data.result;
					$scope.result.cacheLength = Object.keys( data.result).length;
				} else {
				}
			});
		},
		updateAppVersion:function(){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var newVersion = $scope.view.newVersion;
			if(newVersion){
				var url = utils.url('/admin/system/cache/update-app-version.json', {
					appVersion:newVersion
				});
				$http.get(url).success(function(data) {
					if (data.success && data.result != null) {
						utils.notifySuccess('更新成功');
						window.location.reload();
					} else {
					}
				});
			}
		},
		updateStartupTime:function(){
			var url = utils.url('/admin/system/cache/update-startup-time.json', {
			});
			$http.get(url).success(function(data) {
				if (data.success) {
					utils.notifySuccess('更新成功');
				} else {
				}
			});
		},
		clearAllCache:function(){
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			// var me = this;
			var url = utils.url('/admin/system/cache/clear-all-cache.json', {
			});
			$http.get(url).success(function(data) {
				if (data.success) {
					utils.notifySuccess('清除成功');
					me.load();
				} else {
				}
			});
		},
		clearCache:function(key){
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			// var me = this;
			var url = utils.url('/admin/system/cache/clear-cache.json', {
				name:key
			});
			$http.get(url).success(function(data) {
				if (data.success) {
					utils.notifySuccess('清除成功');
					me.load();
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