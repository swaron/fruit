define([], function() {
	tests = {
		mobile:function(){
			return (/android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i.test(navigator.userAgent.toLowerCase()));
		}
	};
	features = {};
	Modernizr = {};
	for ( var feature in tests ) {
        featureName  = feature.toLowerCase();
        Modernizr[featureName] = function(){
        	if(typeof features[featureName] == 'undefined'){
        		features[featureName] = tests[featureName]();
        		return features[featureName]; 
        	}else{
        		return features[featureName];
        	}
        };
    }
	return Modernizr;
});
