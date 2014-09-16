Ext.define('Lib.form.field.DateTime', {
    extend:'Ext.form.field.Date',
    alias: 'widget.datetimefield',
    requires: ['Lib.picker.DateTime'],

    /**
     * @cfg {String} format
     * The default date format string which can be overriden for localization support. The format must be valid
     * according to {@link Ext.Date#parse}.
     */
    format : "Y-m-d H:i:s",
 
    /**
     * @cfg {String} altFormats
     * Multiple date formats separated by "|" to try when parsing a user input value and it does not match the defined
     * format.
     */
    altFormats : "Y-m-d H:i:s",
    /**
     * default current date
     */
    initialValue:new Date(),
    createPicker: function() {
        var me = this,
            format = Ext.String.format;

        //修改picker为自定义picker
        return Ext.widget('dateptimeicker',{
            pickerField: me,
            ownerCt: me.ownerCt,
            renderTo: document.body,
            floating: true,
            hidden: true,
            focusOnShow: true,
            minDate: me.minValue,
            maxDate: me.maxValue,
            disabledDatesRE: me.disabledDatesRE,
            disabledDatesText: me.disabledDatesText,
            disabledDays: me.disabledDays,
            disabledDaysText: me.disabledDaysText,
            format: me.format,
            showToday: me.showToday,
            startDay: me.startDay,
            minText: format(me.minText, me.formatDate(me.minValue)),
            maxText: format(me.maxText, me.formatDate(me.maxValue)),
            listeners: {
                scope: me,
                select: me.onSelect
            },
            keyNavConfig: {
                esc: function() {
                    me.collapse();
                }
            }
        });
    },

    /**
     * @private
     */
    onExpand: function() {
        var value = this.getValue();
    
        //多传一个参数，从而避免时分秒被忽略。
        this.picker.setValue(Ext.isDate(value) ? value : this.initialValue, true);
    }
});
