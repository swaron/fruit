define([ 'app/utils' ,'ng/module/my'], function(utils,my) {
	var module = angular.module('Page', [my.name], function() {
	});
	module.filter('parentId', [function() {
		return function(input, pid) {
			if(angular.isArray(input)){
				var children = [];
				for (var i = 0; i < input.length; i++) {
					var item = input[i];
					if(item.parentId == pid){
						children.push(item);
					}
				}
				return children;
			}
			return input;
		};
	}]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var $http = this.$injector.get('$http');
		$scope.view = {};
		$scope.model = {
			copy:utils.getParam('copy'),
			quotation:{
				productId : utils.getParam('productId'),
				categoryId : utils.getParam('categoryId'),
				quotationId : utils.getParam('quotationId'),
				happenTime:Date.now(),
				quantity:1,
				unit:'箱'
			}
		};
		$scope.query = {};
		$scope.result = {};
		this.load();
		
		$scope.$watch('view.level4', function(category) {
			if(category){
				$scope.$eval('model.quotation.categoryId = view.level4.categoryId');
				$scope.$eval('model.quotation.categoryName = view.level4.name');
			}
      });
	};
	
	angular.extend(PageController.prototype, {
		load : function() {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			if($scope.model.quotation.quotationId){
				var url = utils.url('/admin/quotation/load.json',{
					quotationId : $scope.model.quotation.quotationId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						if( $scope.model.copy ){
							if(data.result.quotationId == null){
								alert('复制有风险，预期的ID不存在。');
							}
							delete data.result.quotationId;
						}
						$scope.model.quotation = data.result;
					}
				});
			}
			$http.get(utils.url('/admin/seller/find.json')).success(function(data) {
				if(data.success){
					$scope.result.sellers = data.records;
				}
			});
			$http.get(utils.url('/admin/category/find.json')).success(function(data) {
				if(data.success){
					$scope.result.categorys = data.records;
				}
			});
		},
		saveAndEdit:function(form){
			var $scope = this.$scope;
			var promise = this.saveItem(form);
			if(promise){
				promise.success(function(data){
					$scope.model.quotation = data.result;
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
					window.history.back();
//					window.location.href = utils.url('/admin/quotation.html');
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
			//要修改result.productPhotos，所以先copy
			var result = angular.copy($scope.model.quotation);
			result.sellerId = $scope.view.seller.sellerId;
			result.sellerName = $scope.view.seller.name;
			if(!angular.isString(result.productPhotos)){
				result.productPhotos = angular.toJson(result.productPhotos);
			}
			var url = utils.url('/admin/quotation/save.json');
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