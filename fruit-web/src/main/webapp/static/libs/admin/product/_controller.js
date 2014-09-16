define([ './_module', 'app/utils' ],
	function(module, utils) {
		var Controller = function($injector, $scope) {
			this.$scope = $scope;
			this.$injector = $injector;
			$scope.query = {};
			$scope.result = {};
			$scope.view = {};
			$scope.model = {};
			
			this.filter = {
//				filters:[{
//					property : 'category_id',
//					value : 3,
//					operator : '='
//				}],
				columns : [{
					name:'name',
					text:'产品名称',
					checked:false
				},{
					name:"attrs->>'产品标题'",
					text:'产品标题',
					checked:false
				},{
					name:"attrs->>'产品子标题'",
					text:'产品子标题',
					checked:false
				}]
			};
		};

		angular.extend(Controller.prototype, {
			load:function(){
				var filters = this.filter.filters;
				var $scope = this.$scope;
				var $http = this.$injector.get('$http');
				var url = utils.url('/admin/product/list.json');
				$http.post(url, {
					start:null,
					limit:null,
					sort:[],
					filter:filters
				}).success(function(data) {
					$scope.result = data;
				}).error(function(data, status, headers, config) {
					// called asynchronously if an error occurs
					// or server returns response with an error status.
				});
			},
			removeItem:function(index){
				if(!window.confirm('确认删除')){
					return;
				}
				var $scope = this.$scope;
				var $http = this.$injector.get('$http');
				var item =$scope.result.records[index];
				var productId = item.productId;
				var name = item.name;
				var url = utils.url('/admin/product/remove.json',{
					productId:productId,
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
		module.controller('ProductController',
			[ '$injector', '$scope', Controller ]);

	});