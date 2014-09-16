define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});

	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {};
		$scope.query = {
			deliveryDate:Date.now(),
			deliveryAddress:null
		};
		
		$scope.result = {};
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/episode/find.json', {
				deliveryDate:$scope.query.deliveryDate,
				deliveryAddress:$scope.query.deliveryAddress
			});
			$http.get(url).success(function(data) {
				if (data.success) {
					$scope.result = data;
				}
			});
		},
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	
	return module;
});