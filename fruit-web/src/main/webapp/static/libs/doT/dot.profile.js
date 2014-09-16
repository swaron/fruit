var profile = (function() {
	var copyOnly = function(filename, mid) {
		var list = {
			"doT/dot.profile" : 1,
			"doT/package.json" : 1
		};
		var test = (mid in list) || !(/\.js$/.test(filename)) || (/^doT\/examples\//.test(filename));
		return test;
	};

	var config = {
		resourceTags : {
			test : function(filename, mid) {
				return mid == "app/tests" || mid == "app/robot";
			},
			copyOnly : function(filename, mid) {
				return copyOnly(filename, mid);
			},
			amd : function(filename, mid) {
				return !copyOnly(filename, mid) && /\.js$/.test(filename);
			}
		}
	};
	return config;
})();