/**
 * @class Ext.form.field.TreePicker
 * @extends Ext.form.field.Picker
 * 
 * 
 * 
 * 
 * A Picker field that contains a tree panel on its popup, enabling selection of tree nodes. the displayField and
 * valueField should be a property in model.model can be changed.
 */
Ext.define('Lib.picker.TreePicker', {
	extend : 'Ext.form.field.Picker',
	alias : 'widget.treepicker',

	triggerCls : Ext.baseCSSPrefix + 'form-arrow-trigger',

	lastSelectRecord : null,

	forceSelection : false,

	allselectable : true,

	enableKeyEvents : true,
	
	/**
	 * Allow node with children can be selected
	 */
	allowParentNodeSelected: true,

	config : {
		/**
		 * @cfg {Ext.data.TreeStore} store A tree store that the tree picker will be bound to
		 */
		store : null,

		/**
		 * @cfg {String} displayField The field inside the model that will be used as the node's text. Defaults to the
		 *      default value of {@link Ext.tree.Panel}'s `displayField` configuration.
		 */
		displayField : null,

		valueField : 'id',

		/**
		 * @cfg {Array} columns An optional array of columns for multi-column trees
		 */
		columns : null,

		/**
		 * @cfg {Boolean} selectOnTab Whether the Tab key should select the currently highlighted item. Defaults to
		 *      `true`.
		 */
		selectOnTab : true,

		/**
		 * @cfg {Number} maxPickerHeight The maximum height of the tree dropdown. Defaults to 300.
		 */
		maxPickerHeight : 300,

		/**
		 * @cfg {Number} minPickerHeight The minimum height of the tree dropdown. Defaults to 100.
		 */
		minPickerHeight : 100

	},

	editable : false,

	getLastSelectRecord : function() {
		return this.lastSelectRecord;
	},

	initComponent : function() {
		var me = this;
		me.store.load();
		me.callParent(arguments);
		this.addEvents(
				/**
				 * @event select Fires when a tree node is selected
				 * @param {Ext.ux.TreePicker}
				 *            picker This tree picker
				 * @param {Ext.data.Model}
				 *            record The selected record
				 */
				'select');

		me.store.on('load', me.onLoad, me);
		me.on('keyup', me.onKeyup, me);
	},
	onKeyup : function(field, e, eOpts) {
		var me = field;
		var value = me.value;
		var rawValue = me.getRawValue();
		if (Ext.isEmpty(rawValue)) {
			me.setValue(null);
			return;
		}
		var newRecord = this.findChildByDisplayvalue(rawValue);
		if (newRecord != null) {
			me.setValue(newRecord.get(me.valueField));
			me.fireEvent('select', me, newRecord)
			return;
		} else {
			me.setValue(rawValue);
		}
	},

	/**
	 * Creates and returns the tree panel to be used as this field's picker.
	 * 
	 * @private
	 */
	createPicker : function() {
		var me = this;
		var picker = Ext.create('Ext.tree.Panel', {
			rootVisible : false,
			store : me.store,
			floating : true,
			hidden : true,
			displayField : me.displayField,
			columns : me.columns,
			maxHeight : me.maxPickerHeight,
			shadow : false,
			manageHeight : true,
			listeners : {
				itemclick : Ext.bind(me.onItemClick, me)
			},
			viewConfig : {
				height:undefined,
				listeners : {
					render : function(view) {
						view.getEl().on('keypress', me.onPickerKeypress, me);
					}
				}
			}
		}), view = picker.getView();

		view.on('render', me.setPickerViewStyles, me);

		if (Ext.isIE9 && Ext.isStrict) {
			// In IE9 strict mode, the tree view grows by the height of the
			// horizontal scroll bar when the items are highlighted or
			// unhighlighted.
			// Also when items are collapsed or expanded the height of the view
			// is off. Forcing a repaint fixes the problem.
			view.on('highlightitem', me.repaintPickerView, me);
			view.on('unhighlightitem', me.repaintPickerView, me);
			view.on('afteritemexpand', me.repaintPickerView, me);
			view.on('afteritemcollapse', me.repaintPickerView, me);
		}
		return picker;
	},

	/**
	 * Sets min/max height styles on the tree picker's view element after it is rendered.
	 * 
	 * @param {Ext.tree.View}
	 *            view
	 * @private
	 */
	setPickerViewStyles : function(view) {
		view.getEl().setStyle({
			'min-height' : this.minPickerHeight + 'px',
			'max-height' : this.maxPickerHeight + 'px'
		});
	},

	/**
	 * repaints the tree view
	 */
	repaintPickerView : function() {
		var style = this.picker.getView().getEl().dom.style;

		// can't use Element.repaint because it contains a setTimeout, which
		// results in a flicker effect
		style.display = style.display;
	},

	/**
	 * Aligns the picker to the input element
	 * 
	 * @private
	 */
	alignPicker : function() {
		var me = this, picker;

		if (me.isExpanded) {
			picker = me.getPicker();
			if (me.matchFieldWidth) {
				// Auto the height (it will be constrained by max height)
				picker.setWidth(me.bodyEl.getWidth());
			}
			if (picker.isFloating()) {
				me.doAlign();
			}
		}
	},

	/**
	 * Handles a click even on a tree node
	 * 
	 * @private
	 * @param {Ext.tree.View}
	 *            view
	 * @param {Ext.data.Model}
	 *            record
	 * @param {HTMLElement}
	 *            node
	 * @param {Number}
	 *            rowIndex
	 * @param {Ext.EventObject}
	 *            e
	 */
	onItemClick : function(view, record, node, rowIndex, e) {
		this.selectItem(record);
	},

	/**
	 * Handles a keypress event on the picker element
	 * 
	 * @private
	 * @param {Ext.EventObject}
	 *            e
	 * @param {HTMLElement}
	 *            el
	 */
	onPickerKeypress : function(e, el) {
		var key = e.getKey();

		if (key === e.ENTER || (key === e.TAB && this.selectOnTab)) {
			this.selectItem(this.picker.getSelectionModel().getSelection()[0]);
		}
	},

	/**
	 * Changes the selection to a given record and closes the picker
	 * 
	 * @private
	 * @param {Ext.data.Model}
	 *            record
	 */
	selectItem : function(record) {
		var me = this;
		var hasChild = record.childNodes.length > 0 ? true: false;
		if( !this.allowParentNodeSelected && hasChild ){
			me.picker.expandNode(record,true);
			return;
		}
		if (me.allselectable || record.get('selectable')) {
			me.setValue(record.get(this.valueField));
			me.collapse();
			me.inputEl.focus();
			me.fireEvent('select', me, record)
		}
	},

	/**
	 * Runs when the picker is expanded. Selects the appropriate tree node based on the value of the input element, and
	 * focuses the picker so that keyboard navigation will work.
	 * 
	 * @private
	 */
	onExpand : function() {
		var me = this, picker = me.picker, store = picker.store, value = me.value;
		if (value) {
			var node = me.findChild(value);
			if (node) {
				// App.log(node.getPath());
				picker.expandPath(node.getPath());
				picker.selectPath(node.getPath());
			}
		} else {
			picker.getSelectionModel().select(store.getRootNode());
		}

		Ext.defer(function() {
			picker.getView().focus();
		}, 1);
	},

	/**
	 * Sets the specified value into the field
	 * 
	 * @param {Mixed}
	 *            value
	 * @return {Ext.ux.TreePicker} this
	 */
	setValue : function(value) {
		var me = this, record;

		me.value = value;

		if (me.store.loading) {
			// Called while the Store is loading. Ensure it is processed by the
			// onLoad method.
			return me;
		}

		// try to find a record in the store that matches the value
		record = value ? me.findChild(value) : null;

		me.lastSelectRecord = record;

		if (me.forceSelection) {
			// set the raw value to the record's display field if a record was found
			if (record) {
				me.setRawValue(record.get(this.displayField));
			} else {
				me.setRawValue('');
				me.value = '';
			}
		} else {
			me.setRawValue(record ? record.get(this.displayField) : value);
		}

		return me;
	},

	/**
	 * Returns the current data value of the field (the idProperty of the record)
	 * 
	 * @return {Number}
	 */
	getValue : function() {
		return this.value;
	},
	getSubmitValue : function(){
		return this.value;
	},

	/**
	 * Handles the store's load event.
	 * 
	 * @private
	 */
	onLoad : function() {
		var value = this.value;

		if (value) {
			this.setValue(value);
		}
	},
	findChild : function(value) {
		var root = this.store.getRootNode();
		return root.findChild(this.valueField, value, true)
	},
	findChildByDisplayvalue : function(value) {
		var root = this.store.getRootNode();
		return root.findChild(this.displayField, value, true)
	}
});
