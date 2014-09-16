Ext.define('Lib.grid.column.Action', {
	extend : 'Ext.grid.column.Action',
	alias : ['widget.lib.actioncolumn'],
	config : {
		text : null
	},
	actionIdRe : new RegExp('action-column-item-(\\d+)'),
	constructor : function(config) {
		this.callParent(arguments);
	},

	// Renderer closure iterates through items creating an <img> element for
	// each and tagging with an identifying
	// class name x-action-col-{n}
	defaultRenderer : function(value, meta, record, rowIdx, colIdx, store, view) {
		var me = this, prefix = Ext.baseCSSPrefix, scope = me.origScope || me, items = me.items, len = items.length, i = 0, item;

		// Allow a configured renderer to create initial value (And set the
		// other values in the "metadata" argument!)
		var v = Ext.isFunction(me.origRenderer) ? me.origRenderer.apply(scope, arguments) || '' : '';

		meta.tdCls += ' ' + Ext.baseCSSPrefix + 'action-col-cell';
		for (; i < len; i++) {
			item = items[i];

			// Only process the item action setup once.
			if (!item.hasActionConfiguration) {

				// Apply our documented default to all items
				item.stopSelection = me.stopSelection;
				item.disable = Ext.Function.bind(me.disableAction, me, [i], 0);
				item.enable = Ext.Function.bind(me.enableAction, me, [i], 0);
				item.hasActionConfiguration = true;
			}
			if (Ext.isFunction(item.isHidden)) {
				var isHidden = item.isHidden.call(scope, item.text, value, meta, record, rowIdx, colIdx, store, view);
				if(isHidden){
					continue;
				}
			}
			
			v += '<a href="javascript:void(0);" class="action-column-item-'
					+ String(i)
					+ ' '
					+ (Ext.isFunction(item.getClass)
							? item.getClass.apply(item.scope || scope, arguments)
							: (item.iconCls || me.iconCls || '')) + '">';
			if (item.icon) {
				v += '<img alt="'
						+ (item.altText || me.altText)
						+ '" src="'
						+ (item.icon || Ext.BLANK_IMAGE_URL)
						+ '" class="'
						+ prefix
						+ 'action-col-icon '
						+ prefix
						+ 'action-column-span-item-'
						+ String(i)
						+ ' '
						+ (item.disabled ? prefix + 'item-disabled' : ' ')
						+ ' '
						+ (Ext.isFunction(item.getClass)
								? item.getClass.apply(item.scope || scope, arguments)
								: (item.iconCls || me.iconCls || '')) + '"'
						+ ((item.tooltip) ? ' data-qtip="' + item.tooltip + '"' : '') + ' />';
			}
			if (Ext.isFunction(item.textRenderer)) {
				v += item.textRenderer.call(scope, item.text, value, meta, record, rowIdx, colIdx, store, view);
			} else {
				if (!Ext.isEmpty(item.text)) {
					v += item.text;
				}
			}

			v += "</a>"
		}
		return v;
	}

});