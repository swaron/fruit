define([ './product/_module', './product/_controller' ], function(module) {
	var ngapp = angular.element(document).find('body');
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
});