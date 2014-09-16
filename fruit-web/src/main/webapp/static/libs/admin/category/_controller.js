define([ './_module', 'app/utils' ,'jquery'],
	function(module, utils,jQuery) {
		var CategoryController = function($injector, $scope) {
			this.$scope = $scope;
			this.$injector = $injector;
			$scope.query = {};
			$scope.result = {};
			$scope.view = {};
			$scope.model = {};
			
			this.load();
		};

		angular.extend(CategoryController.prototype, {
			load:function(){
				var $http = this.$injector.get('$http');
				var $scope = this.$scope;
				var url = utils.url('/admin/category/find.json');
				var query = this.$scope.query;
				
				$http.post(url,null,{
					params : query
				}).success(function(data) {
					$scope.result = data;
				});
			},
			toggleTotalAttr:function(item,event){
				var $scope = this.$scope;
				var targetEl = jQuery(event.target);
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
			removeItem:function(index){
				if(!window.confirm('确认删除')){
					return;
				}
				var $scope = this.$scope;
				var $http = this.$injector.get('$http');
				var item =$scope.result.records[index];
				var categoryId = item.categoryId;
				var name = item.name;
				var url = utils.url('/admin/category/remove.json',{
					categoryId:categoryId,
					name:name
				});
				$http['delete'](url).success(function(data){
					if(data.success){
						$scope.result.records.splice(index,1);
						utils.notifySuccess('属性删除成功。');
					}
				});
				
			}
		});
		module.controller('CategoryController',
			[ '$injector', '$scope', CategoryController ]);

	});