Ext.define('Lib.grid.column.Dict', {
	extend : 'Ext.grid.column.Column',
	requires:'Lib.service.EnumCodeService',
	alias : ['widget.dictcolumn'],
	table : null,
	column : null,
	hasQtip : true,
	defaultRenderer : function(dictCode) {
		var dictValue;
		if (this.table && this.column) {
			return Lib.EnumCode.getName(this.table,this.column, dictCode);
		}else{
			Ext.Error.raise("table and column is required by dictcolumn.");
		}
	}
});