(function() {

	// data-dojo-props etc. is not restricted to JSON, it can be any javascript
	function myEval(text) {
		return eval("(" + text + ")");
	}

	function getNameMap(ctor) {
		// summary:
		// Returns map from lowercase name to attribute name in class, ex: {onclick: "onClick"}
		var map = ctor._nameCaseMap, proto = ctor.prototype;

		// Create the map if it's undefined.
		// Refresh the map if a superclass was possibly extended with new methods since the map was created.
		if (!map) {
			map = ctor._nameCaseMap = {};
			for (var name in proto) {
				if (name.charAt(0) === "_") {
					continue;
				} // skip internal properties
				map[name.toLowerCase()] = name;
			}
		}
		return map;
	}

	Ext.define('Lib.Parser', {
		singleton : true,
		/**
		 * 把一个dom下的所有 data-xtype=xxx 变成ext的组件
		 * 
		 * @param {}
		 *            target
		 */
		parse : function(target) {
			var me = this;
			var list = me.scan(target);
			// reverse一下，这样如果碰到嵌套的widget情况时可以先生成child，再生成parent，从而可以把child加到parent里面
			Ext.each(list.reverse(), function(item) {
				me.transformWidget(item);
			});
		},
		/**
		 * summary: Scan a DOM tree and return an array of objects representing the DOMNodes that need to be turned into
		 * widgets.
		 */
		scan : function(root) {
			var root = root ? Ext.getDom(root) : Ext.getBody().dom;
			var list = []; // Output List
			var extType = "data-xtype";
			// Info on DOMNode currently being processed
			var node = root.firstChild;
			// Metadata about parent node
			var parent = {
				html : ''
			};
			// DFS on DOM tree, collecting nodes with data-dojo-type specified.
			while (true) {
				if (!node) {
					// Finished this level, continue to parent's next sibling
					if (!parent || !parent.node) {
						break;
					}
					node = parent.node.nextSibling;
					parent = parent.parent;
					continue;
				}
				if (node.nodeType == 3) {
					node = node.nextSibling;
					continue;
				}
				if (node.nodeType != 1) {
					// 1 is element, others are
					// Text or comment node, skip to next sibling
					node = node.nextSibling;
					continue;
				}

				if (node.nodeName.toLowerCase() == "script") {
					//node = node.nextSibling;
//					continue;
				}

				// Check for data-xtype attribute, fallback to backward compatible dojoType
				var type = node.getAttribute(extType);

				// Short circuit for leaf nodes containing nothing [but text]
				var firstChild = node.firstChild;
				if (!type && (!firstChild || (firstChild.nodeType == 3 && !firstChild.nextSibling))) {
					parent.html += node.outerHTML;
					node = node.nextSibling;
					continue;
				}

				// Meta data about current node
				var current;

				if (type) {
					// Setup meta data about this widget node, and save it to list of nodes to instantiate
					current = {
						xtype : type,
						ctor : type,
						parent : parent,
						node : node
					};
					list.push(current);
				} else {
					// Meta data about this non-widget node
					current = {
						node : node,
						parent : parent
					};
				}

				// Recurse, looking for
				// descendant nodes with dojoType specified (unless the widget has the stopParser flag).
				// When finished with children, go to my next sibling.
				parent = current;
				node = firstChild;
			}
			return list;
		},
		transformWidget : function(item) {
			var name = item.xtype;
			var parent = item.parent;
			var node = item.node;
			var html = item.html;
			var ctor;
			if (typeof(name) == "string") {
				ctor = Ext.ClassManager.getByAlias(name) || Ext.ClassManager.get(name);
			}
			if (!ctor) {
				ctor = Ext.ClassManager.getByAlias('widget.' + name);
			}
			if (!ctor) {
				Ext.Error.raise('class used in markup not found, make sure the js class file are loaded, class name: ' + name);
				return;
			}
			var dom = Ext.getDom(node);
			var config = {};
			var proto = ctor && ctor.prototype
			for (var attr, i = 0, attrs = dom.attributes, ln = attrs.length; i < ln; i++) {
				attr = attrs.item(i);
				var name = attr.nodeName;
				var lcName = name.toLowerCase();
				var value = attr.nodeValue;
				if (Ext.String.startsWith(name, "data-")) {
					name = name.substring("data-".length);
					lcName = name.toLowerCase();
				}
				switch (lcName) {
					case "xtype" :
						break;
					case "class" :
						config["cls"] = dom.className;
						break;
					default :
						// Normal attribute, ex: value="123"
						// Find attribute in widget corresponding to specified name.
						// May involve case conversion, ex: onclick --> onClick
						if (!(name in proto)) {
							var map = getNameMap(ctor);
							name = map[lcName] || name;
						}
						if (name in proto) {
							switch (typeof proto[name]) {
								case "string" :
									config[name] = value;
									break;
								case "number" :
									config[name] = value.length ? Number(value) : NaN;
									break;
								case "boolean" :
									// for checked/disabled value might be "" or "checked". interpret as true.
									config[name] = value.toLowerCase() != "false";
									break;
								case "function" :
									// override function is not allowed.
									break;
								default :
									var pVal = proto[name];
									if (pVal === null || pVal === undefined) {
										// 如果值为空，类型无法判断,那我们是用eval还是直接用value，不好决定，先用value。
										config[name] = value;
									} else {
										config[name] = (pVal && "length" in pVal) ? (value
												? value.split(/\s*,\s*/)
												: []) : // array
												(pVal instanceof Date) ? (value == "" ? new Date("") : // the NaN of
																										// dates
														value == "now" ? new Date() : // current date
																Ext.Date.parse(value, "c")) : myEval(value);
									}
							}
						} else {
							// 如果值在原型上没有，而属性是data-开头，那么强制作为参数, 并且换成驼峰形
							if (attr.nodeName.indexOf('data-') == 0) {
								name = Ext.dom.Element.normalize(name);
								config[name] = value;
							}
						}
				}

			}
			if (item.items) {
				config["items"] = item.items;
			}else if(node.innerHTML && ! /^\s*$/.test(node.innerHTML)){
				config["html"] = node.innerHTML;
			}
			
			if(node.tagName){
				config['tag'] = node.tagName;
			}
			var widget = new ctor(config);
			if (parent && parent.xtype) {
				parent.items = parent.items || [];
				parent.items.push(widget);
			} else {
				widget.render(dom.parentNode, dom);
				Ext.removeNode(dom);
			}
		}
	});
})();
