/**
 * this proxy will retrieve ALL records from server,
 * and perform paging on client side. it is convenient for not large tables.
 * 
 * ## Example Code
 * 
	var store = Ext.create('Ext.data.Store', {
		model : 'app.model.RiskTrade',
		autoLoad : true,
		pageSize : 11,
		proxy : {
			type : 'localpagingajax',
			url : Lib.url('/risk/all/riskTrade.json'),
			reader : 'json',
			extraParams : {
				riskIds : []
			}
		}
	});
*/
Ext.define('Lib.data.LocalPagingAjaxProxy', {
    extend: 'Ext.ux.data.PagingMemoryProxy',
    mixins:{
    	ajaxProxy: 'Ext.data.proxy.Ajax'
    },
    alias: 'proxy.localpagingajax',
    isSynchronous:false,
    constructor	: function(options){
		this.mixins.ajaxProxy.constructor(options); //constructors of the mixins
		this.callParent(arguments);
	},
    read : function(operation, callback, scope){
    	if(!Ext.isEmpty(this.data)){
	    	return this.callParent(arguments);
    	}else{
    		var me = this;
    		var args = arguments;
    		var sequence = function(operation){
    			if(operation.wasSuccessful()){
    				me.data = operation.response;
    			}
    			//call read in Ext.ux.data.PagingMemoryProxy 
    			return me.superclass.read.apply(me, args);
    		};
    		return this.mixins.ajaxProxy.read(operation, sequence, scope);
    	}
    }
});

