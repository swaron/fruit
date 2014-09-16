define([ 'app/utils' ,'ng/module/my'], function(utils,my) {
	var module = angular.module('Page', [my.name], function() {
	});
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {
			copy:utils.getParam('copy'),
			banner:{
				homeBannerId:utils.getParam('homeBannerId'),
				startTime:Date.now(),
				endTime:Date.now(),
				globalVisible:true,
				enabled:true,
				bannerWeight:50,
			}
		};
		$scope.query = {};
		$scope.result = {
			addresses : null
		};
		this.load();
		this.loadAddress();
	};
	
	angular.extend(PageController.prototype, {
		loadAddress:function(){
			var url = utils.url('/page/pub/address/list.json');
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			$http.get(url).success(function(data) {
				$scope.result.addresses = data.records;
			});
		},
		load : function() {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
			if($scope.model.banner.homeBannerId){
				var url = utils.url('/admin/home-banner/load.json',{
					homeBannerId : $scope.model.banner.homeBannerId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						if( $scope.model.copy ){
							if(data.result.homeBannerId == null){
								alert('复制有风险，预期的ID不存在。');
							}
							delete data.result.homeBannerId;
						}
						$scope.model.banner = data.result;
					}
				});
			}
		},
		saveAndEdit:function(form){
			var $scope = this.$scope;
			var promise = this.saveItem(form);
			if(promise){
				promise.success(function(data){
					$scope.model.banner = data.result;
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
			var result = angular.copy($scope.model.banner);
			if(!angular.isString(result.imageUrl)){
				result.imageUrl = angular.toJson(result.imageUrl);
			}
			var url = utils.url('/admin/home-banner/save.json');
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