define([ 'app/utils' ,'ng/module/my'], function(utils,my) {
	var module = angular.module('Page', [my.name], function() {
	});

	module.directive('myJsonAttr', [ function() {
		return {
			require : 'ngModel',
			link:function(scope, element, attrs, ngModel){
				ngModel.$formatters.push(function(arr){
					if(!arr){
						return arr;
					}
					try {
						if(arr instanceof Array){
							return arr.join('\n');
						}else{
							return arr;
						}
					} catch (e) {
						return arr;
					}
				});
				ngModel.$parsers.push(function(view){
					if(!view){
						return view;
					}
					try {
						var arr = view.trim().split(/[\r\n]+/g);
						return arr;
					} catch (e) {
						return view;
					}
				});
			}
		};
	}]);
	module.directive('dbunique', [ '$http',function($http) {
		return {
			require : 'ngModel',
			link:function(scope, element, attrs, ngModel){
				var validator = function(modelValue){
					if(!modelValue || modelValue.trim() == ''){
						return true;
					}
					var url = utils.url('/admin/attribute/find.json',{
						attrCode : modelValue
					});
					$http.get(url).success(function(data){
						if(data.total > 0 ){
							if(data.records[0].attrId == scope.model.attribute.attrId){
								ngModel.$setValidity('dbunique',true);
							}else{
								ngModel.$setValidity('dbunique',false);
							}
						}else{
							ngModel.$setValidity('dbunique',true);
						}
					});
					return true;
				};
				ngModel.$validators['dbunique'] = validator;
			}
		};
	}]);
	var PropController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.model = {
			copy:utils.getParam('copy'),
			attribute:{
				frontendInput : 'text',
				attrId : utils.getParam('attrId'),
				system: false
			}
		};
		this.load();
		
//		$scope.$watch('form.attr_code.$error', function(newValue, oldValue) {
//			var error = newValue;
//      });
	};
	
	angular.extend(PropController.prototype, {
		load : function() {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			if($scope.model.attribute.attrId){
				var url = utils.url('/admin/attribute/load.json',{
					attrId : utils.getParam('attrId')
				});
				$http.get(url).success(function(data) {
					if(data.success){
						if( $scope.model.copy ){
							if(data.result.attrId == null){
								alert('复制有风险，预期的ID不存在。');
							}
							delete data.result.attrId;
							data.result.system = false;
						}
						$scope.model.attribute = data.result;
					}
				});
			}
		},
		saveAndEdit:function(form){
			var $scope = this.$scope;
			var promise = this.saveItem(form);
			if(promise){
				promise.success(function(data){
					$scope.model.attribute = data.result;
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
					window.location.href = utils.url('/admin/attribute.html');
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
			var result = this.$scope.model.attribute;
			result.attrModel = angular.toJson(result.attrModel);
			var url = utils.url('/admin/attribute/save.json');
			return $http.post(url, result).success(function(data) {
				$scope.saving = false;
				utils.notifySuccess('保存成功');
			}).error(function(){
				$scope.saving = false;
			});
		}
	});
	module.controller('PropController', [ '$injector', '$scope', PropController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	return module;
	
});