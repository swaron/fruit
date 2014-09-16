define([ 'app/utils', 'ng/module/my','require' ], function(utils, my, require ) {
	var module = angular.module('Page', [ my.name ]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var me = this;
		$scope.db_code = $injector.get('db_code');
		$scope.view = {
		};
		$scope.model = {
			episodeId : utils.getParam('episodeId')
		};
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
			if($scope.model.episodeId){
				var url = utils.url('/admin/order/orders-by-episode.json', {
					episodeId: $scope.model.episodeId
				});
				$http.get(url).success(function(data) {
					$scope.result = data;
				}).error(function(data) {
					
				});
			}
		},
		updateAllStatus:function(){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var proceed = window.confirm("确认修改");
			if(!proceed){
				return;
			}
			var me = this;
			if($scope.model.episodeId){
				var url = utils.url('/admin/order/update-all-to-completed.json', {
					episodeId: $scope.model.episodeId
				});
				$http.get(url).success(function(data) {
					alert("共修改了" + data.result + "个订单。")
					me.load();
				}).error(function(data) {
					
				});
			}
		},
		completeOrder:function(order){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var proceed = window.confirm("确认完成");
			if(!proceed){
				return;
			}
			var url = utils.url('/admin/order/finish-order.json', {
				orderId:order.orderId,
			});
			$http.get(url).success(function(data) {
				if(data.message){
					utils.notifyError(data.message);
				}
				if(data.result.orderStatus != null){
					order.orderStatus = data.result.orderStatus;
				}
			}).error(function(data) {
				
			});
		},
		claimResponse:function(item,$event){
			var me =this;
//			utils.notifySuccess("目前请发短信或者打电话给客服人员，客服联系方式在页脚。",120000);
			var claimResponse = window.prompt("线下处理，这里填写理赔的处理进度或者结果。",item.claimResponse || "处理中");
			if(claimResponse){
				var $http = this.$injector.get('$http');
				var url = utils.url('/page/order/claim-response.json', {
					claimResponse:claimResponse,
					orderId:item.orderId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						utils.notifySuccess('理赔结果更新完成');
						item.claimRequest = data.result.claimRequest;
						item.claimResponse = data.result.claimResponse;
					}else{
						utils.notifyError('理赔结果更新失败<br/>' + data.message);
						item.claimRequest = data.result.claimRequest;
						item.claimResponse = data.result.claimResponse;
					}
				}).error(function(data) {
					
				});
			}
		},
		cancelOrder:function(order){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var proceed = window.confirm("确认取消");
			if(!proceed){
				return;
			}
			var url = utils.url('/admin/order/cancel-order.json', {
				orderId:order.orderId,
			});
			$http.get(url).success(function(data) {
				if(data.message){
					utils.notifyError(data.message);
				}
				if(data.result.orderStatus != null){
					order.orderStatus = data.result.orderStatus;
				}
			}).error(function(data) {
				
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