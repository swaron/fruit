define([ 'require', './episode/_module', './episode/_controller', './episode/_directive' ], function(require, module) {
//	module.config([ '$routeProvider', function($routeProvider) {
//		$routeProvider.when('/main', {
//			templateUrl : require.toUrl('./episode/tpl/main.htm'),
//			controller : 'EpisodeListCtrl',
//			controllerAs : 'c'
//		}).when('/detail/:episodeId', {
//			templateUrl : require.toUrl('./episode/tpl/detail.htm'),
//			controller : 'EpisodeDetailCtrl',
//			controllerAs : 'c'
//		}).otherwise({
//			redirectTo : '/main'
//		});
//	} ]);
	var ngapp = angular.element(document).find('body');
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
});