define([ 'app/utils', 'ng/module/my','ng/module/hy','ng/module/bs','app/modernizr','jquery' ], function(utils, my,hy,bs,modernizr,jquery) {
	var module = angular.module('Page', ['ngSanitize', 'my','hy','bs' ], function() {
	});
	
	
	var windowWidth = window.innerWidth || 0;
	var windowHeight = window.innerHeight || 0;
	//手机嘛，省点流量
	var shouldFitScreen = modernizr.mobile() && (windowWidth <= 640 || windowHeight <= 640); 
	
	module.filter('contentPreProcress', function() {
		return function(input) {
			if(input){
				if(shouldFitScreen){
					//8px的padding
					var width = (jquery('#main .tab-content .p-content').width() || 0) - 8;
					if(width > 20){
						input = input.replace(/(<img\b[^>]+src=["])([^"]+)(["].*?>)/g,function(match,$1,$2,$3,index){
							return $1 + utils.url($2, {
								w : width
							}) +$3;
						})
					}
				}
			}
			return input;
		};
	});
	var Controller = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.query = {
			epid: utils.urlFileName(),
			title:utils.getParam('epid')
		};
		$scope.result = {};
		$scope.view = {};
		$scope.model = {
			product: null,
			mimage:{
				md:null,
				lg:null
			}
		};
		var me = this;
		me.load();
	};

	angular.extend(Controller.prototype, {
		load : function() {
//			var _repo = this.$injector.get('_repo');
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if($scope.query.title){
				document.title = title + ' - 粒粒果鲜';
			}
			var url = utils.url('/page/episode/product.json', {
				episodeProductId : $scope.query.epid
			});
			
			$http.get(url).success(function(data) {
				$scope.model.episode = data.result.episode;
				$scope.model.product = data.result.product;
//				$scope.model.episode.attrs['产品主图片集'][0];
			}).error(function(data) {

			});
		},
		addToCart : function(item, count) {
			var $scope = this.$scope;
			var _cart = this.$injector.get('_cart');
			var deliveryDate =  $scope.$eval('model.episode.deliveryDate');
			if( Date.now() > (deliveryDate + 2*3600*1000 )){
				utils.notifyError("无法放入购物车，当前团购的配送时间已过。请定下批次的团购。");
			}else{
				var updates = [ {
					type : 'add',
					epid : item.episodeProductId,
					episodeId: $scope.$eval('model.episode.episodeId'),
					count : count
				} ]; // list<ShoppingAction>
				_cart.updateCart(updates).then(function(){
					utils.notifySuccess("水果已添加到购物车。");
				});
			}
			
			
		}
	});

	module.controller('PageController', [ '$injector', '$scope', Controller ]);

	var body = document.getElementsByTagName('html')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;
});
