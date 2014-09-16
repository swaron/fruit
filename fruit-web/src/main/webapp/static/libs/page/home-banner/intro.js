define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});

	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {
			banner:{
				homeBannerId: utils.getParam('homeBannerId')
			}
		};
		$scope.query = {};
		$scope.result = {};
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if($scope.model.banner.homeBannerId){
				var url = utils.url('/page/home-banner/detail.json',{
					homeBannerId : $scope.model.banner.homeBannerId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						$scope.model.banner = data.result;
					}
				});
			}
		},
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	
	return module;
});