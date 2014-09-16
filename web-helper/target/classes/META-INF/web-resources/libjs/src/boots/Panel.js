/**
 * 超级简单的bootstrap panel，如果这个panel的功能不够用，那么就用ext的panel吧
 * @class Lib.boots.Panel
 * 
 */
Ext.define('Lib.boots.Panel', {
    extend: 'Ext.container.Container',
    alias: 'widget.boots.panel',
 	config : {
		collapsible : true,
		collapsed : false
	},
//	cls:'panel-primary',
	componentCls:'panel',
 	baseCls : Ext.baseCSSPrefix + 'boots-panel',
    childEls: [
        'header','body','headerCt'
    ],
    renderTpl: [
        // If this Panel is framed, the framing template renders the docked items round the frame
        // This empty div solves an IE6/7/Quirks problem where the margin-top on the bodyEl
        // is ignored. Best we can figure, this is triggered by the previousSibling being
        // position absolute (a docked item). The goal is to use margins to position the
        // bodyEl rather than left/top since that allows us to avoid writing a height on the
        // panel and the body. This in turn allows CSS height to expand or contract the
        // panel during things like portlet dragging where we want to avoid running a ton
        // of layouts during the drag operation.
        // This empty div also has to be relatively positioned, otherwise it crashes IE6-9 Quirks
        // when panel is rendered in a table-based layout.
	    	'<div id="{id}-header" class="x-boots-panel-header panel-heading {headerCls}" <tpl if="collapsible">data-toggle="collapse" data-target="#{id}-body" style="cursor:pointer;" </tpl> >',
	    		'<div class="row"><div id="{id}-headerCt"  class="col-md-12">',
				'{%this.renderHeader(out,values);%}',
					'<tpl if="collapsible">', 
						'<div class="pull-right x-boots-tool"><i class="icon-tool-collapse">&nbsp;</i></div>',
					'</tpl>',
	    		'</div>',
	    		'</div>',
			'</div>',
	        '<div id="{id}-body" class="x-boots-panel-body {collapsedCls} <tpl if="bodyBorder">x-boots-panel-body-border</tpl>"<tpl if="bodyStyle"> style="{bodyStyle}"</tpl>>',
		           '{%this.renderContainer(out,values);%}',
	        '</div>'
    ],
    headerLoader:null,
    headerData:{},
    initEvents:function(){
//    	show.bs.collapse	This event fires immediately when the show instance method is called.
//		shown.bs.collapse	This event is fired when a collapse element has been made visible to the user (will wait for CSS transitions to complete).
//		hide.bs.collapse	 This event is fired immediately when the hide method has been called.
//		hidden.bs.collapse	This event is fired when a collapse element has been hidden from the user (will wait for CSS transitions to complete).
    	var me = this;
    	var bodyId = this.getId()+'-body';
		$('#' + bodyId).on('hidden.bs.collapse', function () {
			me.updateLayout();
		})
		$('#' + bodyId).on('shown.bs.collapse', function () {
			me.updateLayout();
		})
		this.callParent();
    },
    initComponent : function() {
		var me = this;
		this.callParent();
		this.headerData = Ext.apply(this.headerData, {
			title : this.title
		});
		if (me.headerLoader) {
			if (me.headerLoader.isLoader) {
				loader.setTarget(me);
			} else {
				me.headerLoader = new Ext.ComponentLoader(Ext.apply({
					target : me,
					renderer : function(loader, response, active) {
						var success = true;
						try {
							var data = Ext.decode(response.responseText);
							if (Ext.isFunction(loader.getTarget().prepareData)) {
								loader.getTarget().prepareData(data);
							}
							loader.getTarget().updateHeader(data);
						} catch (e) {
							success = false;
						}
						return success;
					}
				}, me.headerLoader));
			}

		}
	},
	updateHeader:function(data){
		var me  = this;
		if(me.headerCt){
			me.headerCt.update(me.headerTpl.apply(data));
		}
	},
	doRenderHeader:function(out,renderData){
        // Careful! This method is bolted on to the renderTpl so all we get for context is
        // the renderData! The "this" pointer is the renderTpl instance!
        var me = renderData.$comp;
        if (me.headerTpl) {
            // Make sure this.tpl is an instantiated XTemplate
            if (!me.headerTpl.isTemplate) {
                me.headerTpl = new Ext.XTemplate(me.headerTpl);
            }
            if (me.headerData) {
                //me.tpl[me.tplWriteMode](target, me.data);
                me.headerTpl.applyOut(me.headerData, out);
                delete me.headerData;
            }
        }
	},
	setupRenderTpl: function (renderTpl) {
        this.callParent(arguments);
        renderTpl.renderHeader = this.doRenderHeader;
    },
	initRenderData: function() {
        var me = this,
            data = me.callParent();
		Ext.apply(data, {
			headerCls: me.headerCls,
			collapsedCls: me.collapsed? 'collapse':'in',
			collapsible: me.collapsible,
			bodyBorder: me.bodyBorder,
			bodyStyle: me.bodyStyle
		});
        return data;
    }
});
