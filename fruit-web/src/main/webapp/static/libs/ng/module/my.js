define(['app/utils','fastclick', './my/_module','./my/_constant', './my/_service', './my/_service_repo', './my/_directive', './my/_filters' ], function(utils, fastclick,module) {
	module.run([ '_repo','_user','_cart','$rootScope', function(_repo,_user,_cart,$rootScope) {
		$rootScope.utils = utils;
		// for eager instantiate repo.
		fastclick.attach(document.body);
	}]);
	return module;
});
  