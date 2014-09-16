Ext.define('Lib.store.ArrayDbStore', {
	extend : 'Ext.data.ArrayStore',
	alias : 'widget.arraydbstore',
	remoteSort : true,
	remoteFilter : true,
	autoLoad : false,
	// becareful with autoSync, it will sync with server immediately when records are added to store
	autoSync : false,
	pageSize : 15,

	entity : null,
	fields : [],
	constructor : function(config) {
		if (config && config.entity && !config.storeId) {
			config.storeId = config.entity + Ext.id();
		}
		config = Ext.apply({
            proxy: {
                type : 'ajax',
				format : 'json',
	            reader:'array'
            }
        }, config);
		var fields = config.fields;
		this.callParent(arguments);
		if(!fields){
			fields = this.model.getFields();
		}
		var values = [];
		for (var i = 0; i < fields.length; i++) {
			var field = fields[i];
			if (Ext.isString(field)) {
	            field = {name: field};
	        }
			values.push(field.mapping || field.name);
		}
		var proxy = this.getProxy();
		proxy.extraParams = Ext.applyIf({
			fields : values
		}, proxy.extraParams)
		proxy.url = Lib.Config.arrayDbStorePath + '/' + this.entity + '.json';
	}
});
