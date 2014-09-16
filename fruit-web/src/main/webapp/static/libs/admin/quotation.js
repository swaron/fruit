define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});

	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {};
		$scope.model = {};
		$scope.query = {
			happenTimeFrom: Date.now(),
			happenTimeTo: Date.now()
		};
		
		$scope.result = {};
		this.load();
	};

	angular.extend(PageController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			// var me = this;
			var url = utils.url('/admin/quotation/find.json', {
				categoryName:$scope.query.categoryName,
				seller:$scope.query.seller,
				happenTimeFrom:$scope.query.happenTimeFrom,
				happenTimeTo:$scope.query.happenTimeTo
			});
			$http.get(url).success(function(data) {
				if (data.success) {
					$scope.result = data;
				}
			});
		},
		removeItem:function(index){
			if(!window.confirm('确认删除')){
				return;
			}
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var item =$scope.result.records[index];
			
			var quotationId = item.quotationId;
			var url = utils.url('/admin/quotation/remove.json',{
				quotationId:quotationId
			});
			$http['delete'](url).success(function(data){
				if(data.success){
					$scope.result.records.splice(index,1);
					utils.notifySuccess('属性删除成功。');
				}
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