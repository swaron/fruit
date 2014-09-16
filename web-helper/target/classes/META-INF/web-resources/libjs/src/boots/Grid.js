/**
 * 把bootstrap的表格变成一个ext组件。
 * bootstrap的表格功能少，但简单。
 */

Ext.define('Lib.boots.Grid', {

    /* Begin Definitions */
    alias: 'widget.boots.grid',
    extend: 'Ext.view.View',

    itemSelector: '.x-dataview-item',
    emptyText: 'No Data available',
    
    //default style
    btnCls:'btn-app',
    columns:[],
    tableCls:'table-bordered table-condensed',
    initComponent : function() {
		var me = this;
		me.tableTpl = Ext.XTemplate.getTpl(this, 'tableTpl');
        me.headerTpl   = Ext.XTemplate.getTpl(this, 'headerTpl');
        me.rowTpl   = Ext.XTemplate.getTpl(this, 'rowTpl');
        me.cellTpl  = Ext.XTemplate.getTpl(this, 'cellTpl');
		this.callParent();
	},
    collectData: function(records, startIndex) {
        return {
            view: this,
            rows: records,
            viewStartIndex: startIndex
        };
    },
    tpl: '{%values.view.tableTpl.applyOut(values, out)%}',
    tableTpl: [
    	'<tpl if="view.title"><div class="panel-heading" style="border: 1px solid rgb(221, 221, 221);border-bottom-width:0px;">{view.title}</div></tpl>',
    	'<table class="table {view.tableCls}">',
            '{% values.view.renderTHead(values, out) %}',
            '{% values.view.renderTFoot(values, out) %}',
            '<tbody id="{view.id}-body">',
            '{%',
                'values.view.renderRows(values.rows, values.viewStartIndex, out);',
            '%}',
            '</tbody>',
        '</table>',
        {
            priority: 0
        }
    ],
    headerTpl: [
        '<thead><tr>',
            '<tpl for="columns">',
                '<th width="{width}" style="{style}">{text}</th>',
            '</tpl>',
        '</tr></thead>'
    ],
    rowTpl: [
        '<tr ',
            'data-boundView="{view.id}" ',
            'data-recordId="{record.internalId}" ',
            'data-recordIndex="{recordIndex}" ',
            'class="x-dataview-item {[values.rowClasses.join(" ")]}" >',
            '<tpl for="columns">' +
                '{%',
                    'parent.view.renderCell(values, parent.record, parent.recordIndex, xindex - 1, out, parent)',
                 '%}',
            '</tpl>',
        '</tr>'
    ],
    cellTpl: [
        '<td class="{tdCls}" {tdAttr} style="text-align:{align};<tpl if="style">{style}</tpl>" >',
                '{value}',
        '</td>'
    ],
    renderRows : function(rows, viewStartIndex, out) {
        var me = this,
        	rowValues = {};
        rowValues.view = this;
        rowValues.columns = me.columns;

        for (var i = 0; i < rows.length; i++, viewStartIndex++) {
        	rowValues.record = rows[i];
        	rowValues.recordIndex = viewStartIndex;
        	rowValues.rowClasses= [];
        	if (me.getRowClass) {
                cls = me.getRowClass(rows[i], viewStartIndex, null, me.dataSource);
                if (cls) {
                    rowValues.rowClasses.push(cls);
                }
            }
			if (out) {
	            me.rowTpl.applyOut(rowValues, out);
	        } else {
	            return me.rowTpl.apply(rowValues);
	        }
        }
    },
	renderCell: function(column, record, recordIndex, columnIndex, out) {
        var me = this,
            cellValues = {},
            classes = [],
            fieldValue = record.get(column.dataIndex),
            cellTpl = me.cellTpl,
            value, clsInsertPoint;

        cellValues.record = record;
        cellValues.column = column;
        cellValues.recordIndex = recordIndex;
        cellValues.columnIndex = columnIndex;
        
        cellValues.align = column.align || 'left';
        cellValues.tdCls = column.tdCls;
        cellValues.style = cellValues.tdAttr = "";
        
        if (column.renderer && column.renderer.call) {
            value = column.renderer.call(column.scope || me, fieldValue, cellValues, record, recordIndex, columnIndex, me.dataSource, me);
        } else {
            value = fieldValue;
        }
        cellValues.value = (value == null || value === '') ? '&#160;' : value;

            
        // On IE8, array[len] = 'foo' is twice as fast as array.push('foo')
        // So keep an insertion point and use assignment to help IE!
        clsInsertPoint = 0;

        if (column.tdCls) {
            classes[clsInsertPoint++] = column.tdCls;
        }
        // Chop back array to only what we've set
        classes.length = clsInsertPoint;

        cellValues.tdCls = classes.join(' ');

        cellTpl.applyOut(cellValues, out);
        
    },
    
	renderTHead: function(values, out) {
		values.columns = this.columns;
		this.headerTpl.applyOut(values,out);
    },
    renderTFoot: function(values, out){
    },
	//添加一个方法来合并单元格 rowspan
	mergeCell:function(columns /*grid.headerCt.getGridColumns()*/,groups){
		
		var view = this;
		var store = view.getStore();
		
		if(!groups && !store.isGrouped()){
			return;
		}
		if(groups && !store.isGrouped()){
			store.remoteGroup = false;
			store.remoteSort = false;
			store.group(groups);
		}
		
		groups = store.groupers;
		
		var rows = view.getNodes();
		var records = view.getRecords(rows);
		if(rows.length > 0){
			var firstRow = rows[0];
			var firstTds = Ext.query('>td',firstRow);
			for(var i=0;i< firstTds.length; i++){
				//开始处理每一列
				var dataIndex = columns[i].dataIndex;
				if(dataIndex && groups.containsKey(dataIndex)){
					//对第i列，处理merge rows。因为rowspan需要设置在第一行，所以倒序来处理. 从倒数第2行开始
					var lastBoundaryIndex = rows.length;
					for(var j=rows.length-1; j > 0 ; j--){
						var row= rows[j];
						var rec = records[j];
						var groupStr = groups.getByKey(dataIndex).getGroupString(rec);
						var prevGroupStr = groups.getByKey(dataIndex).getGroupString(records[j-1]);
						if(groupStr != prevGroupStr || row.boundary > 0){
							row.boundary =  lastBoundaryIndex - j;
							lastBoundaryIndex = j;
						}
					}
					rows[0].boundary = lastBoundaryIndex - 0;
				}else{
					//这列不在groups里面，继续下一列的处理
					continue;
				}
				//边界都已经标记上了，根据边界来添加 rowspan和display:none
				for(var k=0;k<rows.length;k++){
					var row = rows[k];
					var td = Ext.query('>td:nth-child('+(i+1) +')',row)[0];
					if(row.boundary > 0){
						td.setAttribute('rowspan',row.boundary);
					}else{
						td.style.display = 'none';
					}
				}
				
			}
		
		}else{
			return;
		}
		
	}
});