define(['app/utils'], function(utils) {
	var module = angular.module('my',[]);
	module.run([ '$rootScope', function($rootScope) {
		$rootScope.utils = utils;
	} ]);
	module.config(['$httpProvider',function($httpProvider) {
		$httpProvider.interceptors.push(['$q',function($q) {
			return {
				request : function(config) {
//					console.log('ajax request');
					return config;
				},
				response : function(response) {
//					console.log('ajax response');
					// same as above
					return response;
				},
				responseError : function(rejection) {
//					console.log('ajax error');
					// if (canRecover(rejection)) {
					// return responseOrNewPromise
					// }
					utils.notifyError("有错误发生，请检查网络并尝试刷新页面，如果依然有问题，请联系客服。");
					return $q.reject(rejection);
				}
			};
		}]);
	}]);
	return module;
});
