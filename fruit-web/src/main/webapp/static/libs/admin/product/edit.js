define([ 'app/utils', 'ng/module/my' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});
	var ProductController = function($injector, $scope) {
		this.categoryCache = {
			
		};
		this.productCache = {
			
		};
		this.$scope = $scope;
		this.$injector = $injector;
		var $http = this.$injector.get('$http');
		var me = this;
		$scope.view = {
			//子产品描述源
			childProducts:{}, //productId:product
			//选择的类型描述
			categoryDesc:null,
			//产品额外独立的属性及值
			extraAttrs:{},
			//存储添加属性组件的输入值
			newAttr:{},
			//merge的属性集合，及相同的属性,{attrCode:{'#havingCount':2,'1':'attrValue1','2':'attrValue2'} }
			mergedSkuAttrs:{}
		};
		$scope.model = {
			copy:utils.getParam('copy'),
			product : {
				//默认值
				typeId: 1,
				enabled:true,
				//attrs不能为空，要不然属性不好添加
				attrs:{
				},
				productId : utils.getParam('productId')
			}
		};
		$scope.query = {
			lastCategroy : null,
			categoryName : ''
		};
		$scope.result = {
			//产品从类型上定义的属性及值
			attrs:[],
			//选择的类型列表
			categorys : []
		};
		$scope.step = 0;
		//处理组合产品的子产品
		$scope.$watchCollection('model.product.attrs._sku_variant_items', function(sku_variants) {
			if( typeof sku_variants == 'object' && sku_variants){
				var variants = {};
				for ( var id in sku_variants) {
					if(me.productCache[id] != null){
						variants[id] = me.productCache[id]; 
					}else{
						var url = utils.url('/admin/product/load.json',{
							productId : id
						});
						$http.get(url).success(function(data) {
							if(data.success && data.result.productId){
								//childProducts = variants，被bind了的，会自动更新(watchCollection会。watch不会)
								variants[data.result.productId] = data.result;
								//updateMergedAttrs
							}
						});
					}
				}
				$scope.view.childProducts = variants;
				//updateMergedAttrs
			}else{
				return;
			}
		});
		////处理组合产品的 sku 属性的设定
		$scope.$watchCollection('view.childProducts',function(products){
			var i = 1,attrs;
			//{attrCode:{disabled:true,values:{'1':'attrValue1','2':'attrValue2'} }} }
			$scope.view.mergedSkuAttrs = attrs = {
			};
			if(!products){
				return;
			}
			var productLength = Object.keys(products).length;
			for ( var id in products) {
				var p = products[id];
				if(p.attrs){
					for ( var attrCode in p.attrs) {
						if(!attrs[attrCode]){
							attrs[attrCode] = {
								disabled:true,
								values:{}
							};
						}
						//这个属性，在产品i下（sku可能为空，所以用序号而不用sku）的值
						attrs[attrCode]['values'][i] = p.attrs[attrCode];
						
						if(Object.keys(attrs[attrCode]['values']).length == productLength){
							attrs[attrCode]['disabled'] = false;
						}
					}
				}
				i++;
			}
		});
		
		//更新产品所属类型的描述
		$scope.$watch('model.product.categoryId', function(newVal) {
			if(newVal == null){
				$scope.view.categoryDesc = '';
				return;
			}
			if (newVal != $scope.query.lastCategoryId) {
				$scope.query.lastCategoryId = newVal;
				me.loadAttrsOfCategory(newVal);
			}
			// 更新 view.categoryDesc
			var obj = me.categoryCache[newVal]; 
			if(obj){
				$scope.view.categoryDesc = obj.namePath + ' (' + obj.idPath + ')' ;
			}else{
				console.log('check failed for category cache for ' + newVal);
				var url = utils.url('/admin/category/load.json',{
					categoryId : newVal
				});
				$http.get(url).success(function(data) {
					var category = data.result;
					if(data.success && category && category.categoryId != null){
						//放到cache中
						me.categoryCache[category.categoryId] = category ;
						//确认categoryId没变
						if($scope.$eval('model.product.categoryId') == category.categoryId){
							$scope.view.categoryDesc = category.name + ' | ' + category.idPath + ' | ' + category.namePath;
						}
						
					}
				});
			}
		});
		//显示剩余的属性
		$scope.$watch('result.attrs',function(categoryAttrs){
			me.checkExtraAttr();
		});
		$scope.$watchCollection('model.product.attrs', function(newVal) {
			me.checkExtraAttr();
		});
		this.loadCategory();
		this.load();
	};
	

	angular.extend(ProductController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if($scope.model.product.productId){
				var url = utils.url('/admin/product/load.json',{
					productId : $scope.model.product.productId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						if( $scope.model.copy ){
							if(data.result.productId == null){
								alert('复制有风险，预期的ID不存在。');
							}
							delete data.result.productId;
						}
						$scope.model.product = data.result;
						$scope.model.product.attrs = $scope.model.product.attrs || {};
					}
				});
			}
		},
		addChildProduct:function(product){
			var $scope = this.$scope;
			if(product){
				this.productCache[product.productId] = product;
				//_sku_variant_items 以子产品id为key
				//_sku_var_attrs 以属性attrCode为key
				var variants = $scope.$eval('model.product.attrs._sku_variant_items');
				if(!variants){
//					if($scope.model.product.attrs){
//						$scope.model.product.attrs = {};
//					}
					variants = $scope.model.product.attrs._sku_variant_items = {};
				}
				//目前没有需要的配置需要存到值里面
				variants[product.productId] = true;
			}
		},
		removeChildProduct:function(key){
			var $scope = this.$scope;
			var variants = $scope.$eval('model.product.attrs._sku_variant_items');
			if(variants){
				delete variants[key];
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
					$scope.result.candidateProducts = data.records;
				}
			});
		},
		loadCategory : function() {
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/category/find.json',{
				name: $scope.query.categoryName
			});
			$http.get(url).success(function(data) {
				if (data.success && data.records.length) {
					$scope.result.categorys = data.records;
					for (var i = 0; i < data.records.length; i++) {
						var category = data.records[i];
						me.categoryCache[category.categoryId] = category; 
					}
				}
			});
		},
		toggleTotalAttr:function(item,event){
			var targetEl = $(event.target);
			var $http = this.$injector.get('$http');
			var $filter = this.$injector.get('$filter');
			
			if(targetEl.siblings('pre').length){
				//already shown
				targetEl.siblings('pre').remove();
				return;
			}
			var url = utils.url('/admin/category/attrs.json',{
				categoryId:item.categoryId
			});
			$http.get(url).success(function(data){
				if(data.success && data.records){
					var attrObject = {};
					//父节点在后面,倒过来，让字节点的属性覆盖父节点。
					var all = data.records.reverse();
					for (var i = 0; i < all.length; i++) {
						var attr = all[i];
						attrObject[attr.attrCode] = {
							namePath:attr.namePath,
							attrModel:attr.attrModel
						};
					}
					var attrs = $filter('json')(attrObject);
					var tpl = '<pre>{attrs}</pre>';
					var html = utils.substitute(tpl,{
						attrs:attrs
					});
					targetEl.after(html);
				}
			});
		},
		addExtraAttr:function(attr){
			var $scope = this.$scope;
			if(attr !=null){
				if(!attr.attrCode || attr.attrCode.indexOf('_') == 0){
					alert('属性代码不可以为空且不可以以_开头');
				}else{
					$scope.model.product.attrs[attr.attrCode] = attr.attrValue;
				}
			}
		},
		removeExtraAttr:function(key){
			var $scope = this.$scope;
			if(key && key.indexOf('_') != 0){
				//删掉这个属性之后，extraAttrs 会随之被移除
				delete $scope.model.product.attrs[key]; 
			}
		},
		checkExtraAttr:function(){
			var $scope = this.$scope;
			var attrs = $scope.$eval('model.product.attrs');
			var categoryAttrs = $scope.$eval('result.attrs');
			if(attrs == null ){
				return;
			}
			if(categoryAttrs == null || categoryAttrs.length == 0){
				//产品属性还没有初始化
				//alert('类别的属性列表未加载，请先设置产品所在类别。');
				return;
			}
			var obj = angular.copy(attrs);
			for (var i = 0; i < categoryAttrs.length; i++) {
				var attr = categoryAttrs[i];
				//移除类型上的属性
				delete obj[attr.attrCode];
			}
			//剩余的属性
			for ( var attrCode in obj) {
				if(attrCode.indexOf('_') == 0){
					//内部属性，忽略
					delete obj[attrCode];
					continue;
				}
			}
			$scope.view.extraAttrs = obj;
		},
		loadAttrsOfCategory : function(categoryId) {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/category/attrs.json');
			
			$http.get(url, {
				params : {
					categoryId : categoryId
				}
			}).success(function(data) {
				if (data.success && data.records) {
					// 生成属性列表
					$scope.result.attrs = data.records;
//					data.records = [ {
//						attrCode : '水果产地',
//						frontendInput : 'select',
//						attrModel : [ 'a', b ]
//					} ];
					
				}
			});
		},
		prevStep : function() {
			var scope = this.$scope;
			if (scope.step == 0) {
			} else {
				scope.step = scope.step - 1;
			}
		},
		nextStep : function() {
			var scope = this.$scope;
			if (scope.step == 3) {
			} else {
				scope.step = scope.step + 1;
			}
		},
		saveAndEdit : function(form) {
			var promise = this.saveItem(form);
			if (promise) {
				promise.success(function(data) {
					utils.notifySuccess('保存成功');
				});
			}
		},
		saveAndReturn : function(form) {
			var promise = this.saveItem(form);
			if (promise) {
				promise.success(function(data) {
					window.location.href = utils.url('/admin/attribute.html');
				});
			}
		},
		saveItem : function(form) {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if (form.$invalid) {
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			if($scope.model.product.categoryId == null){
				alert('请选择产品类型');
				return false;
			}
			$scope.saving = true;
			
			var product = $scope.model.product;
			//check extend
			var submit = angular.copy(product);
			submit.attrs = angular.toJson(submit.attrs);
			
			var url = utils.url('/admin/product/save.json');
			return $http.post(url, submit).success(function(data) {
				if(data.success){
					$scope.model.product = data.result;
				}
				$scope.saving = false;
			}).error(function() {
				$scope.saving = false;
			});
		}
	});
	module.controller('ProductController', [ '$injector', '$scope', ProductController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	return module;

});