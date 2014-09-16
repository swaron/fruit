define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', ['ngAnimate', my.name ]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
//		$scope.model = {
////			username:null,
////			password:null,
//			redirectUrl: utils.getParam('redirectUrl')
//		};
		$scope.result = {};
	};

	angular.extend(PageController.prototype, {
		register:function(form){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var _form = this.$injector.get('_form');
			if (form.$invalid) {
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			
			if($scope.model.password != $scope.model.password2){
				//error
				form.password2.errors['custom'] = "两次输入密码不一样，请重新输入。";
				form.password2.$setValidity('custom',false);
				return ;
			}
			if($scope.model.password.length < 6 ){
				form.password.errors['custom'] = "密码至少需为6位。";
				form.password.$setValidity('custom',false);
				return ;
			}
			if($scope.model.password == '123456' ||  $scope.model.password == $scope.model.username ){
				form.password.errors['custom'] = "亲，你的密码太简单了。还不如用你的手机号码末几位加某人生日末几位。";
				form.password.$setValidity('custom',false);
				return ;
			}
			
			$scope.saving = true;
			var url = utils.url('/page/user/register/submit.json');
			var sdata = {
				username: $scope.model.username,
				password: $scope.model.password
			};
			$http.post(url, sdata).success(function(data) {
				if (data.success) {
					$scope.result.username = $scope.model.username;
				}else{
					form.username.errors['custom'] = data.message;
					form.username.$setValidity('custom',false);
				}
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