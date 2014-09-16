define([ 'app/utils', 'ng/module/my','require' ], function(utils, my, require ) {
	var module = angular.module('Page', [ my.name ]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {
		};
		$scope.model = {};
		$scope.query = {
		};
		$scope.result = {
		};
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/page/feedback/proposal/messages.json', {
			});
			$http.get(url).success(function(data) {
				$scope.result.msg = data;
			}).error(function(data) {
				
			});
		},
		submit:function(form){
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if (form.$invalid) {
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			$scope.saving = true;
			var url = utils.url('/page/feedback/proposal/submit.json', {
			});
			var sdata = {
				username: $scope.model.username,
				content:  $scope.model.content
			};
			$http.post(url,sdata).success(function(data) {
				utils.notifySuccess("留言已成功提交。");
				me.load();
				$scope.saving = false;
			}).error(function(data) {
				$scope.saving = false;
			});
		}
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var body = document.getElementsByTagName('body')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;

});