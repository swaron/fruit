/**
 * 提供一个简单的显示数据库表的grid
 * 
 * <pre>
 * example 1,最简单的方式,指定Jpa的entity名字，指定要排除的列（@id和@version列会被自动排除,要显示这2列要通过columns属性明确指定，看其他2个例子）:
 * Ext.widget('dbgrid', {
 * 	entity : 'secUser',
 * 	renderTo : 'dbgrid-example1'
 * });
 * example 2:
 * Ext.widget('dbgrid', {
 * 	 entity : 'secUser',
 * 	 columns : [{
 *     text:'Header',
 *     dataIndex:'column_name',
 * 	   xtype : 'datecolumn',
 * 	   format : 'Y-m-d',
 * 	   field : {
 * 	     xtype : 'datefield',
 * 	     format : 'Y-m-d'
 * 	   }
 *   },{
 *     text:'Header',
 *     dataIndex:'column_name'
 *   }],
 * 	 title : '数据库表展现测试2',
 * 	 renderTo : 'dbgrid'
 * });
 * example 3：
 * Ext.widget('grid', {
 *   store:Ext.getStore('entityName');
 * 	 columns : [{
 *     text:'Header',
 *     dataIndex:'column_name',
 * 	   xtype : 'datecolumn',
 * 	   format : 'Y-m-d',
 * 	   field : {
 * 	     xtype : 'datefield',
 * 	     format : 'Y-m-d'
 * 	   }
 *   },{
 *     text:'Header',
 *     dataIndex:'column_name'
 *   }],
 * 	 title : '数据库表展现测试2',
 * 	 renderTo : 'dbgrid'
 * });
 * </pre>
 */
Ext.define('Lib.grid.DbGridPanel', {
	requires : ['Lib.service.EnumCodeService', 'Lib.store.DbStore','Ext.grid.column.Date', 'Lib.grid.column.Object', 'Ext.data.Store', 'Ext.form.field.ComboBox',
			'Ext.form.field.Date', 'Ext.form.field.Checkbox', 'Ext.toolbar.Paging'],

	extend : 'Ext.grid.Panel',
	alias : ['widget.dbgridpanel', 'widget.dbgrid'],
	
	config:{
		entity:null,
		excludeFileds:null
	},
	columns:[],
	initStore:function(){
		if(this.entity && this.store){
			Ext.Error.raise('Only one of entity or store can be specified. entity is used to retrieve a store.');
			return;
		}
		if(this.entity){
			var me = this;
			this.store = Ext.create('Lib.store.DbStore',{
				entity : me.entity
			});
		}
		if(this.columns == false){
			this.mon(this.store,'metachange',function(store, meta){
				me.reconfigure(store, meta.columns);
			});
		}
	},
	initComponent : function() {
		this.initStore();
		this.bbar = Ext.create('Ext.PagingToolbar', {
			store : this.getStore(),
			displayInfo : true
		});
		this.callParent(arguments);
	}
});
