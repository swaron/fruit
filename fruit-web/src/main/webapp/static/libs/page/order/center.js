define([ 'app/utils', 'ng/module/my','require' ], function(utils, my, require ) {
	var module = angular.module('Page', [ my.name ]);
	var PageController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var me = this;
		$scope.view = {
			period: '最近三周',
			query:null
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
			var url = utils.url('/page/order/my-orders.json', {
				period: $scope.view.period,
				query : $scope.view.query
			});
			$http.get(url).success(function(data) {
				$scope.result.orders = data;
			}).error(function(data) {
				
			});
		},
		claimRequest:function(item,$event){
			var me =this;
//			utils.notifySuccess("目前请发短信或者打电话给客服人员，客服联系方式在页脚。",120000);
			var claimRequest = window.prompt("请填写要关于问题的描述（比如：买山竹5个/1斤，坏一个/200g，理赔方式：退钱/补货。），以及期望的理赔方式，其他您觉得有用的信息。",item.claimRequest || "");
			if(claimRequest){
				var $http = this.$injector.get('$http');
				var url = utils.url('/page/order/claim-request.json', {
					claimRequest:claimRequest,
					orderId:item.orderId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						utils.notifySuccess('理赔申请已经提交，我们每隔2~3天会统一处理.<br/>如果希望马上处理，或者逾期未处理，请直接电话或者微信联系客服',60000);
						item.claimRequest = data.result.claimRequest;
						item.claimResponse = data.result.claimResponse;
					}else{
						utils.notifyError('申请失败<br/>' + data.message);
						item.claimRequest = data.result.claimRequest;
						item.claimResponse = data.result.claimResponse;
					}
				}).error(function(data) {
					
				});
			}
		},
		cancelOrder:function(item,$event){
			var $http = this.$injector.get('$http');
			var url = utils.url('/page/order/cancel-order.json', {
				orderId:item.orderId
			});
			$http.get(url).success(function(data) {
				if(data.success){
					window.location.href = utils.url('/page/order/order-canceled.html',{
						orderId:item.orderId
					});
				}else{
					require(['jquery','bootstrap'],function($){
						var tr = $($event.target).closest('tr');
						var next = tr.next();
						if(next && next.hasClass('alert')){
							var msgEl = tr.next().find('span:last');
							msgEl.text(data.message);
						}else{
							var html = ['<tr class="alert alert-warning alert-dismissible text-center" role="alert"><td colspan="7">',
							            '  <button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>',
							            '  <span class="msg">{message}</span>',
							            '</td></tr>'].join('');
							html = utils.substitute(html,data);
							tr.after(html);
						}
					});
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