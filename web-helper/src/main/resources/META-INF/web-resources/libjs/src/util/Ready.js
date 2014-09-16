// 
//有些页面要等一些组件加载完之后才能做一些初始化.这个类的作用就是做这个的。
//var loader = new loader.Loader();
// loader.require('comp1');
// loader.onReady(function(loader,datas){
// 	datas['comp1'];
// });
// loader.register('comp1',data);
Ext.define('Lib.util.Ready', {
	statics : {
		loaders : Ext.create('Ext.util.MixedCollection'),
		getLoader : function(loaderId) {
			return this.loaders.getByKey(loaderId);
		}
	},
	mixins : {
		observable : 'Ext.util.Observable'
	},
	requires : {},
	registers : [],
	constructor : function(config) {
		// The Observable constructor copies all of the properties of `config` on
		// to `this` using Ext.apply. Further, the `listeners` property is
		// processed to add listeners.
		//
		this.mixins.observable.constructor.call(this, config);

		this.addEvents('ready');
		if (config && config.loaderId) {
			this.statics().loaders.add(config.loaderId, this);
		}
	},
	/**
	 * 增加一个需求
	 * 
	 */
	requireValue : function(o) {
		var me = this, requires = me.requires || (me.requires = {}), arg, args, i;

		if (typeof o == 'string') {
			for (args = arguments, i = args.length; i--;) {
				arg = args[i];
				if (!requires[arg]) {
					requires[arg] = true;
//					Lib.log('require ' + arg);
				}
			}
		} else {
			Ext.applyIf(me.requires, o);
//					Lib.log('require ' + o);
		}
	},
	requireField:function(/*arguments*/){
		var me = this, requires = me.requires || (me.requires = {}), arg, args, i;
		for (args = arguments, i = args.length; i--;) {
			arg = args[i];
			if (!requires[arg]) {
				var comp = Ext.getCmp(arg);
				if(!comp){
					comp = Ext.ComponentQuery.query('field[name=' +arg+']');
				}
				if(comp && Ext.isFunction(comp.getValue) && comp.getValue() !== undefiend && comp.getValue()!= null ){
					//value already exist
					requires[field] = true;
					//fire provide
					me.provideValue(arg, comp.getValue());
				}else{
					requires[arg] = true;
				}
//				Lib.log('require ' + arg);
			}
		}
	},
	onReady : function(fn) {
		var me =this;
		if(Ext.isFunction(fn)){
			this.on('ready', fn,this,{
				single:true
			});
		}else if(Ext.isObject(fn)){
			Ext.applyIf(fn,{
				single:true
			});
		}else{
			this.on('ready', fn,this,{
				single:true
			});
		}
		if(me.requiredSatisfied()){
//			Lib.log('fire page ready, source by on ready init');
			me.fireEvent('ready', me, me.registers);
		}
		
	},
	requiredSatisfied:function(){
		var requires = this.requires;
		var registers = this.registers;
		for (var r in requires){
	        if(requires.hasOwnProperty(r) && requires[r] !== false){
	        	if(registers.hasOwnProperty(r) ){
	        		continue;
	        	}else{
	        		return false;
	        	}
	        }
		}
		return true;
	},
	provideValue : function(name, data) {
//		Lib.log('provide ' + name);
		var me = this, registers = me.registers || (me.registers = {});
		if(registers.hasOwnProperty(name)){
			//already regiester, we can return now.
		}
		if (typeof name == 'string') {
			if(typeof data == 'undefined'){
				registers[name] = true;
			}else{
				registers[name] = data;
			}
		}
		if(me.requiredSatisfied()){
//			Lib.log('fire page ready, source by ' + name);
			me.fireEvent('ready', me, registers);
		}
	}
},function(){
	Lib.CompReady = Ext.create('Lib.util.Ready',{
		loaderId:'page'
	});
//	Lib.log('create page readier');
});