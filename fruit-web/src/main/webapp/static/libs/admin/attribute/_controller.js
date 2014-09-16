define([ './_module', 'app/utils'],
	function(module, utils) {
		var AttrController = function($injector, $scope) {
			this.$scope = $scope;
			this.$injector = $injector;
			var $http = this.$injector.get('$http');
			$scope.form = {
				system: null
			};
			this.load();
		};

		angular.extend(AttrController.prototype, {
			load:function(){
				var $http = this.$injector.get('$http');
				var $scope = this.$scope;
				var url = utils.url('/admin/attribute/query.json');
				var form = this.$scope.form;
				
				$http.post(url, {
					attrCode : form.attr_code, // 属性的名称代码
					frontendLabel : form.frontend_label,
					system : form.system
				}).success(function(data) {
					$scope.result = data;
				});
			},
			removeItem:function(index){
				if(!window.confirm('确认删除')){
					return;
				}
				var $scope = this.$scope;
				var $http = this.$injector.get('$http');
				var item =$scope.result.records[index];
				var attrId = item.attrId;
				var attrCode = item.attrCode;
				var url = utils.url('/admin/attribute/remove.json',{
					attrId:attrId,
					attrCode:attrCode
				});
				$http['delete'](url).success(function(data){
					if(data.success){
						$scope.result.records.splice(index,1);
						utils.notifySuccess('属性删除成功。');
					}
				});
			}
		});
		module.controller('AttrController',
			[ '$injector', '$scope', AttrController ]);

	});