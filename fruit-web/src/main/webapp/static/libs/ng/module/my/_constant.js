define([ './_module' ], function(module) {
	module.constant('db_name', {
		OrderStatus : {
			"待确认" : 0,
			"等待付款" : 1,
			"等待出库" : 2,
			"正在出库" : 3,
			"已完成" : 4,
			"已取消" : 5
		}
	});
	module.constant('db_code', {
		"goods_order" : {
			"order_status":{
				0 : "待确认",
				1 : "等待付款",
				2 : "等待出库",
				3 : "正在出库",
				4 : "已完成",
				5 : "已取消"
			}
		}
	});
});