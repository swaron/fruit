/**
 * ## Example Code
 * 
 * Lib.EnumCode.getName('table','column', '1'); //return table->column->name <br>
 * Lib.EnumCode.getCode('table','column', 'name'); // return table->column->code <br>
 * Lib.EnumCode.converter('table','column'); //return a function which will convert code to name <br>
 * Lib.EnumCode.store('table','column'); // return a store which contains all data of table.column
*/


Ext.define('Lib.service.BasicEnumCodeService', {
	alternateClassName: 'Lib.BasicEnumCode',
	singleton:true,
    requires : ['Ext.data.reader.Json', 'Ext.data.Store', 'Lib.model.DbCode', 'Ext.data.proxy.Ajax', 'Ext.data.proxy.LocalStorage',
			'Ext.data.proxy.Rest',  'Ext.data.Request', 'Ext.data.Batch'],
	url : Lib.Config.enumCodeUrl,
	localStore : null,
	remoteStore : null,
	nameCache : {},
	mixins : {
		observable : 'Ext.util.Observable'
	},
	initStore : function() {
		var localStore = null;
		try {
			localStore = Ext.create('Ext.data.Store', {
				model : 'Lib.model.DbCode',
				autoLoad : false,
				proxy : {
					type : 'localstorage',
					id : 'app-ecode'
				}
			});
			//proxy will be instantiated in constructor of Ext.data.AbstractStore, will test the availability of localstorage.
		} catch (e) {
			localStore = null;
			Lib.log('failed to create localstorage, fallback to remote store.', e);
		}

		if (localStore) {
			var reload;
			try {
				localStore.load();
				var version = localStore.findRecord('column', '_store_version');
				reload = (version == null) || (version.get('code') != Lib.Config.version);
			} catch (e) {
				// fix if data corrupted
				reload = true;
				Lib.log('unable to read from local store, try to clear local store', e);
			}
			if (reload) {
				Lib.log('not latest version, reload codes. latest version: ' + Lib.Config.version);
				this.remoteStore = Ext.create('Ext.data.Store', {
					model : 'Lib.model.DbCode',
					proxy : {
						type : 'ajax',
						url : this.url,
//						actionMethods : {
//							read : "GET"
//						},
						reader : {
							type : 'json'
						}
					},
					autoLoad : false
				});
//				var oldAsync = Ext.Ajax.async;
//				Ext.Ajax.async = false;
				this.remoteStore.load({
					scope : this,
					callback : function(records, operation, success) {
						if(!success){
							return;
						}
						localStore.getProxy().clear();
						localStore.load();
						Lib.log('load code records into local storage.');
						Ext.each(records, function(record, index) {
							delete record.data[record.idProperty];
							localStore.add(record.data);
						});
						localStore.add({
							column : '_store_version',
							code : Lib.Config.version,
							name : Lib.Config.version
						});
						localStore.sync();
					}
				});
//				Ext.Ajax.async = oldAsync;
			}
		} else {
			this.remoteStore = Ext.create('Ext.data.Store', {
				model : 'Lib.model.DbCode',
				proxy : {
					type : 'ajax',
//					actionMethods : {
//						read : "GET"
//					},
					url : this.url,
					reader : {
						type : 'json'
					}
				},
				autoLoad : false
			});
			this.remoteStore.load();
		}
		this.localStore = localStore;
		this.codeStore = localStore || this.remoteStore;
	},
	findName:function(table, column, code, defaultValue){
		if (code == null || code == '') {
			return defaultValue === undefined ? '': defaultValue;
		}
		var store = this.codeStore;
		var index = store.findBy(function(model) {
			return model.get('table') == table && model.get('column') == column && model.get('code') == code;
		});
		if (index == -1) {
			var msg = Ext.String.format('unable to get magiccode definition, table:{0},column:{1},code:{2}', table, column,code);
			Lib.log(msg);
			Lib.log('records count in store:' + store.getCount() + '. store info:', store);
		} else {
			return store.getAt(index).get('name');
		}
	},
	getName : function(table, column, code, defaultValue) {
		var key = table + '.' + column + '.' + code;
		if (this.nameCache[key] === undefined) {
			var name = this.findName(table, column, code, defaultValue);
            this.nameCache[key] = name;
        }
        return this.nameCache[key];
	},
	getCode : function(table, column, name) {
		if (name == null || name == '') {
			return null;
		}
		var store = this.codeStore;
		var index = store.findBy(function(model) {
			return model.get('table') == table && model.get('column') == column && model.get('name') == name;
		});
		if (index == -1) {
			var msg = Ext.String.format('unable to get magiccode definition, table:{0},column:{1},name:{2}', table, column,name);
			Lib.log(msg);
			Lib.log('records count in store:' + store.getCount() + '. store info:', store);
		} else {
			return store.getAt(index).get('code');
		}
	},
	store : function(table, column , noBlank) {
		var columnStore = Ext.create('Ext.data.Store', {
			model : 'Lib.model.DbCode',
			sorters : [{
				property : 'order',
				direction : 'ASC'
			}]
		});
		var items = this.codeStore.queryBy(function(m) {
			return m.get('table') == table && m.get('column') == column;
		});
		if(noBlank !== true){
			columnStore.add({
				table:table,
				column:column,
				code: '',
				name_en:'全部',
				name_zh:'全部',
				order:0
			});
		}
		columnStore.add(items.getRange());
		columnStore.sort();
		return columnStore;
	},
	converter : function(table, column, defaultValue) {
		var service = this;
		var fn = function(code) {
			return service.getName(table, column, code, defaultValue);
		}
		return fn;
	},
	test : function() {
		Lib.log('test getname with code 1 in syscode: ', this.getName('SYSCODE', 1));
		Lib.log('test getcode with 个人  in account_type: ', this.getCode('ACCOUNT_TYPE', '个人'));
		Lib.log('test store in result_code,count: ', this.store('RESULT_CODE').getCount());
		Lib.log('test converter in SYSCODE with 1 ', this.converter('SYSCODE')('1'));
	},
	constructor : function() {
		this.initStore();
	}
});
