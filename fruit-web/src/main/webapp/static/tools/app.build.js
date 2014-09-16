var apps = file.getFilteredFileList('../libs',new RegExp("/app/[\\w/-]+\\.js$") ).map(function(file) {
	if (file.indexOf('../libs/') == 0 && file.match(/\.js$/)) {
		return file.substr('../libs/'.length).slice(0, -3);
	} else {
		return null;
	}
}).filter(function(file){
	return file != null;
});
var pages = file.getFilteredFileList('../libs',new RegExp("/(page|admin)/[\\w/-]+\\.js$") ).map(function(file) {
	if (file.indexOf('../libs/') == 0 && file.match(/\.*js$/) && !file.match(/\/_/) ) {
		return file.substr('../libs/'.length).slice(0, -3);
	} else {
		return null;
	}
}).filter(function(file){
	return file != null;
});
var pageModules = pages.map(function(file){
	return {
		name: file,
		exclude:['common','ng/module/my','ng/module/hy']
	};
});

debugger
//user node-debug to launch debugger
({
	// copy appdir to dir
//	appDir : '../libs/page',
	dir : '../js',
	// base Url relative to appdir -> ../libs
	baseUrl : "../libs",
	paths : {
		app : "app",
		page : "page",
		"text":"requirejs/text",
		"fastclick":"angularjs/fastclick",
		"bootstrap":"empty:",
		"zepto":"empty:",
		"jquery":"empty:"
	},
	shim: {
    	'angularjs/angular-sa-an-ms-cn': {
            deps: [
                   'angularjs/1.3.0-rc.1/angular',
                   'angularjs/1.3.0-rc.1/angular-sanitize',
                   'angularjs/1.3.0-rc.1/angular-animate',
                   'angularjs/1.3.0-rc.1/angular-messages',
                   'angularjs/1.3.0-rc.1/i18n/angular-locale_zh-cn'
                   ]
        }
    },
	packages:[],
	modules : [ {
		name:'text'
	},{
		name:'common',
		include: apps
	},{
		name:'angularjs/angular-sa-an-ms-cn'
	},{
		name:'ng/module/my'
	},{
		name:'ng/module/hy'
	}].concat(pageModules),
	optimize : 'uglify2',
	optimizeCss:'none',
//	optimize : 'closure',
	// optimize : 'none',
	skipDirOptimize : true,
	generateSourceMaps:true,
	preserveLicenseComments:false
})
