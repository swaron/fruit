Ext.define('Lib.store.DbStore', {
	requires : ['Ext.data.Store', 'Lib.service.EnumCodeService', 'Ext.data.Model', 'Ext.data.reader.Json'],
	extend : 'Ext.data.Store',
	alias:'widget.dbstore',
	remoteSort : true,
	remoteFilter : true,
	autoLoad : true,
	//becareful with autoSync, it will sync with server immediately when records are added to store
	autoSync : false,
	pageSize : 10,

	//model is to be replaced by meta data from server
//	model : 'Lib.model.DbModel',
	fields:[],
	proxy : {
		type : 'rest',
		format : 'json',
		extraParams : {
			meta : true
		},
		//url will be set in constructor of this store
//		url : Lib.Config.dbStorePath...,
		reader : {
			type : 'json',
			root : 'records'
		},
        writer: {
            type: 'json'
        }
	},
	entity : null,
	constructor: function(config) {
		if(config && config.entity && !config.storeId){
			if(Ext.getStore(config.entity) == null){
				config.storeId = config.entity;
			}else{
				config.storeId = config.entity + '-' +Ext.id();
			}
		}
        this.callParent(arguments);
        this.getProxy().url = Lib.Config.dbStorePath + '/' + this.entity;
 	},
    onDestroyRecords: function(records, operation, success){
        if (!success) {
        	//reload if we failed on remove, so the grid will show all records again.
			Ext.defer(this.reload, 1, this);
        }
        this.callParent(arguments);
    }
});
