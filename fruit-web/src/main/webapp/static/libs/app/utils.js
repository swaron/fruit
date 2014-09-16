define(["jquery"], function($) {
	var utils = {
		url : function(str, params) {
			var baseurl = str;
			if (String(str).indexOf('/') == 0) {
				var contextPath = AppConfig.contextPath || '';
				baseurl = contextPath + str;
			}
			if (params !== undefined) {
				if (typeof params != 'string' && !(params instanceof String)) {
					params = $.param(params);
				}
				if (baseurl.indexOf('?') == -1) {
					return baseurl + '?' + params;
				} else {
					return baseurl + '&' + params;
				}
			}
			return baseurl;
		},
		notifySuccess : function(message, keepTime) {
			var tpl = new String('<div class="alert alert-primary alert-popup fade"><button type="button" class="close" data-dismiss="alert">&times;</button> <div class="pull-left" style="padding:10px;"><i class="al al-zhengque al-4x text-success"></i></div><div class="alert-body text-success">{message}</div></div>');
			tpl = tpl.replace('{message}', message);
			var el = $(tpl);
			$(document.body).append(el);
			if(!el.alert){
				//bootstrap 没有加载,使用angular的方法
				el.find('button').on('click',function(){
					el.remove();
				});
			}
			el.addClass('in');
			if (keepTime === undefined) {
				keepTime = 1500;
			}
			if (keepTime && keepTime > 0) {
				window.setTimeout(function() {
					if(el == null){
						return;
					}
					if(el.alert){
						el.alert('close');
					}else{
						el.remove();
					}
				}, keepTime);
			}
		},
		notifyError : function(message, keepTime) {
			var tpl = new String('<div class="alert alert-warning alert-popup fade"><button type="button" class="close" data-dismiss="alert">&times;</button>{message}</div>');
			tpl = tpl.replace('{message}', message);
			var el = $(tpl);
			$(document.body).append(el);
			if(!el.alert){
				//bootstrap 没有加载,使用angular的方法
				el.find('button').on('click',function(){
					el.remove();
				});
			}
			el.addClass('in');
			if (keepTime === undefined) {
				keepTime = 10000;
			}
			if (keepTime && keepTime > 0) {
				window.setTimeout(function() {
					if(el == null){
						return;
					}
					if(el.alert){
						el.alert('close');
					}else{
						el.remove();
					}
				}, keepTime);
			}
		},
		format : function(str){
		    var args = arguments;
		    return str.replace(/\{(\d+)\}/g,function(m,i){
		    	return args[parseInt(i) + 1] || '';
		    });
		},
		urlFileName:function(){
			var path = window.location.pathname;
			if(path == '/'){
				return '';
			}
			var start = path.lastIndexOf('/') + 1;
			var end = path.lastIndexOf('.');
			if(end < start){
				end = undefined;
			}
			return path.substring(start,end);
		},
		getParam : function(paramKey) {
			var query = window.location.search;
			if(query && query.indexOf('?') == 0){
				query = query.substr(1);
			}
			var params = this.queryToObject(query);
			return params[paramKey];
		},
		fireEvent : function(element, event) {
			$(element).trigger(event);
//			on.emit(element, event, {
//				bubbles : true,
//				cancelable : true
//			});
		},
		ns : function(names) {
			var arr = names.split('.');
			var namespace = window;
			for (var i = 0; i < arr.length; i++) {
				if (typeof namespace[arr[i]] == 'undefined') {
					namespace[arr[i]] = {};
				}
				namespace = namespace[arr[i]];
			}
			return namespace;
		}, 
		substitute : function(template, data) {
			return template.replace(/\{([\w\.]*)\}/g, function(str, key) {
				if(data.hasOwnProperty(key)){
					return data[key];
				}else{
					return key;
				}
			});
		},
//		used for convert message.p.d.key to message['p.d.key']
		parseValue : function(obj, keys) {
			// <debug error>
			if (typeof keys !== 'string') {
				throw ("Invalid key, must be a string");
			}
			// </debug>
			var finalValue = obj;
			var keyList = keys.split('.');
			while (keyList.length > 0) {
				var key = keyList.shift();
				finalValue = finalValue[key];
				if (finalValue == undefined || finalValue == null) {
					break;
				}
			}
			return finalValue;
		},
		shakeNgForm:function(form){
			var formEl = $(document.forms[form.$name]);
			var me = this;
			formEl.find("input,select").each(function() {
				var key = $(this).attr('name');
				if (form[key] && form[key].$invalid) {
					this.addClass('shaked');
					me.shake(this, 1000);
				}
			});
		},
		shake:function(el,interval){
			interval = interval || 1500;
			$(el).removeClass('shake').addClass('shake shake-constant shake-vertical');
			var set = setTimeout(function(){  
	            $(el).removeClass("shake shake-constant shake-vertical");      
	        },interval);
			return set;
		},
		queryToObject: function queryToObject(/*String*/ str){
	        // summary:
	        //		Create an object representing a de-serialized query section of a
	        //		URL. Query keys with multiple values are returned in an array.
	        //
	        // example:
	        //		This string:
	        //
	        //	|		"foo=bar&foo=baz&thinger=%20spaces%20=blah&zonk=blarg&"
	        //
	        //		results in this object structure:
	        //
	        //	|		{
	        //	|			foo: [ "bar", "baz" ],
	        //	|			thinger: " spaces =blah",
	        //	|			zonk: "blarg"
	        //	|		}
	        //
	        //		Note that spaces and other urlencoded entities are correctly
	        //		handled.

	        // FIXME: should we grab the URL string if we're not passed one?
	        var dec = decodeURIComponent, qp = str.split("&"), ret = {}, name, val;
	        for(var i = 0, l = qp.length, item; i < l; ++i){
	            item = qp[i];
	            if(item.length){
	                var s = item.indexOf("=");
	                if(s < 0){
	                    name = dec(item);
	                    val = "";
	                }else{
	                    name = dec(item.slice(0, s));
	                    val  = dec(item.slice(s + 1));
	                }
	                if(typeof ret[name] == "string"){ // inline'd type check
	                    ret[name] = [ret[name]];
	                }

	                if( ret[name] instanceof Array){
	                    ret[name].push(val);
	                }else{
	                    ret[name] = val;
	                }
	            }
	        }
	        return ret; // Object
	    }
	};
	//app.repo.
	//app.user.
	//app.str, 
	//app.base.utils
	window.app = window.app || {};
	$.extend(window.app,{
		utils:utils
	});
	return utils;
});
