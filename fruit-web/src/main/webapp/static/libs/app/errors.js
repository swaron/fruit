define([], function() {
	var Errors = function(){
		
	};
	angular.extend(Errors.prototype,{
		email:'email 格式有误',
		max:'超出最大值',
		maxlength:'超出最大允许长度',
		min:'超出最小值',
		minlength:'长度不够',
		number:'只能输入数字',
		pattern:'格式不符合',
		required:'必填项',
		url:'url 格式有误'
	});
	return new Errors();
});
