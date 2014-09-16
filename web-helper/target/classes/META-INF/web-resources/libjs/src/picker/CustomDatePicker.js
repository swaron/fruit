Ext.define('Lib.picker.CustomDatePicker', {
    extend: 'Ext.picker.Date',//继承于 Ext.picker.Date
    alias: 'widget.customdatepicker',//添加xtype dateptimeicker
    cancelTxt:'取消',
    cancelTxtTip:'取消',

    renderTpl: [
        '<div id="{id}-innerEl" role="grid">',
            '<div role="presentation" class="{baseCls}-header">',
                '<a id="{id}-prevEl" class="{baseCls}-prev {baseCls}-arrow" href="#" role="button" title="{prevText}" hidefocus="on" ></a>',
                '<div class="{baseCls}-month" id="{id}-middleBtnEl">{%this.renderMonthBtn(values, out)%}</div>',
                '<a id="{id}-nextEl" class="{baseCls}-next {baseCls}-arrow" href="#" role="button" title="{nextText}" hidefocus="on" ></a>',
            '</div>',
            '<table id="{id}-eventEl" class="{baseCls}-inner" cellspacing="0" role="presentation">',
                '<thead role="presentation"><tr role="presentation">',
                    '<tpl for="dayNames">',
                        '<th role="columnheader" class="{parent.baseCls}-column-header" title="{.}">',
                            '<div class="{parent.baseCls}-column-header-inner">{.:this.firstInitial}</div>',
                        '</th>',
                    '</tpl>',
                '</tr></thead>',
                '<tbody role="presentation"><tr role="presentation">',
                    '<tpl for="days">',
                        '{#:this.isEndOfWeek}',
                        '<td role="gridcell" id="{[Ext.id()]}">',
                           '<a role="presentation" hidefocus="on" class="{parent.baseCls}-date" href="#"></a>',
                        '</td>',
                    '</tpl>',
                '</tr></tbody>',
            '</table>',
            '<tpl if="showToday">',
                //添加一个确认按钮渲染
                '<div id="{id}-footerEl" role="presentation" class="{baseCls}-footer">{%this.renderTodayBtn(values, out)%}{%this.renderCancelBtn(values, out)%}</div>',
            '</tpl>',
        '</div>',
        {
            firstInitial: function(value) {
                return Ext.picker.Date.prototype.getDayInitial(value);
            },
            isEndOfWeek: function(value) {
                // convert from 1 based index to 0 based
                // by decrementing value once.
                value--;
                var end = value % 7 === 0 && value !== 0;
                return end ? '</tr><tr role="row">' : '';
            },
            renderTodayBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.todayBtn.getRenderTree(), out);
            },
            renderMonthBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.monthBtn.getRenderTree(), out);
            },
            renderCancelBtn: function(values, out) {
                Ext.DomHelper.generateMarkup(values.$comp.cancelBtn.getRenderTree(), out);
            }
        }
    ],

    beforeRender: function () {
        var me = this,_$Number=Ext.form.field.Number;
        //在组件渲染之前，将自定义添加的时、分、秒和确认按钮进行初始化
        //组件宽度可能需要调整下，根据使用的theme不同，宽度需要调整
       
        me.cancelBtn = new Ext.button.Button({
            ownerCt: me,
            ownerLayout: me.getComponentLayout(),
            text: me.cancelTxt,
            tooltip: me.cancelTxtTip,
            tooltipType:'title',
            handler:me.cancelHandler,//确认按钮的事件委托
            scope: me
        });
        me.callParent();
    },
    
    finishRenderChildren: function () {
        var me = this;
        //组件渲染完成后，需要调用子元素的finishRender，从而获得事件绑定
        me.cancelBtn.finishRender();
        me.callParent();
    },

    /**
     * 确认 按钮触发的调用
     */
    cancelHandler : function(){
        var me = this;
        me.hide();
        return me;
    },

    // @private
    // @inheritdoc
    beforeDestroy : function() {
        var me = this;

        if (me.rendered) {
            //销毁组件时，也需要销毁自定义的控件
            Ext.destroy(
                me.cancelBtn
            );
        }
        me.callParent();
    }
},
function() {
    var proto = this.prototype,
        date = Ext.Date;

    proto.monthNames = date.monthNames;
    proto.dayNames   = date.dayNames;
    proto.format     = date.defaultFormat;
});
