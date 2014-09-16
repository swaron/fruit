Ext.define('Lib.boots.PillNav', {
	alias : 'widget.boots.pillnav',
	extend : 'Ext.Component',
	autoEl : 'ul',
	componentCls : 'nav nav-pills',
	// cls:'nav-pills-sm',
	baseCls : 'x-boots',
	config : {
		activeItem : 0
	},
	tpl : '<tpl for="items"><li class="{liCls}"><a href="{href}" data-index="{#}" ><i class="{iconCls}"> </i>{text}</a></li></tpl>',
//	data:{
//		items : [{
//			active : true,
//			href : null,
//			iconCls : 'icon-ok',
//			text : '名称'
//		}]
//	},
	onRender: function() {
        var me = this;
		me.mon(me.el, 'click', function(e, t, eOpts) {
			var li = e.getTarget('li');
			var link = t;
			if(link.tagName = 'A'){
				if(Ext.get(li).hasCls('active')){
					return;
				}
				me.el.select('li.active').removeCls('active');
				Ext.get(li).addCls('active');
//				var oldActiveItem = Ext.isNumber(me.activeItem)?  me.data.items[me.activeItem] : me.activeItem;
				me.activeItem = Ext.get(link).getAttribute("data-index") - 1;
				me.fireEvent('itemactive', me, me.data.items[me.activeItem]);
			}
		});
        me.callParent(arguments);
    },
	loader : {
		url : App.rest('/rest/pre/main/flight-prepare/nav-list.json'),
		renderer : 'data',
		autoLoad : true
	},
	prepareData : function(data) {
		var me = this;
		var items = data.items;
		if(!items){
			items = data.items = [];
		}
		if (data.activeItem && data.activeItem < items.length) {
			items[data.activeItem].active = true;
		}
		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			if (item.active == true) {
				item.liCls = 'active';
			}
			if (!item.href) {
				item.href = "javascript:void(0);";
			}
			
			//special case
			if(this.checked){
				item.iconCls = "icon-ok";
			}
		}
//		me.callParent(arguments);
	},
	initComponent : function() {
		var me = this;
		me.addEvents(
            /**
             * @event itemactive
             * Fires when this button is clicked, and a nav is actived 
             * @param {Ext.button.Button} this
             * @param {Event} e The click event
             */
            'itemactive'
        );
		me.callParent(arguments);
	}
});
