define([ 'app/utils', 'ng/module/my', 'bootstrap' ], function(utils, my) {
	var module = angular.module('Page', [ my.name ], function() {
	});
	module.filter('keys', function() {
		return function(obj){
			if(obj && typeof obj == 'object'){
				return Object.keys(obj);
			}else{
				return [];
			}
		};
	});
	var CategoryController = function($injector, $scope) {
		this.$scope = $scope;
		this.$injector = $injector;
		$scope.view = {
			//显示在input里面的父类别 id+name
			parentDesc:null,
			newAttrs:{}
		};
		$scope.model = {
			copy:utils.getParam('copy'),
			//当前类别
			category:{
				attrs:{},
				inheritAttr:true,
				categoryId : utils.getParam('categoryId'),
			}
		};
		$scope.query = {};
		$scope.result = {
			categorys:null,
			parent:null,
			attrs:[]
		};
		this.load();
	};

	angular.extend(CategoryController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var me = this;
			if($scope.model.category.categoryId){
				var url = utils.url('/admin/category/load.json',{
					categoryId:$scope.model.category.categoryId
				});
				$http.get(url).success(function(data) {
					if(data.success){
						if( $scope.model.copy ){
							if(data.result.categoryId == null){
								alert('复制有风险，预期的ID不存在。');
							}
							delete data.result.categoryId;
						}
						$scope.model.category = data.result;
						//后面可能会修改attrs的内容，null会引起修改失败
						$scope.model.category.attrs = $scope.model.category.attrs || {};
						me.updateParentText();
					}
				});
			}
		},
		findTotalAttr:function(parent,$event){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			//test mergedAttrs, 不重复加载
			if(parent && parent.categoryId && !parent.mergedAttrs){
				var url = utils.url('/admin/category/attrs.json',{
					categoryId:parent.categoryId
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
						parent.mergedAttrs = attrObject;
						$($event.target).hide();
					}
				});
				
			}
			
		},
		updateParentText : function(){
			var $scope = this.$scope;
			var parentId = $scope.$eval('model.category.parentId');
			var categorys = $scope.$eval('result.categorys');
			
			if(parentId !=null && categorys && categorys.length){
				for (var i = 0; i < categorys.length; i++) {
					var rec = categorys[i];
					if(rec.categoryId == parentId){
						$scope.view.parentDesc = rec.categoryId + '.' + rec.name;
						$scope.result.parent = rec;
					}
				}
			}
		},
		initCategorys : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/category/find.json',{
				name:''
			});
			var me = this;
			$http.get(url).success(function(data) {
				if (data.success) {
					$scope.result.categorys = data.records;
					me.updateParentText();
				}
			});

		},
		changeParentCategory : function(event) {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var input = $scope.view.parentDesc;
			if(!input){
				return;
			}
			var rawid = input.substr(0, input.indexOf('.'));
			var id = parseInt(rawid);
			if (!isNaN(id) && id != $scope.lastQueryParentId) {
				$scope.lastQueryParentId = id;
				var url = utils.url('/admin/category/load.json',{
					categoryId:id
				});
				$http.get(url).success(function(data) {
					$scope.result.parent = data.result;
					$scope.model.category.parentId = data.result.categoryId;
				}).error(function() {
				});

			}
		},
		removeAttrFromCategory:function(attrCode){
			var $scope = this.$scope;
			delete $scope.model.category.attrs[attrCode];
		},
		addAttrToCategory:function(item){
			var $scope = this.$scope;
			try {
				var code = item.attrCode;
				var model = angular.fromJson(item['attrModel']||null);
				$scope.model.category.attrs[code] = model;
			} catch (e) {
				alert(utils.format('属性({0})的模型值({1})必须为有效的json字符串，请修改当前属性，重新加入。{2}', item.attrCode, item.attrModel,e.message));
			}
		},
		searchAttr:function(){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var attrCode = $scope.query.attrCode;
			var url = utils.url('/admin/attribute/query.json');
			return $http.post(url, {
				attrCode:attrCode
			}).success(function(data) {
				$scope.result.attrs = data;
			}).error(function() {
			});
		},
		saveAndReturn : function(form) {
			var promise = this.saveItem(form);
			if (promise) {
				promise.success(function(data) {
					window.location.href = utils.url('/admin/category.html');
				});
			}
		},
		saveItem : function(form) {
			var $http = this.$injector.get('$http');
			var $scope = this.$scope;
//			if (form.$pristine) {
//				return false;
//			}
			if (form.$invalid) {
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			$scope.saving = true;
			
			var model = $scope.model.category;
			var url = utils.url('/admin/category/save.json');
			if(angular.isObject(model.attrs)){
				//正常都是Object，有一次，不知道为啥变成string，导致无法添加属性。无法重现，
				model.attrs = angular.toJson(model.attrs||undefined);
			}
			return $http.post(url, model).success(function(data) {
				$scope.model.category = data.result;
				$scope.saving = false;
			}).error(function() {
				$scope.saving = false;
			});
		}
	});
	module.controller('CategoryController', [ '$injector', '$scope', CategoryController ]);

	var ngapp = document.getElementsByTagName('body')[0];
	angular.element(ngapp).ready(function() {
		angular.bootstrap(ngapp, [ module.name ]);
	});
	return module;

});