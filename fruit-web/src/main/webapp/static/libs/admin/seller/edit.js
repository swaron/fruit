define([ 'app/utils' ,'ng/module/my'], function(utils,my) {
	var module = angular.module('Page', [my.name], function() {
	});
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.model = {
			copy:utils.getParam('copy'),
			seller:{
				sellerId : utils.getParam('sellerId')
			}
		};
		this.load();
		
//		$scope.$watch('form.attr_code.$error', function(newValue, oldValue) {
//			var error = newValue;
//      });
	};
	
	angular.extend(PageController.prototype, {
		load : function() {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			if($scope.model.seller.sellerId){
				var url = utils.url('/admin/seller/load.json',{
					sellerId : utils.getParam('sellerId')
				});
				$http.get(url).success(function(data) {
					if(data.success){
						if( $scope.model.copy ){
							if(data.result.sellerId == null){
								alert('复制有风险，预期的ID不存在。');
							}
							delete data.result.sellerId;
						}
						$scope.model.seller = data.result;
					}
				});
			}
		},
		saveAndEdit:function(form){
			var $scope = this.$scope;
			var promise = this.saveItem(form);
			if(promise){
				promise.success(function(data){
					$scope.model.seller = data.result;
				});
			}
		},
		saveAndReturn : function(form) {
			var promise = this.saveItem(form);
			if(promise === false){
				return;
			}
			if(promise){
				promise.success(function(data){
					window.location.href = utils.url('/admin/seller.html');
				});
			};
		},
		saveItem : function(form) {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if (form.$pristine) {
				return false;
			}
			if (form.$invalid) {
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			$scope.saving = true;
			var result = this.$scope.model.seller;
			var url = utils.url('/admin/seller/save.json');
			return $http.post(url, result).success(function(data) {
				$scope.saving = false;
				utils.notifySuccess('保存成功');
			}).error(function(){
				$scope.saving = false;
			});
		}
	});
	module.controller('PageController', [ '$injector', '$scope', PageController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	return module;
	
});