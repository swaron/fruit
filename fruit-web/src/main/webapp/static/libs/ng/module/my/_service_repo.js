define([ './_module' ], function(module) {
	var resultSample2 = {
		code : 2,// 1.success, 2. merged(收到其他设备对购物车的更新。)
		merged : [ {
			ip : 'ip',
			dev: 'device',
			epid : '',
			ptitle: '',
			type : '+',
			count : '-1',
			stime : 'now'
		} ],
		cart:{
			version : 125,
			count: 12,
			items : {
				epid : '...'
			}
		}
	};
	
	var cartSample = {
			version : 21345,
			items:{
				'epid1' : {
					enabled : true,
					stime : 123, // long, submit time in ms
					etime : 123, // expire time
					epid : 'pid1',
					count : 1
				},
				'epid2':{
					
				}
			}
		};
	var user_sample = {
		location:null,//选择的地址，目前是具体的一个配送地址
		nickname : null,
		authenticated:false,
//		logout:false,
		actived:true,
		expireTime : null,
		roles : [],
		updateTime : null
	}
	
	module.factory('_repo', ['$rootScope','$window', function($rootScope,window) {
		var supported = ('localStorage' in window) && ('setItem' in window.localStorage);
		if (!supported) {
			var noop = function(){};
			window.localStorage = {
				getItem:noop,
				setItem:noop,
				removeItem:noop,
				clear:noop
			}
			utils.notifyError('获取本地存储失败，部分功能不可用。 使用隐身模式或者低版本的IE会出现此情况。 解决方案： 使用微信访问或者换IE9+/Chrome/Firefox/Safari.',10000);
		}
		var Repo = function() {
			var me = this;
			me.model = {};
			$rootScope._repo = me;
			me.load(true);
			angular.element(window).on('storage',function(event){
				var key = event.key;
				var oldValue = event.oldValue;
				var newValue = event.newValue;
				var uri = event.url||event.uri;
				var clear = key == null;
				me.load(false);
				//storage事件需要手动digest,
				//不能保证 storage事件不会在源tab触发。如果在源tab触发，会导致"$digest already in progress"错误。
				if(!$rootScope.$$phase) {
					//$digest or $apply
					$rootScope.$digest();
				}
			});
		};
		
		angular.extend(Repo.prototype, {
			local:'',
			session:'',
			keys : [ 'user','cart','address' ],
			load:function(removeExpired){
				var me = this;
				for (var i = 0; i < me.keys.length; i++) {
					var key = me.keys[i];
					var itemValue = null;
					try {
						var itemString = localStorage.getItem(key);
						if(itemString == null){
							continue;
						}
						itemValue = angular.fromJson(itemString);
					} catch (e) {
					}
					if(itemValue && itemValue.expireTime && Date.now() < itemValue.expireTime){
						me.model[key] = itemValue;
					}else if(itemValue.expireTime === -1 ){
						//等于-1 是session的周期
						me.model[key] = itemValue;
						//expired
					}else if(removeExpired === true){
						//ie 的storage事件会在自生tab里面执行。
						me.remove(key);
					}
				}
			},
			get : function(key) {
				if(key==null){
					return null;
				}
				return $rootScope.$eval("_repo.model." +key);
			},
			set : function(key,item,expireTime) {
				if(key && item){
					this.model[key] = item;
					item.expireTime = expireTime || item.expireTime;
					var str = angular.toJson(item);
					try {
						localStorage.setItem(key, str);
					} catch (e) {
						//safari 无痕浏览时无法设置item
					}
				}
			},
			remove:function(key){
				if(key){
					this.model[key] = null;
					try {
						localStorage.removeItem(key);
					} catch (e) {
						//safari 无痕浏览时无法设置item
					}
				}
			}
		});
		var repo = new Repo();
		return repo;
	} ]);
});