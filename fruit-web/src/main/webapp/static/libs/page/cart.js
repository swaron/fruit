define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ]);
	var CartController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		var me = this;
		$scope._cache = this.$injector.get('_cache');
		$scope.view = {
			showEpisode:false
		};
		$scope.model = {
			cart:{
//				episodeId:[]
			},
			episodeCount : 1
		};
		$scope.query = {};
		$scope.result = {};
		$scope.$watch('_repo.model.cart.items',function(items){
			var cartByEpisode = {};
			if(items){
				for(var epid in items){
					var item = items[epid];
					(cartByEpisode[item.episodeId] || (cartByEpisode[item.episodeId] = [])).push(item);
				}
			}
			$scope.model.cart = cartByEpisode;
			$scope.model.episodeCount = Object.keys(cartByEpisode).length;
		});
	};

	angular.extend(CartController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/page/cart/cart-detail.json');
			var cart = _repo.get('cart') || {};
			$http.post(url, cart).success(function(data) {
				if(data.success){
					$scope.model.cart = data.result;
				}
			});
		},
		fetchProduct : function(episodeProductId) {
			var _cache = this.$injector.get('_cache');
			_cache.loadProducts(episodeProductId);
		},
		fetchEpisode : function(episodeId) {
			var _cache = this.$injector.get('_cache');
			_cache.loadEpisode(episodeId);
		},
		removeCartItem : function(epid) {
			var _cart = this.$injector.get('_cart');
			var updates = [ {
				type : 'del',
				epid : epid,
				count : 0
			} ]
			_cart.updateCart(updates);
		},
		getSubTotalMoney : function(episodeId) {
			var $scope = this.$scope;
			var items = $scope.model.cart[episodeId];
			var _cache = this.$injector.get('_cache');
			var sum = 0.0;
			for ( var key in items) {
				var item = items[key];
				if (item.enabled) {
					var unitPrice = $scope.$eval('_cache.products["' + item.epid + '"].price');
					if (unitPrice === undefined) {
						sum = "";// 未知
						break;
					}
					sum += (item.count * unitPrice)
				}
			}
			return sum;
		},
		getTotalMoney : function() {
			var $scope = this.$scope;
			var items = $scope.$eval('_repo.model.cart.items');
			var _cache = this.$injector.get('_cache');
			var sum = 0.0;
			for ( var key in items) {
				var item = items[key];
				if (item.enabled) {
					var unitPrice = $scope.$eval('_cache.products["' + item.epid + '"].price');
					if (unitPrice === undefined) {
						sum = "";// 未知
						break;
					}
					sum += (item.count * unitPrice)
				}
			}
			return sum;
		},
		getTotalCount : function() {
			var $scope = this.$scope;
			var items = $scope.$eval('_repo.model.cart.items');
			var sum = 0;
			for ( var key in items) {
				var item = items[key];
				if (item.enabled) {
					sum++;
				}
			}
			return sum;
		},
		getSubTotalCount : function(episodeId) {
			var $scope = this.$scope;
			var items = $scope.model.cart[episodeId];
			var sum = 0;
			for ( var key in items) {
				var item = items[key];
				if (item.enabled) {
					sum++;
				}
			}
			return sum;
		},
		proceedConfirmOrder:function(sdata){
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var _cart = this.$injector.get('_cart');
			var url = utils.url('/page/order/create-order.json');
			$http.post(url, sdata).success(function(data) {
				if (data.success && data.result) {
					var items = sdata.items;
					var episodeId = sdata.episodeId;
					var updates = [];
					for (var i = 0; i < items.length; i++) {
						var item = items[i];
						if(item.enabled && item.episodeId == episodeId){
							updates.push({
								type : 'del',
								epid: item.epid,
								count:0
							});
						}
					}
					var promise = _cart.updateCart(updates);
					promise.then(function(){
						var orderId = data.result; // 订单已经和用户绑定，订单ID只是额外的一个校验。
						window.location.href = utils.url('/page/order/confirm-order.html', {
							orderId : orderId
						});
					});
				}else if(data.result == 401){
//					alert('请重新登录。');
					window.location.href = utils.url('/page/login.html',{
						redirectUrl: utils.url('/page/cart.html')
					});
				}else if(data.result == 403){
					alert('没有权限。');
				}else if(data.message){
					alert(data.message);
					$scope.view.showEpisode = true;
				}
			}).error(function(data) {
				alert('订单提交失败。 当前未登录，网络不可用都会导致失败');
			});
		},
		confirmOrder : function(episodeId) {
			var me = this;
			var $scope = this.$scope;
			var $injector = this.$injector;
			var $http = this.$injector.get('$http');
			var _user = this.$injector.get('_user');
			var _cart = this.$injector.get('_cart');
			var items = $scope.model.cart[episodeId];
			
			var count = me.getSubTotalCount(episodeId);
			if(count == 0){
				alert("亲，没有商品可以结算，是不是已经取消了购物车的商品。");
				return
			}
			
			var updates = []; //内容已经直接修改到cart里面了，必须带force
			var promise = _cart.updateCart(updates,true);
			
			promise.then(function(){
				var sdata = {
						deliverAddress:$scope.$eval('_repo.model.address.name'),
						deliverNotes:null,
						episodeId:episodeId,
						items: angular.copy(items)
				}
				
				if(_user.isAuthenticated){
					me.proceedConfirmOrder(sdata);
				}else{
					var _login = $injector.get('_login');
					_login.popup().then(function(data){
						me.proceedConfirmOrder(sdata); 
					});
					return;
				}
			});
		}
	});
	module.controller('CartController', [ '$injector', '$scope', CartController ]);

	var body = document.getElementsByTagName('body')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;

});