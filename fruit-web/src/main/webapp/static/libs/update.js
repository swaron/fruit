(function(){
	var updates = {
		'2014.08.18':function(){
			localStorage.removeItem('cart');
		}
	}
	var current = "2014.08.18";
	
	var supported = ('localStorage' in window) && ('setItem' in window.localStorage);
	if(!supported){
		return;
	}
	var version = localStorage.getItem('version');
	if(version ==null || version < current){
		updates[current]();
		localStorage.setItem('version',current);
	}
})();
