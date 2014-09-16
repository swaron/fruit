/**
 * 弹出窗口使用bootstrap
 */
define([ './_module', 'app/utils', 'app/date','bootstrap' ], function(module, utils, date) {
	var EpisodeController = function($injector, $scope) {
		this.$injector = $injector;
		this.$scope = $scope;
		$scope.view = {
			popup:{
				title:'新增批次'
			}
		};
		$scope.model = {
			current:{
				
			}
		};
		$scope.query = {};
		$scope.result = {
			episodes:{},
			addrs:[]
		// records:
		};
		this.load();
	};
	angular.extend(EpisodeController.prototype, {
		load : function() {
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var url = utils.url('/admin/episode/find.json');
			$http.get(url).success(function(data) {
				$scope.result.episodes = data;
			});
		},
		ensureAddrs:function(){
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var addrs = $scope.$eval('result.addrs');
			if(addrs && addrs.length){
				//
			}else{
				var url = utils.url('/admin/episode/find-addrs.json');
				$http.get(url).success(function(data) {
					$scope.result.addrs = data.records;
				});
			}
		},
		addItem : function() {
			this.ensureAddrs();
			var $scope = this.$scope;
			var $filter = this.$injector.get('$filter');
			$scope.view.popup = {
//				title:'新增批次'
			};
			var now = new Date();
			var bookingTimeStart = date.add(now, 'day', 1);
			bookingTimeStart.setHours(2, 0, 0, 0);
			var bookingTimeEnd = date.add(now, 'day', 3);
			bookingTimeEnd.setHours(2, 0, 0, 0);
			
			$scope.model.current = {
				name : $filter('date')(date.add(now, 'day', 3),'yyyy-MM-dd 地点'),
				deliveryDate : date.add(now, 'day', 3).getTime(),
				bookingTimeStart : bookingTimeStart.getTime(),
				bookingTimeEnd : bookingTimeEnd.getTime(),
				activated : true
			};
			$('#edit-item-win').modal({
				backdrop : 'static'
			});
		},
		editItem : function(item) {
			this.ensureAddrs();
			var $scope = this.$scope;
			$scope.view.popup = {
//				title : '编辑批次'
			};
			$scope.model.current = item;
			$('#edit-item-win').modal({
				backdrop : 'static'
			});
		},
		copyItem : function(item) {
			this.ensureAddrs();
			var $scope = this.$scope;
			$scope.view.popup = {
				note:'(复制的副本)'
//				title : '复制批次'
			};
			$scope.model.current = angular.copy(item);
//			delete $scope.model.current.episodeId; 
			$scope.model.current.name = $scope.model.current.name + "-修改我";
			$scope.model.current._copy = true;
			$('#edit-item-win').modal({
				backdrop : 'static'
			});
		},
		removeItem:function(index){
			if(!window.confirm('确认删除')){
				return;
			}
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			var item =$scope.result.episodes.records[index];
			
			var url = utils.url('/admin/episode/remove.json',{
				episodeId:item.episodeId,
				name:item.name
			});
			$http['delete'](url).success(function(data){
				if(data.success){
					$scope.result.episodes.records.splice(index,1);
					utils.notifySuccess('删除成功。');
				}
			});
		},
		save : function(form, current) {
			var me = this;
			var $scope = this.$scope;
			var $http = this.$injector.get('$http');
			if (form.$pristine) {
				return false;
			}
			if (form.$invalid) {[]
				utils.shakeNgForm(form);
				return false;
			}
			if ($scope.saving) {
				return false;
			}
			$scope.saving = true;
			
			var url = utils.url('/admin/episode/save.json',{
				copy: $scope.model.current._copy
			});
			$http.post(url, $scope.model.current ).success(function(data) {
				utils.notifySuccess('保存成功');
				$scope.saving = false;
				$('#edit-item-win').modal('hide');
				me.load();
			}).error(function(data, status, headers, config) {
				$scope.saving = false;
			});
		}
	});
	module.controller('EpisodeController', [ '$injector', '$scope', EpisodeController ]);
});