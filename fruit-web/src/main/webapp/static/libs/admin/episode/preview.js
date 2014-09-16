define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ 'my' ], function() {
	});

	var Controller = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.query = {};
		$scope.result = {};
		$scope.view = {};
		$scope.model = {
			episode:{
				episodeId:utils.getParam('episodeId')
			}
		};
		var me = this;
		$scope.$watch('_repo.model.address.name', function(newVal) {
			me.load(newVal);
		});
	};

	angular.extend(Controller.prototype, {
		load : function(addr) {
			var _repo = this.$injector.get('_repo');
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/episode/detail.json', {
				episodeId : $scope.model.episode.episodeId
			});
			$http.get(url).success(function(data) {
				$scope.result.episode = data;
			}).error(function(data) {

			});
		},
		addToCart : function(item, count) {
			var _repo = this.$injector.get('_repo');
			var _cart = this.$injector.get('_cart');
			var cart = _repo.get('cart') || {};
			var updates = [ {
				type : 'add',
				epid : item.episodeProductId,
				count : count
			} ]; // list<ShoppingAction>
			_cart.updateCart(updates);
		}
	});
	module.filter('thumburlsize', function() {
		return function(input, width, height) {
			var url = utils.url(input, {
				w : width,
				h : height
			});
			return url;
		};
	});

	module.controller('EpisodeController', [ '$injector', '$scope', Controller ]);

	var body = document.getElementsByTagName('body')[0];
	angular.element(body).ready(function() {
		angular.bootstrap(body, [ module.name ]);
	});
	return module;
});
