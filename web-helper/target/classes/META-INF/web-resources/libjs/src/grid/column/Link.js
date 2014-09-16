Ext.define('Lib.grid.column.Link', {
	extend : 'Ext.grid.column.Column',
	alias : ['widget.linkcolumn'],
	constructor : function(config) {
		this.callParent(arguments);
	},
	disabled : false,
	stopSelection : true,
	defaultRenderer : function(value, meta) {
		var me = this;
		var html = "";
		if (me.iconCls) {
			html += '<i class="' + me.iconCls + '"></i> ';
		}
		html += '<a href="javascript:void(0);" >' + value + '</a>';
		return html;
	},
	/**
	 * @private Process and refire events routed from the GridView's processEvent method. Also fires any configured
	 *          click handlers. By default, cancels the mousedown event to prevent selection. Returns the event
	 *          handler's status to allow canceling of GridView's bubbling process.
	 */
	processEvent : function(type, view, cell, recordIndex, cellIndex, e, record, row) {
		var me = this, target = e.getTarget(), fn, key = type == 'keydown' && e.getKey(), disabled;

		// If the target was not within a cell (ie it's a keydown event from the View), then
		// rely on the selection data injected by View.processUIEvent to grab the
		// first action icon from the selected cell.
		if (key && !Ext.fly(target).findParent(view.getCellSelector())) {
			target = Ext.fly(cell).down('a', true);
			// NOTE: The statement below tests the truthiness of an assignment.
		}
		
		if (target && target.tagName == 'A') {
			if (me.disabled !== true) {
				if (type == 'click' || (key == e.ENTER || key == e.SPACE)) {
					fn = me.handler;
					if (fn) {
						fn.call(me.scope || me, view, recordIndex, cellIndex, e, record, row);
					}
				} else if (type == 'mousedown' && me.stopSelection !== false) {
					return false;
				}
			}
		}

		return me.callParent(arguments);
	}
});