/**
 * Auto adjust table column width with the content in table,
 * 1. forceFit==true:  the width of columns is flex, will force fit into grid and auto resize with grid, 
 * 2. forceFit==false: the width of columns is static, will NOT resize with grid. total column width is equal or larger then grid size. 
 * depends on extjs 4.2
 */
Ext.define('Lib.grid.feature.AutoColumnWidth', {
	extend : 'Ext.grid.feature.Feature',
	alias : 'feature.autocolumnwidth',
	requires : ['Ext.grid.plugin.HeaderResizer'],
	eventPrefix : 'autowidth',
	ftype: 'autocolumnwidth',
	config:{
		maxColWidth : 500,
		applyOnce : true,
		applied : false,
		minColWidth : null
	},
	
	init : function() {
		this.grid;
		this.view;
		this.headerCt = this.view.getHeaderCt();
		this.resizer = this.headerCt.resizer;
		if (this.resizer) {
			this.minColWidth = this.minColWidth || this.resizer.minColWidth;
			this.maxColWidth = this.maxColWidth || this.resizer.maxColWidth;
		}else{
			this.minColWidth = this.minColWidth || Ext.grid.plugin.HeaderResizer.prototype.minColWidth;
			this.maxColWidth = this.maxColWidth || Ext.grid.plugin.HeaderResizer.prototype.maxColWidth;
		}
		this.view.on('viewready', this.afterViewReady, this, {
			single : false
		});
		this.grid.mon(this.grid.store,'load',this.onStoreLoad, this, {
			single : false
		});
		this.grid.on('reconfigure',this.forceReAutoLayout, this, {
			single : false
		});
	},
	onStoreLoad:function(){
//		this.storeLoaded = true;
		this.afterViewReady();
	},
	forceReAutoLayout: function() {
		this.applied = false;
	},
	afterViewReady : function() {
		if(this.applied || !this.grid.store || !this.view.viewReady){
			//store or view is not ready, return.
			return;
		}
		if(this.lastOptions.page ！= 1){
			//if first page，recaculate width
			return；
		}
		var headerCt = this.headerCt;
		var cols = headerCt.getVisibleGridColumns();
		this.adjustColumnWidth(cols);
		this.applied = true;
	},
	adjustColumnWidth:function(cols){
		//for performance reason, calculate all column's width, and update all at the same time. 
		var viewEl = this.view.el;
		var totalColWidth = 0;
		var lockWidth = 0;
		for(var i=0,ln = cols.length;i<ln;i++){
			var col = cols[i];
			if(col.locked || col.isCheckerHd){
				//don't change
				if(!col.width && col.flex && col.isCheckerHd){
					//when forceFit ==true, width was set to flex.
					col.width = col.flex;
					delete col.flex;
				}
				col.newWidth = col.width;
				lockWidth += col.newWidth;
			}else{
				var maxWidth = this.view.getMaxContentWidth(col);
				if(isNaN(maxWidth)){
					//不知道为什么ie8返回nan,hack ie8
					maxWidth = 0;
				}
				col.newWidth = Ext.Number.constrain(maxWidth, this.minColWidth, this.maxColWidth);
				totalColWidth += col.newWidth;
			}
		}
		
		
        Ext.suspendLayouts();
        
        if(this.grid.forceFit){
        	//use flex for relative layout
    		for(var i=0,ln = cols.length;i<ln;i++){
    			var col = cols[i];
    			if(col.locked || col.isCheckerHd){
					//don't change width
				}else{
	    			if (col.width) {
	    	        	delete col.width;
	    	        }
	    			col.flex = col.newWidth;
				}
    		}
        }else{
        	//user absolute pixel layout
        	var containerWidth = this.view.getWidth();
			if(totalColWidth < (containerWidth-lockWidth) && totalColWidth > 0 ){
				var extend = (containerWidth-lockWidth)/totalColWidth;
				for(var i=0,ln = cols.length;i<ln;i++){
					var col = cols[i];
					if(col.locked || col.isCheckerHd){
					//don't change width
					}else{
						//expend width to container
						col.newWidth = col.newWidth * extend;
					}
				}
			}
			
        	for(var i=0,ln = cols.length;i<ln;i++){
        		var col = cols[i];
        		if (col.flex) {
        			delete col.flex;
        		}
        		col.setWidth(col.newWidth);
        	}
        }
		//leave last column as flex when force fit is not specified.
//		if(i > 0 && !this.forceFit){
//			cols[i-1].flex = 1;
//		}
		
//		var viewEl = this.view.el;
//		var table = viewEl.down('.' + Ext.baseCSSPrefix + 'grid-table');
		
		this.view.setWidth(this.headerCt.getFullWidth());
        Ext.resumeLayouts(true);
	}
//	updateWidthPorperties:function(cols){
//		var viewEl = this.view.el;
//		var totalColWidth = 0;
//		for(var i=0,ln = cols.length;i<ln;i++){
//			var col = cols[i];
//			var maxWidth = this.view.getMaxContentWidth(col);
//			col.newWidth = maxWidth = Ext.Number.constrain(maxWidth, this.minColWidth, this.maxColWidth);
//			totalColWidth += maxWidth;
//		}
//		
//		
//		var containerWidth = this.view.getWidth();
//		if(this.grid.forceFit){
//			//use flex for relative layout
//			if(totalColWidth < containerWidth && totalColWidth > 0 ){
//				var extend = containerWidth/totalColWidth;
//				for(var i=0,ln = cols.length;i<ln;i++){
//					var col = cols[i];
//					//expend width to container
//					col.newWidth = col.newWidth * extend;
//				}
//			}
//		}else{
//			//user absolute pixel layout
//			if(cols.length > 0){
//				var col = cols[cols.length -1];
//				 col.newWidth = containerWidth-totalColWidth+col.newWidth;
//			}
//		}
//	}
	
});