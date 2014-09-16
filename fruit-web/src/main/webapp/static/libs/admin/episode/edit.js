define([ 'app/utils', 'ng/module/my','bootstrap' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});
	var EpisodeController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {
			toAddProduct:{
				
			},
			episodeProduct:{
				price:null,
				attrs:{}
			},
			newAttr:{
				attrCode: '',
				attrValue:null
			}
		};
		$scope.model = {
			records:[],
			result : {
				episodeId : utils.getParam('episodeId'),
			}
		};
		$scope.query = {
			productSku:null,
			productName:null
		};
		$scope.result = {};
		this.load();
	};

	angular.extend(EpisodeController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			// var me = this;
			if ($scope.model.result.episodeId) {
				var url = utils.url('/admin/episode/detail.json', {
					episodeId : $scope.model.result.episodeId
				});
				$http.get(url).success(function(data) {
					if (data.success && data.result != null) {
						$scope.model = data;
					}else{
						alert('没有找到这批次的信息。');
					}
				});
			}
		},
		saveProduct:function(form){
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if (form.$invalid) {
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			$scope.saving = true;
			
			//check extend
			var data= {
				episodeId:$scope.model.result.episodeId,
				productId:$scope.view.toAddProduct.productId,
				price: $scope.view.episodeProduct.price,
				attrs: angular.toJson($scope.view.episodeProduct.attrs)
			};
			var url = utils.url('/admin/episode/add-product.json');
			return $http.post(url, data).success(function(res) {
				if(res.success){
					utils.notifySuccess('添加成功');
					me.load();
					$('#edit-item-win').modal('hide');
				}
				$scope.saving = false;
			}).error(function() {
				$scope.saving = false;
			});
			//post to server
			//reload list
		},
		addProduct:function(item){
			var $scope = this.$scope;
			$scope.view.toAddProduct = item;
			$scope.view.episodeProduct.price = item.attrs['产品单价']; 
			$('#edit-item-win').modal({
				backdrop : 'static'
			});
		},
		removeProduct:function(index){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var item = $scope.model.records[index];
			var url = utils.url('/admin/episode/remove-product.json',{
				episodeId:$scope.model.result.episodeId,
				productId:item.productId
			});
			$http['delete'](url).success(function(data){
				if(data.success){
					$scope.model.records.splice(index,1);;
					utils.notifySuccess('删除成功');
				}
			});
		},
		
		addEpisodeProductAttr:function(attr){
			var $scope = this.$scope;
			if(attr !=null){
				if(!attr.attrCode){
					alert('属性代码不可以为空');
				}else{
					$scope.view.episodeProduct.attrs[attr.attrCode] = attr.attrValue;
				}
			}
		},
		removeEpisodeProductAttr:function(key){
			var $scope = this.$scope;
			if(key){
				//删掉这个属性之后，extraAttrs 会随之被移除
				delete $scope.view.episodeProduct.attrs[key]; 
			}
		},
		queryProduct : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/product/find.json',{
				name: $scope.query.productName,
				sku: $scope.query.productSku
			});
			$http.get(url).success(function(data) {
				if (data.success) {
					$scope.result.products = data.records;
				}
			});
		}
	});
	module.controller('EpisodeController', [ '$injector', '$scope', EpisodeController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	return module;

});
