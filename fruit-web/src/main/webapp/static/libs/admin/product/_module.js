define([ 'app/utils' ,'ng/module/my'], function(utils,my) {
	var Page = angular.module('Page', ['my'], function() {
	});
	return Page;
});