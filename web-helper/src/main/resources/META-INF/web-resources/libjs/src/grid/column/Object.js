Ext.define('Lib.grid.column.Object', {
	extend : 'Ext.grid.column.Column',
	alias : ['widget.objectcolumn'],
	constructor : function(config) {
		this.callParent(arguments);
	},
	column:{xtype:'gridcolumn',dataIndex:'id',renderer: Ext.identityFn },
	
	// Renderer closure iterates through items creating an <img> element for
	// each and tagging with an identifying
	// class name x-action-col-{n}
	defaultRenderer : function(value, meta) {
		if(!this.column || value == null){
			return "";
		}
		Ext.applyIf(this.column,{
			xtype:'gridcolumn',
			dataIndex:'id'
		});
		var filedValue = null;
		var dataIndex = this.column.dataIndex;
		if(dataIndex){
			filedValue = Lib.parseValue(value,dataIndex);
		}
		if(this.column.renderer || this.column.defaultRenderer){
			var renderer = this.column.renderer || this.column.defaultRenderer;
			return renderer.apply(this,[filedValue,meta]);
		}
		var objectColumn = Ext.widget(this.column);
		if(objectColumn.renderer){
			return objectColumn.renderer.apply(this,[filedValue,meta]);
		}
		return filedValue;
	}

});