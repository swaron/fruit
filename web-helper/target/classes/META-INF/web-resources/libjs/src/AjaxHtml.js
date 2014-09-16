/**
 * 通过Ajax获取数据，结合写在Html里面template，渲染结果。 结果类似 通过jsp+jstl的实现。
 */
Ext.define('Lib.AjaxHtml', {
	alias : 'widget.ajaxhtml',
	extend : 'Ext.Component',
	config : {
		url : '',
		html : '',
		tag:'',
		params : null
	},
	data:{},
	processNode:function(node){
		if (node.nodeName.toLowerCase() != 'tpl' && node.hasAttribute && node.hasAttribute('for') ) {
			jQuery(node).wrap('<tpl for="' + node.getAttribute('for') + '" ></tpl>')
			node.removeAttribute('for');
		}
		if(node.childNodes && node.childNodes.length > 0){
			var nodes = node.childNodes;
			for (var i = 0; i < nodes.length; i++) {
				this.processNode(nodes[i]);
			}
		}
	},
	processSourceTpl : function(src) {
		var html = jQuery.parseHTML( '<div>' + src + '</div>')[0];
		this.processNode(html);
		var tpl = html.innerHTML;
		return tpl;

	},
	constructor:function(config){
		this.callParent(arguments);
	},
	initComponent : function() {
		if (this.html) {
			var source = this.html;
			delete this.html;
			if(this.tag && this.tag.toUpperCase() == 'SCRIPT'){
				this.tpl = source;
			}else{
				this.tpl = this.processSourceTpl(source);
			}
		}
		if (this.url) {
			var params = Ext.Object.fromQueryString(window.location.search);
			this.loader = {
				url : this.url,
				renderer : 'data',
				autoLoad : true,
				params : Ext.apply(params, this.params || {})
			}
		}
		this.callParent(arguments);
		this.removeCls('hidden');
	}
});
