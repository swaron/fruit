/**
 * 这个文件存放一些Lib的直接属性
 * 
 * @type
 */
Ext.require('Ext.util.Cookies');
var Lib = Lib || {};
Ext.apply(Lib, {
	Config : {
		// cfg要在每个应用里面覆盖
		dbStorePath : '/app-web' + '/rest/db-store/app-repo',
		enumCodeUrl : '/app-web' + '/rest/code/enum.json'
	},
	url : function(str,params) {
		var baseurl = str;
		if (String(str).indexOf('/') == 0) {
			var contextPath = App.cfg.contextPath || '';
			baseurl =  contextPath + str;
		}
		if(params !== undefined){
			if(Ext.isObject(params)){
				return Ext.urlAppend(baseurl, Ext.Object.toQueryString(params));
			}else if(Ext.isString(params)){
				return Ext.urlAppend(baseurl, params);
			}
		}
		return baseurl;
	},
	rest : function(str,params) {
		var baseurl = str;
		if (String(str).indexOf('/') == 0) {
			var contextPath = App.cfg.restUrl || '';
			baseurl =  contextPath + str;
		}
		if(params !== undefined){
			if(Ext.isObject(params)){
				return Ext.urlAppend(baseurl, Ext.Object.toQueryString(params));
			}else if(Ext.isString(params)){
				return Ext.urlAppend(baseurl, params);
			}
		}
		return baseurl;
	},
	openWindow:function(options){
		var form = Ext.widget('form',{
			standardSubmit:true,
			hidden:true,
			renderTo:Ext.getBody()
		});
		if(options.target){
			form.getEl().set({
	            target: options.target
	        });
		}
		form.submit(options);
//		form.close();
	},
	mask : function(maskText) {
		Ext.getBody().mask(maskText);
	},
	unmask : function() {
		Ext.getBody().unmask();
	},
	log : function(args) {
		if (/* Lib.Config.debug && */window.console && console.log) {
			var args = Ext.toArray(arguments);
//			args.push(arguments.callee.caller);
			if(console.log.apply) {
				console.log.apply(console,args);
			}
		}
	},
	fireEvent : function(element, event) {
		if (document.createEvent) {
			// dispatch for firefox + others
			var evt = document.createEvent("HTMLEvents");
			evt.initEvent(event, true, true); // event type,bubbling,cancelable
			return !element.dispatchEvent(evt);
		} else if (document.createEventObject) {
			// dispatch for IE 6,7,8
			var evt = document.createEventObject();
			return element.fireEvent('on' + event, evt);
		} else {
			var fn = element[event];
			if (Ext.isFunction(fn)) {
				fn();
			}
		}
	},
	getParam : function(paramKey) {
		var query = window.location.search;
		var params = Ext.Object.fromQueryString(query);
		return params[paramKey];
	},
	substitute : function(template, data) {
		var me = this;
		return template.replace(/\{([\w\.]*)\}/g, function(str, key) {
			var value = me.parseValue(data,key);
			return (value === null || value === undefined) ? "" : value;
		});
	},
	objectToFilters : function(obj, hasOperator, defaultConfig) {
		var filters = [];
		Ext.Object.each(obj, function(key, value, me) {
			// value = Ext.String.trim(value);
			if (Ext.isString(value) && !value) {
				// ignore blank strings;
				return true;
			}
			if(hasOperator === true){
				if (Ext.Array.contains(['<', '=', '>', '<=', '>=', '<>', '!='], value)) {
					// ignore string with only operators but no value
					return true;
				}
			}
			var filter = Ext.applyIf({
				property : key,
				value : value
			},defaultConfig);
			filters.push(filter);
		});
		return filters;
	},
	addFilters : function(filters, config) {
		// ignore blank strings and operator;
		if (Ext.Array.contains(['','<', '=', '>', '<=', '>=', '<>', '!='], config.value)) {
			return;
		}
		filters.push(config);
	},
	//移除部分filter(properties)
	removeFilters : function(filters, properties) {
		if (!Ext.isArray(filters)) {
			if (filters === undefined) {
				filters = [];
			} else {
				filters = [filters];
			}
		}
		if (!Ext.isArray(properties)) {
			if (properties === undefined) {
				properties = [];
			} else {
				properties = [properties];
			}
		}
		Ext.Array.each(Ext.Array.clone(filters), function(item, index) {
			if (Ext.Array.contains(properties, item.property)) {
				Ext.Array.remove(filters,item);
			}
		});
		return filters;
	},
	//给部分filter(properties)添加属性(config)，
	applyFilters : function(filters, properties, config) {
		if (!Ext.isArray(filters)) {
			if (filters === undefined) {
				filters = [];
			} else {
				filters = [filters];
			}
		}
		if (!Ext.isArray(properties)) {
			if (properties === undefined) {
				properties = [];
			} else {
				properties = [properties];
			}
		}
		Ext.Array.each(filters, function(item, index) {
			if (Ext.Array.contains(properties, item.property)) {
				Ext.apply(item, config);
			}
		});
		return filters;
	},
	notifySuccess : function(message, title, keepTime) {
		var tpl = new Ext.XTemplate('<div class="alert alert-success model-dialog fade"><button type="button" class="close" data-dismiss="alert">&times;</button><tpl if="title"><h4>{title}</h4></tpl>{message}</div>');
		var el = tpl.append(Ext.getBody(), {
			title : title,
			message : message
		}, true);
		el.addCls('in');
		if (keepTime === undefined) {
			keepTime = 2000;
		}
		if (keepTime && keepTime > 0) {
			window.setTimeout(function() {
				$("#" + el.id).alert('close');
			}, keepTime);
		}
	},
	// used for convert message.p.d.key to message['p.d.key']
	parseValue : function(obj, keys) {
		// <debug error>
		if (typeof keys !== 'string') {
			Ext.Error.raise("Invalid key, must be a string");
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
	/**
	 * 导出excel，设置grid，url，还有可选的filename和其他参数params
	 * @param  config  {grid:grid,filename:'abc.xlsx',start:,limit:,params:{} }
	 * Lib.exportAsExcel({grid,url,filename,start,limit,params})
	 */
	exportAsExcel : function(config) {
		var grid = config.grid;
		var params = config.params;
		var store = grid.store;
		var url = config.url;
		var start = config.start;
		var limit = config.limit;
		
		if(start && limit){
			if(limit > 5000){
				Ext.Msg.alert("提示", "当前条件下的数据量太大，不适合一次性导出，请修改条件，让总数据量小于5000之后再导出。");
				return;
			}
		}else if(store.getTotalCount() > 5000){
			Ext.Msg.alert("提示", "当前条件下的数据量太大，不适合一次性导出，请修改查询条件，让总数据量小于5000之后再导出。");
			return;
		}
		
		var columns = grid.columns;
		if (Ext.isObject(columns) && columns.items) {
			columns = columns.items;
		}
		var list = [];
		for (var i = 0; i < columns.length; i++) {
			var col = columns[i];
			// handle object column
			var mapping = col.dataIndex;
			var dateFormat = col.format;
			var xtype = col.xtype;
			if (col.column && col.column.dataIndex) {
				mapping = col.dataIndex + '.' + col.column.dataIndex;
				dateFormat = col.column.format;
				xtype = col.column.xtype;
			}

			if (col.hidden == false && col.dataIndex) {
				list.push({
					text : col.text,
					formula : null,
					xtype : xtype,
					format : dateFormat || Ext.Date.defaultFormat,
					dataIndex : mapping
				});
			}
		}

//		if (!store.entity) {
//			Ext.Error.raise('argument need to be an gird with Lib.store.DbStore. ');
//			return;
//		}
		var form = Ext.DomHelper.append(Ext.getBody(), {
			tag : 'form',
			cls : 'x-hidden'
		}, true);
		// 追踪下载状态
		Ext.util.Cookies.clear("downloadStatus");
		Lib.mask('正在生成excel，稍候......');
		var task = Ext.TaskManager.start({
			run : function() {
				var name = Ext.util.Cookies.get('downloadStatus');
				// Lib.log('cookie downloadStatus:' + name);
				if (name != null) {
					Lib.unmask();
					return false;
				}
			},
			interval : 500,
			repeat : 120 * 2
		});
		params = params || {};
		Ext.apply(params, {
			start:start,
			limit:limit,
			filename : config.filename || (store.entity + ".xlsx"),
			filter : store.getProxy().encodeFilters(store.filters.items),
			sort : store.getProxy().encodeSorters(store.sorters.items),
			columns : Ext.encode(list)
		},store.getProxy().extraParams);
		Ext.Ajax.request({
			url : url || Lib.Config.dbExcelPath + "/" + store.entity + ".do",
			form : form,
			isUpload : true,
			params : params,
			success : function(res, opt) {
				if(res.responseText && res.responseText.indexOf('HTTP Status') ==0 && res.responseText.indexOf('HTTP Status 2') != 0 ){
					Lib.unmask();
					Ext.Msg.alert("Failure", "导出失败, 请尝试刷新页面，或者修改参数之后再重试几次。如果依然失败，请联系管理员。");
				}
				// Lib.log("excel success");
			},
			failure : function(res, opt) {
				Lib.unmask();
				Ext.Msg.alert("Failure", "导出失败,通常是网络问题。请尝试刷新页面。如果服务器在线并且刷新之后依然出现这个错误，请联系管理员。");
				// Lib.log("excel failure");
			}
		});
		Ext.removeNode(form);
	}

});

// 每个项目必然有不同的context，而有些组件依赖服务端，需要服务端对于的地址。我们固定吧这些东西存放在一个叫App_Config的变量里面。内容在AppConfig.jsp里面设置
if (typeof App_Config !== 'undefined') {
	Ext.apply(Lib.Config, App_Config);
};
