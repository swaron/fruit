Ext.define('Lib.form.field.Month', {
	alias : 'widget.monthfield',
	extend : 'Ext.form.field.Date',
	requires : ['Ext.picker.Month'],
	editable : false,
	format : 'm/Y',
	initComponent : function() {
		var me = this;
		clearTime = Ext.Date.clearTime;
		me.callParent();
		me.data = me.data ? clearTime(me.data, true) : clearTime(new Date());
	},
	
	createPicker : function() {
		var me = this;
		return new Ext.picker.Month({
			renderTo : document.body,
			floating : true,
			hidden : true,
			focusOnToFront : true,
			minHeight : 230,
			minWidth : 220,
			listeners : {
				scope : me,
				cancelclick : me.onCancelClick,
				okclick : me.onOkClick,
				yeardblclick : me.onOkClick,
				monthdblclick : me.onOkClick,
				beforerender : me.onBeforerender
			}
		});
	},
	
    safeParse : function(value, format) {
        var me = this,
            utilDate = Ext.Date,
            result = null,
            strict = me.useStrict,
            parsedDate;

        if (utilDate.formatContainsHourInfo(format)) {
            // if parse format contains hour information, no DST adjustment is necessary
            result = utilDate.parse(value, format, strict);
            result = Ext.Date.getFirstDateOfMonth(result);
        } else {
            // set time to 12 noon, then clear the time
            parsedDate = utilDate.parse(value + ' ' + me.initTime, format + ' ' + me.initTimeFormat, strict);
            if (parsedDate) {
                result = utilDate.clearTime(parsedDate);
                result = Ext.Date.getFirstDateOfMonth(result);
            }
        }
        return result;
    },

	onBeforerender : function(picker, opts) {
		var me = this;
		picker.setValue(me.data);
	},
	/**
	 * Respond to an ok click on the month picker
	 * 
	 * @private
	 */
	onOkClick : function(picker, value) {
		var me = this, month = value[0], year = value[1], date = new Date(year, month, 1);
		me.data = date;
		picker.setValue(date);
//		var displayValue = me.formatDate(date);
		me.setValue(date);
		me.hideMonthPicker(picker);
	},

	/**
	 * Respond to a cancel click on the month picker
	 * 
	 * @private
	 */
	onCancelClick : function(picker) {
		// update the selected value, also triggers a focus
		var me = this;
		picker.setValue(me.data);
		me.hideMonthPicker(picker);
	},

	/**
	 * Run any animation required to hide/show the month picker.
	 * 
	 * @private
	 * @param {Boolean}
	 *            isHide True if it's a hide operation
	 */
	runAnimation : function(monthPicker, isHide) {
		var picker = monthPicker, options = {
			duration : 200,
			callback : function() {
				if (isHide) {
					picker.hide();
				} else {
					picker.show();
				}
			}
		};

		if (isHide) {
			picker.el.slideOut('t', options);
		} else {
			picker.el.slideIn('t', options);
		}
	},

	hideMonthPicker : function(monthPicker) {
		var me = this;
		me.runAnimation(monthPicker, true);
		me.isExpanded = false;
	}

});
