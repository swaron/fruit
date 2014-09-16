define(
	[ "./_module", "jquery", "app/errors", "app/utils", "require" ],
	function(module, $, messages, utils, require) {
		var myErrorsDirective = [
			'$filter',
			function($filter) {
				var link = function(scope, element, attrs, ngModel) {
					if (!ngModel) {
						return;
					}
					var el = $(element);
					var form = el.closest('form').attr('name');
					ngModel.errors = ngModel.errors || {};
					if (scope[form] && scope[form][ngModel.$name]) {
						var expr = form + '.' + ngModel.$name + '.$error';
						scope.$watchCollection(expr, function(errors) {
							// scope.$watch(expr,function(){
							if (ngModel.$pristine) {
								return;
							}
							if (ngModel.$valid) {
								el.siblings('.error-msg').remove();
								el.siblings('.help-block').show();
							} else {
								for ( var key in errors) {
									if (errors[key] === false) {
										continue;
									}
									el.siblings('.help-block').hide();
									el.siblings('.error-msg').remove();
									
									var errorMsg = attrs[key + 'Msg'];
									if (errorMsg) {
										errorMsg = utils.format(errorMsg, attrs[key], ngModel.$viewValue);
									} else if (messages[key]) {
										errorMsg = utils.format(messages[key], attrs[key], ngModel.$viewValue);
									}
									if(ngModel.errors[key]){
										errorMsg = ngModel.errors[key];
									}
									if(errorMsg){
										el.after('<div class="error-msg"><i class="fa fa-warning"> </i> ' + errorMsg
												+ ' </div>');
									}
									break;
								}
							}

						});
					} else {
						console.log('warning, field not found.');
					}
//					ngModel.$viewChangeListeners.push(function() {
//						if (ngModel.$dirty && ngModel.$invalid && ngModel.$error) {
//							console.log('invalid: ' + JSON.stringify(ngModel.$error));
//						} else {
//							console.log('valid: ' + JSON.stringify(ngModel.$error));
//						}
//					});
					ngModel.$validators['custom']= function(){
						return true;
					};
				};
				return {
					require : '?ngModel',
					restrict : 'A',
					link : link
				};
			} ];
		var myVisibleDirective = [ '$animate', function($animate) {
			return function(scope, element, attr) {
				scope.$watch(attr.myVisible, function ngShowWatchAction(value) {
					$animate[(!!value) ? 'removeClass' : 'addClass'](element, 'my-invisible');
				});
			};
		} ];

		var myInvisibleDirective = [ '$animate', function($animate) {
			return function(scope, element, attr) {
				scope.$watch(attr.myInvisible, function ngHideWatchAction(value) {
					$animate[(!!value) ? 'addClass' : 'removeClass'](element, 'my-invisible');
				});
			};
		} ];
		var myFilter = [ function() {
			return {
				scope : {
					model : '=myFilter',
					query : '&onQuery'
				},
				controller : [ '$scope', function($scope) {
					this.finishOnEnter = function(filter, event) {
						var me = this;
						if (event.keyCode == 13) {
							filter.editing = false;
						}
					};
					this.removeFilter = function(index) {
						$scope.model.filters.splice(index, 1);
					};
					this.addFilter = function(filter) {
						if (filter.property && filter.operator) {
							if(filter.property && filter.value && (filter.value.indexOf('%') == -1) ){
								filter.value = '%' + filter.value + '%';
							}
							if (!$scope.model.filters) {
								$scope.model.filters = [];
							}
							$scope.model.filters.push(angular.copy(filter));
						}
					};
				} ],
				link : function(scope, element, attrs) {

				},
				controllerAs : 'c',
				templateUrl : require.toUrl('./myfilter.htm')
			};
		} ];
		var myAttrNode = [
			'$compile',
			function($compile) {
				return {
					restrict : 'EA',
					scope : {
						attrCode : '=',
						attrType : '=attrType',
						attrModel : '=attrModel',
						attrValue : '='
					},
					template : '<div></div>',
					controllerAs : 'c',
					link : function(scope, element, attrs) {
						// <my-attr-node attr-type="attr.frontendInput"
						// attr-model="attr.attrModel" model=""></my-attr-node>
						var type = scope.attrType;
						// var model = scope.model='["红富士","嘉莉诗"]';
						// select,text,hidden,date,datetime,boolean,
						// image,textarea,multiselect,price,weight,
						// multiimage,number,email
						var html = '<input type="text" class="form-control" name="{{::attrCode}}" ng-model="attrValue" />';
						if (type == 'select') {
							html = '<select class="form-control" name="{{::attrCode}}" ng-model="attrValue" ng-options="attr for attr in attrModel" ><option value="">请选择...</option></select>';
						} else if (type == 'multiselect') {
							html = '<select class="form-control" name="{{::attrCode}}" ng-model="attrValue" multiple="multiple" ng-options="attr for attr in attrModel" ><option value="">请选择...</option></select>';
						} else if (type == 'textarea') {
							html = '<textarea class="form-control" name="{{::attrCode}}" ng-model="attrValue" ></textarea>';
						} else if (type == 'image' || type == 'multiimage' || type == 'multifile') {
							var btnDesc = {
								image:'图片',
								multiimage:'图片集',
								multifile:'文件集'
							};
							html = [
								'<button class="btn btn-primary" data-toggle="modal" href="" data-target="#image_upload_dialog_{{::attrCode}}">选择',btnDesc[type],'</button>',
								'<div><span ng-repeat="file in attrValue" ><i ng-class="file.name | myFileTypeCls"> </i> {{::file.name}}<br/><img class="img-responsive" ng-src="{{::file.url}}">&nbsp;</span> </div>',
								'<div id="image_upload_dialog_{{::attrCode}}" class="modal " tabindex="-1" role="dialog" aria-hidden="true">',
								'	<div class="modal-dialog modal-lg" >',
								'		<div class="modal-content" style="height:500px;">',
								'<div class="modal-header model-header-small">',
								'<span>&nbsp;</span>',
								'	<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>',
								'</div>',
								'			<iframe name="image_upload_iframe_'+scope.attrCode+'" width="100%" height="100%" frameborder="0" src="about:blank;"></iframe>',
								'		</div>', '	</div>', '</div>' ].join('');
							require([ 'jquery','bootstrap' ], function(jquery) {
								//load bootstrapjs
								jquery('#image_upload_dialog_' + scope.attrCode).on('hidden.bs.modal', function(e) {
									var files = window.frames['image_upload_iframe_' + scope.attrCode].getUploadedFiles();
									if (files && files.length) {
										var attrValue = [];
										for (var i = 0; i < files.length; i++) {
											var file = files[i];
											attrValue.push({
												id : file.id,
												name : file.name,
												url : file.url
											});
										}
										scope.manualValue = attrValue;
										// 这个model的变化需要手动触发
										scope.$apply('attrValue=manualValue');
									}
								});
								jquery('#image_upload_dialog_' + scope.attrCode).on(
									'show.bs.modal',
									function(e) {
										if (scope.attrValue) {
											// window.frames['image_upload_iframe'].location.reload();
										}
										window.frames['image_upload_iframe_' + scope.attrCode].location = utils.url('/upload/iframe.html',{v:AppConfig.version});
									});
							});
						} else if (type == 'text') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="text" />';
						} else if (type == 'statictext') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="statictext" list="statictext_dl_{{attrCode}}" />'
									+ '<datalist id="statictext_dl_{{attrCode}}"><option ng-repeat="attr in attrModel" value="{{attr}}" /></datalist>';
						} else if (type == 'date') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="date"/>';
						} else if (type == 'datetime') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="datetime-local"/>';
						} else if (type == 'boolean') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="checkbox"/>';
						} else if (type == 'number') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="number"/>';
						} else if (type == 'price' || type == 'weight') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="number" step="0.01" />';
						} else if (type == 'email') {
							html = '<input class="form-control" name="{{::attrCode}}" ng-model="attrValue" type="email"/>';
						}
						element.html(html);
						$compile(element.contents())(scope);

					}
				};
			} ];
		var requiredDirective = [ function() {
			return function(scope, element, attr) {
				var id = attr.id || attr.name;
				if (id) {
					require([ 'jquery' ], function(jquery) {
						var labels = jquery('label[for="' + id + '"]');
						if (labels.length) {
							var label = labels.first().text().trim();
							if (label.indexOf('*') == -1) {
								labels.first().append('<span class="text-danger">*</span>');
							}
						}
					});
				}
			};
		} ];
		var myAutofillDirective = [ function() {
			return {
				require: 'ngModel',
				link:function(scope, element, attr,ngModel) {
					var origVal = element.val();
					if(origVal && ngModel.$modelValue != null){
						ngModel.$setViewValue(origVal);
//						ngModel.$modelValue = ngModel.$modelValue || origVal;
					}
				}
			};
		} ];
		/**
		 * it is ok to use ng-list in most case.
		 */
		var myJsonValueDirective = [ function() {
			return {
				require : 'ngModel',
				link : function(scope, element, attrs, ngModel) {
					ngModel.$formatters.push(function(obj) {
						if (!obj) {
							return obj;
						}
						try {
							return angular.toJson(obj);
						} catch (e) {
							return obj;
						}
					});
					ngModel.$parsers.push(function(view) {
						if (!view) {
							return view;
						}
						try {
							return angular.fromJson(view);
						} catch (e) {
							return view;
						}
					});
				}
			};
		} ];
		var myOverflowEllipsisDirective = [ function() {
			return {
				link : function(scope, element, attrs) {
					var toWatch = attrs.myOverflowEllipsis;
					scope.$watch(toWatch,function(newVal){
						var text = element.text();//取得原文
						var innerSpanHeight = element[0].offsetHeight; //span的高度，
						var outerDivHeight = element.parent()[0].offsetHeight; //外围div的高度
						for (var i = text.length-1; i > 0 && innerSpanHeight > outerDivHeight; i--) {
							//如果span高度大于div，那么去掉最后一个字符+‘...’，直到 innerSpanHeight<=outerDivHeight.
							element.text( text.substr(0,i) + '...');
							innerSpanHeight = element[0].offsetHeight;
						}
					});
				}
			};
		} ];
		/**
		 * 处理日期是用long来存的情形
		 */
		var myDateDirective = [ function() {
			return {
				require : 'ngModel',
				//要大于默认的input的0
				priority:2,
				link : function(scope, elm, attrs, ngModelCtrl) {
					ngModelCtrl.$formatters.push(function(modelValue) {
						if(typeof modelValue == 'number'){
							return new Date(modelValue);
						}else{
							return modelValue;
						}
					});
					ngModelCtrl.$parsers.push(function(viewValue) {
						if(viewValue instanceof Date){
							return viewValue.getTime();
						}else{
							return viewValue;
						}
					});
				},
			};
			
		}];
		module.directive('myErrors', myErrorsDirective);
		module.directive('myVisible', myVisibleDirective);
		module.directive('myInvisible', myInvisibleDirective);
		module.directive('myFilter', myFilter);
		module.directive('myAttrNode', myAttrNode);
		module.directive('required', requiredDirective);
		module.directive('myAutofill', myAutofillDirective);
		module.directive('myJsonValue', myJsonValueDirective);
		module.directive('myOverflowEllipsis', myOverflowEllipsisDirective);
		module.directive('myDate',  myDateDirective);
	});
