define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ]);
	var LoginController = function($injector, $scope, _login) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {
			redirectUrl: utils.getParam('redirectUrl')
		};
		$scope.query = {};
		$scope.result = {};
	};

	angular.extend(LoginController.prototype, {
		load : function() {
			
		},
		submit:function(form){
			var $rootScope = this.$injector.get('$rootScope');
			var _login = this.$injector.get('_login');
			var _cart = this.$injector.get('_cart');
			var $scope = this.$scope;
			var result = _login.submit(form);
			if(result){
				result.then(function(data){
					//TODO, redirect
					if(data.authenticated){
						//空的操作会返回服务端的数据
						var updates = []; // list<ShoppingAction>
						var items = $rootScope.$eval('_repo.model.cart.items');
						for ( var key in items) {
							updates.push({
								type:'add',
								epid:key,
								count:items[key].count
							});
						}
						_cart.updateCart(updates).then(function(){
							if($scope.model.redirectUrl){
								window.location.href = $scope.model.redirectUrl
							}else{
								window.location.href = utils.url('/');
							}
						});
						
					}
				});
			}
		}
	});
	module.controller('LoginController', [ '$injector', '$scope', LoginController ]);

	var body = document.getElementsByTagName('body')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;

});