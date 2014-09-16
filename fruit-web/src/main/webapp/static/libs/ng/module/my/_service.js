define([ './_module', 'app/utils','app/cookie','require' ,'dojox/encoding/digests/SHA256','jquery'], function(module, utils,cookie, require,sha256,jquery) {
	var cacheFactory = [ '$http','$q', function($http,$q) {
		var CacheService = function() {
			//this is episode product, not product.
			this.products = {};
			this.episodes = {};
			this.loading = {};
		};
		angular.extend(CacheService.prototype, {
			loadProducts : function(episodeProductId) {
				var products = this.products;
				var loading = this.loading;
				if (episodeProductId !=null) {
					if (loading[episodeProductId] ) {
						return loading[episodeProductId];
					} else {
						var deferred = $q.defer();
						loading[episodeProductId] = deferred.promise;
						var url = utils.url('/page/episode/product.json', {
							episodeProductId : episodeProductId
						});
						$http.get(url).success(function(data) {
							if (data.success && data.result.product.episodeProductId) {
								products[data.result.product.episodeProductId] = data.result.product;
								deferred.resolve(data.result.product);
							}
						});
						return loading[episodeProductId];
					}
				}
			},
			loadEpisode : function(episodeId) {
				//uuid 不会重复的，就算是 episode和episodeProduct2个种类
				var episodes = this.episodes;
				var loading = this.loading;
				if (episodeId !=null) {
					if (loading[episodeId] ) {
						return loading[episodeId];
					} else {
						var deferred = $q.defer();
						loading[episodeId] = deferred.promise;
						var url = utils.url('/page/episode/episode.json', {
							episodeId : episodeId
						});
						$http.get(url).success(function(data) {
							if (data.success && data.result) {
								episodes[data.result.episodeId] = data.result;
								deferred.resolve(data.result);
							}
						});
						return loading[episodeId];
					}
				}
			}
		});

		return new CacheService();
	} ];
	var cartFactory = ['$rootScope', '$http','_repo', function($rootScope,$http,_repo) {
		var CartService = function() {
			var me = this;
			$rootScope._cart = me;
		};
		angular.extend(CartService.prototype, {
			getCount:function(){
				var count = $rootScope.$eval('_repo.model.cart.count');
				if(typeof count == 'number'){
					return count;
				}else{
					return 0;
				}
			},
			clearCart:function(){
				var cart = _repo.get('cart') || {};
				cart.items = {};
//				cart.count = 0;
				var updates = [ {
					type : 'clear'
				} ];
				return this.updateCart(updates);
			},
			updateCart : function(updates,force) {
				var cart = _repo.get('cart') || {};
				var sdata = {
					force:force,
					cart: cart, //ShoppingCart{version:,items:,}
					updates:updates
				};
				
				var url = utils.url('/page/cart/update-cart.json');
				return $http.post(url, sdata).success(function(data) {
					if (data.success && data.result) {
						var result = data.result;
						if (result.code == 1) {
							_repo.set('cart', result.cart, Date.now() + 1000*3600*24*7);
						} else if (result.code == 2) {
							//未使用
							var tpl = '修改购物车时收到信息：购物车于 {stime} 在IP:{ip}通过{device} 修改 {ptitle}。数量为{count} \n';
							var msgs = [];
							if (result.merged && result.merged.length) {
								for (var i = 0; i < result.merged.length; i++) {
									var m = result.merged[i];
									var action = '未知';
									var count = '';
									if (m.type = '+') {
										if (m.count > 0) {
											// action = '添加';
											count = m.count;
										} else if (m.count < 0) {
											// action = '减少';
											count = m.count;
										}
									} else if (m.type = '*') {
										action = '清空';
									}
									var msg = utils.substitude(tpl, {
										stime : new Date(m.stime),
										ip : m.ip,
										device : m.dev,
										action : action,
										count : count,
										ptitle : m.ptitle
									});
									msgs.push(msg);
								}
							}
							var msg = '收到了其他购物车更新信息，已经获取了最新的购物车信息。';
							_repo.set('cart', result.cart, Date.now() + 1000*3600*24*7);
							alert(msg);
						}
					}else{
						//alert("购买失败，请联系客服。")
						//data.success 购物车修改失败,忽略
					}
				}).error(function(data) {
					// 购物车修改失败
				});
			}
		})
		return new CartService();
	} ];
	var loginFactory = ['$compile','$rootScope','$http','$q','_repo', function( $compile, $rootScope, $http,$q,_repo) {
		var LoginService = function() {
			var me = this;
			$rootScope._login = me;
			me.model = {
				autoLogin:true
			};
//			me.load();
		};
		var resultDefer = $q.defer();
		var elDefer = $q.defer();
		angular.extend(LoginService.prototype, {
			load:function(){
				var me = this;
				//modal dialog 需要bootstrap
				require(['jquery','text!./login.html','bootstrap'],function($,html){
					var el = $(html);
					$('body').append(el);
					$compile(el)($rootScope);
					elDefer.resolve(el);
				});
			},
			popup : function() {
				var me = this;
				me.load();
				elDefer.promise.then(function(el){
					el.modal('show');
				});
				return resultDefer.promise;
			},
			wantPassword:function($event){
				$event.preventDefault();
				alert('哎呀呀，真抱歉，目前只能联系客服了。');
			},
			submit : function(form){
				var username = form.username.$modelValue;
				var password = form.password.$modelValue;
				var autoLogin = form.autoLogin.$modelValue;
				var me = this;
				if (form.$invalid) {
					utils.shakeNgForm(form);
					return false;
				}
				if (me.saving) {
					return false;
				}
				me.saving = true;
				$http.post(utils.url('/page/login/step1.json'),null,{
					params:{
						username: username,
					}
				}).success(function(data){
					if (data.success && data.result) {
						// 安全提示: 1. 普通网站，QQ，银行卡，支付宝等重要程度不同的地方密码不要设置为一样
						var userId = data.result.userId;
						var challenge = data.result.challenge;
						var pwd = password;
						var starMillis = Date.now();
						pwd = sha256(userId + '{' + pwd + '}', sha256.outputTypes.Hex);
						for (var i = 1; i < 1000; i++) {
							pwd = sha256(pwd, sha256.outputTypes.Hex);
						}
						var endMillis = Date.now();
						console.log('hash time: ' + (endMillis - starMillis));
						console.log("userId: " + userId);
//						console.log("password: " + userId + '{' + password + '}');
						console.log("hash pwd: " + pwd);
						var base64Answer = sha256._hmac(challenge, pwd);
						console.log("challenge: " + challenge);
						console.log("base64answer: " + base64Answer)

						$http.post(utils.url('/page/login/step2.json'),null,{
							params:{
								autoLogin: autoLogin,
								username: username,
								answer : base64Answer,
								userId : userId
							}
						}).success(function(data) {
							if (data.success) {
								// cookie should be set by server with success response
								_repo.set('user', data.result, data.result.expireTime);
								resultDefer.resolve(data.result);
							} else {
								alert('登陆失败，看来是密码错了。')
								//me.markError(query('input[name="pwd"]', formDom), data.message);
							}
							me.saving = false;
						});
						// 得到uuid,hmac
					} else {
						alert('用户' + username + "不存在");
						me.saving = false;
//						me.markError(query('input[name="usr"]', formDom), "用户名不存在。");
					}
				}).error(function(){
					me.saving = false;
				});
				return resultDefer.promise;
			}
		})
		return new LoginService();
	} ];
	var userFactory = ['$compile','$rootScope', '$http','_repo', function($compile,$rootScope,$http,_repo) {
		var addressPopupWidget = null;
		var UserService = function() {
			$rootScope._user = this;
			this.$http = $http;
			this.result = {
				addresses : []
			};
			this.model = {
			};
			this.init();
		};
		UserService.prototype = {
			init:function(){
				var me = this;
				var user = _repo.get('user');
				if(user && user.roles && (user.roles.indexOf('admin') != -1)){
					require(['jquery','text!./admin-panel.html', 'bootstrap'],function($,html){
						var el = $(html);
						$('body').append(el);
						$compile(el)($rootScope);
					});
				}
				if(user){
					//localstorage里面用户的有效性完全可以通过session里面的值来
					if(cookie('AUTH-FLAG')){
						//有效
					}else{
						//无效，移除
						_repo.remove('user');
					}
				}
				me.isAuthenticated = (_repo.model.user || {}).authenticated ;
				me.checkAddress();
			},
			logout:function(){
				var url = utils.url('/page/logout.json', {
				});
				$http.get(url).success(function(data) {
					if (data.success) {
						_repo.remove('user');
						_repo.remove('cart');
//						_repo.set('address',{},-1);
						window.location.href = utils.url('/');
					}
				});
			},
			loadAddress:function(){
				var $http = this.$http;
				var me = this;
				var url = utils.url('/page/pub/address/list.json');
//				jquery.ajax({
//					headers:{
//						"X-XSRF-TOKEN": cookie('XSRF-TOKEN')
//					},
//					url:url,
//					dataType:'json',
//					success:function(data){
//						if (data.success && data.records) {
//							me.result.addresses = data.records;
//							$rootScope.$digest();
//						}else{
//						}
//					}
//				});
				$http.get(url).success(function(data, status, headers, config) {
					if (data.success && data.records) {
						me.result.addresses = data.records;
					}else{
					}
				}).error(function(data,status){
				});
			},
			selectAddress : function() {
				var me = this;
				if(addressPopupWidget != null){
					addressPopupWidget.modal('show');
					return;
				}
//				me.loadAddress();
				require(['jquery','text!./choose-address.html', 'bootstrap'],function($,html){
					var el = $(html);
					$('body').append(el);
//					me.elDefer.resolve(me.el);
					addressPopupWidget = el;
					addressPopupWidget.modal('show');
					$compile(el)($rootScope);
				});
			},
			checkAddress : function() {
				var addr = _repo.model.address;
				if(addr == null){
					this.selectAddress();
				}
			},
			storeAddress:function(item){
				_repo.set('address',item,Date.now() + 2*365*24*3600*1000);
				if(window.location.pathname != utils.url("/") && window.location.pathname !=  utils.url("/page/episode/current.html")){
					window.location.href = utils.url("/");
				}
			}
		};
		return new UserService();
	} ];
	var formFactory = ['$compile','$rootScope', function($compile,$rootScope) {
		var FormService = function() {
			this.result = {
			};
		};
		FormService.prototype = {
//				var formEl = $(document.forms[form.$name]);
//				var me = this;
//				formEl.find("input,select").each(function() {
//					var key = $(this).attr('name');
//					if (form[key] && form[key].$invalid) {
//						me.shake(this, 1000);
//					}
//				});
				markError:function(model,errorMsg){
					var el = $('form[name] [name="'+model.$name+ '"]');
					el.siblings('.help-block').hide();
					el.siblings('.error-msg').remove();
					el.after('<div class="error-msg"><i class="fa fa-warning"> </i> ' + errorMsg
							+ ' </div>');
				}
		};
		return new FormService();
	} ];
	module.factory('_cache', cacheFactory);
	module.factory('_cart', cartFactory);
	module.factory('_login', loginFactory);
	module.factory('_user', userFactory);
	module.factory('_form', formFactory);
});