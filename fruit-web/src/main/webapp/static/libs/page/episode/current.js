define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ 'my' ], function() {
	});

	var Controller = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.query = {};
		$scope.result = {
			activityImages :[]
		};
		$scope.view = {};
		$scope.model = {};
		var me = this;
		$scope.$watch('_repo.model.address.name', function(newVal) {
			if(newVal){
				me.load(newVal);
				me.loadActivity($scope.$eval('_repo.model.address.deliveryAddressId'));
			}
		});
		
//		//从所有商品里面获取广告活动图，然后显示，所以只要给产品添加活动图片，就会在这里显示,现在换成从home_banner取
//		$scope.$watch('result.episode.records', function(newVal) {
//			var activityImages = [];
//			if(newVal && newVal.length){
//				for (var i = 0; i < newVal.length; i++) {
//					var item = newVal[i];
//					if(item.attrs['产品促销活动图'] && item.attrs['产品促销活动图'].length){
//						activityImages = activityImages.concat(item.attrs['产品促销活动图']);
//					}
//				}
//			}
//			$scope.result.activityImages = activityImages;
//		});
	};

	angular.extend(Controller.prototype, {
		loadActivity : function(deliveryAddressId) {
			var _repo = this.$injector.get('_repo');
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/page/home-banner/current.json', {
				deliveryAddressId : deliveryAddressId
			});
			$http.get(url).success(function(data) {
				$scope.result.activityImages = data.records;
			}).error(function(data) {
				
			});
		},
		load : function(addr) {
			var _repo = this.$injector.get('_repo');
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/page/episode/current.json', {
				address : addr
			});
			$http.get(url).success(function(data) {
				$scope.result.episode = data;
			}).error(function(data) {

			});
		},
		isImport:function(item){
			return (item.attrs['水果进口'] == true);
		},
		addToCart : function(item, count) {
			var $scope = this.$scope;
			var _repo = this.$injector.get('_repo');
			var _cart = this.$injector.get('_cart');
			var cart = _repo.get('cart') || {};
			var deliveryDate =  $scope.$eval('result.episode.result.deliveryDate');
			if( Date.now() > (deliveryDate + 2*3600*1000 )){
				utils.notifyError("无法放入购物车，当前团购的配送时间已过。请定下批次的团购。");
			}else{
				var updates = [ {
					type : 'add',
					epid : item.episodeProductId,
					episodeId : $scope.$eval('result.episode.result.episodeId'),
					count : count
				} ]; // list<ShoppingAction>
				_cart.updateCart(updates).then(function(){
					utils.notifySuccess("水果已添加到购物车");
				});
			}
		}
	});

	module.controller('EpisodeController', [ '$injector', '$scope', Controller ]);

	var body = document.getElementsByTagName('html')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;
});
