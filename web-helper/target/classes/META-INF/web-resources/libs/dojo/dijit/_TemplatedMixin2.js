define([
	"dojo/cache",	// dojo.cache
	"dojo/_base/declare", // declare
	"dojo/dom-construct", // domConstruct.destroy, domConstruct.toDom
	"dojo/_base/lang", // lang.getObject
	"dojo/on",
	"dojo/sniff", // has("ie")
	"dojo/string", // string.substitute string.trim
	"./_AttachMixin",
	"doT",
	"dojo/_base/xhr"
], function(cache, declare, domConstruct, lang, on, has, string, _AttachMixin,doT,xhr){

	// module:
	//		dijit/_TemplatedMixin

	var _TemplatedMixin = declare("dijit._UnderscoreMixin", _AttachMixin, {
		// summary:
		//		Mixin for widgets that are instantiated from a template

		// templateString: [protected] String
		//		A string that represents the widget template.
		//		Use in conjunction with dojo.cache() to load from a file.
		// html = tpl + data
		tpl: null,
		data:null,
		url:null,

/*=====
		// _rendered: Boolean
		//		Not normally use, but this flag can be set by the app if the server has already rendered the template,
		//		i.e. already inlining the template for the widget into the main page.   Reduces _TemplatedMixin to
		//		just function like _AttachMixin.
		_rendered: false,
=====*/

		update: function(data){
			var me = this;
			me.data = data;
			if(me.tpl && me.data){
				var node = domConstruct.toDom(me.tpl(me), this.ownerDocument);
				if(node.nodeType != 1){
					// Flag common problems such as templates with multiple top level nodes (nodeType == 11)
					throw new Error("Invalid template: " + cached);
				}
				this.domNode =node;
			}
		},
		initialTpl: function(){
			var me = this;
			if(typeof me.tpl == "string"){
				me.tpl = doT.template(me.tpl);
			}
			var url = this.url;
			if(url){
				var param = {
					handleAs : "json",
					load : function(data) {
						me.update(data);
					}
				};
				if(typeof url == 'string'){
					param.url = url;
				}else if(typeof url == 'object'){
					lang.mix(param,url);
				}
				xhr.get(param);
			}
			if(me.tpl && me.data){
				var node = domConstruct.toDom(me.tpl(me), this.ownerDocument);
				if(node.nodeType != 1){
					// Flag common problems such as templates with multiple top level nodes (nodeType == 11)
					throw new Error("Invalid template: " + cached);
				}
				this.domNode =node;
			}
		},

		buildRendering: function(){
			// summary:
			//		Construct the UI for this widget from a template, setting this.domNode.
			// tags:
			//		protected

			if(!this._rendered){
				this.initialTpl();
			}

			// Call down to _WidgetBase.buildRendering() to get base classes assigned
			// TODO: change the baseClass assignment to _setBaseClassAttr
			this.inherited(arguments);

			if(!this._rendered){
				this._fillContent(this.srcNodeRef);
			}

			this._rendered = true;
		},

		_fillContent: function(/*DomNode*/ source){
			// summary:
			//		Relocate source contents to templated container node.
			//		this.containerNode must be able to receive children, or exceptions will be thrown.
			// tags:
			//		protected
			var dest = this.containerNode;
			if(source && dest){
				while(source.hasChildNodes()){
					dest.appendChild(source.firstChild);
				}
			}
		}

	});

	return _TemplatedMixin;
});
