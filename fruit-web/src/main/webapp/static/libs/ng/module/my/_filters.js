define([ './_module',"app/utils","jquery",'app/modernizr' ], function(module,utils,jquery,modernizr) {
	module.filter('boolean', function() {
		return function(input) {
			return !!input ? '是':'否';
		};
	});
	var fileTypeCls = {
		jpg:'al-tupian',
		jpeg:'al-tupian',
		png:'al-tupian',
		gif:'al-tupian',
		mp3:'al-xinjian',
		ape:'al-xinjian',
		wma:'al-xinjian',
		avi:'fa-dianying',
		rmvb:'fa-dianying',
		rm:'fa-dianying',
		avi:'fa-dianying',
		mpg:'fa-dianying',
		mpeg:'fa-dianying',
		wmv:'fa-dianying',
		mp4:'fa-dianying',
		mkv:'fa-dianying',
		asf:'fa-dianying',
		divx:'fa-dianying'
	};
	module.filter('myFileTypeCls', function() {
		return function(input) {
			var ext = input.substr( input.lastIndexOf('.') + 1);
			if(fileTypeCls.hasOwnProperty(ext)){
				return 'al ' + fileTypeCls[ext];
			}else{
				return 'al al-xinjian';
			}
		};
	});
	module.filter('thumb', function() {
		return function(input, size) {
			if(input == null){
				return null;
			}
			var mapping = {
				"XXS":60,
				"XS":120,
				"S": 240,
				"M": 360,
				"L": 800,
				"XL": 1028,
				"XXL":1600
			};
//			//autofit 根据屏幕大小来，配合image-service
//			if(size == 'autofit'){
//				
//			}
			var length = mapping[size]||360;
			var url = utils.url(input, {
				w : length,
				h : length
			});
			return url;
		};
	});
	module.filter('dbcode', ['db_code' ,function(codes) {
		return function(input, table, col) {
			var tables = codes[table];  
			if(tables){
				var cols = tables[col];
				if(cols){
					var val = cols[input];
					if(val != null){
						return val;
					}
				}
			}
			return "";
		};
	}]);
	module.filter('price', ['' ,function() {
		return function(input) {
			if(typeof input == 'number'){
				return parseFloat(input).toFixed(2);
			}else{
				return input;
			}
		};
	}]);
	module.filter('autofit', [function() {
		return function(input, eid) {
			var width = jquery(eid).width();
			if(width > 20){
				var url = utils.url(input, {
					w : width
				});
				return url;
			}else{
				return undefined;
			}
		};
	}]);
	var mobile = modernizr.mobile();
	var windowWidth = window.innerWidth || 0;
	var windowHeight = window.innerHeight || 0;
	//手机嘛，省点流量
	var shouldFitScreen =mobile && (windowWidth <= 640 || windowHeight <= 640); 
	module.filter('phonefit', ['$filter',function($filter) {
		var thumb = $filter('thumb');
		return function(input, eid, fallback) {
			var width = jquery(eid).width();
			if(width > 20){
				//小于20，屏幕可能还没渲染完，没法获得宽度
				if(shouldFitScreen){
					//手机，省流量
					var url = utils.url(input, {
						w : width
					});
					return url;
				}else{
					if(fallback){
						return thumb(input,fallback);
					}else{
						return input;
					}
				}
			}else{
				return undefined;
			}
		};
	}]);
});