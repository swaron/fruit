package me.suisui.web.page.pub;

import me.suisui.data.jdbc.po.fruit.ShopDeliveryAddress;
import me.suisui.framework.paging.PagingParam;
import me.suisui.framework.paging.PagingResult;
import me.suisui.repo.jdbc.dao.fruit.DeliveryAddressDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/page/pub/address", produces = { "application/json" })
public class AddressPage {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DeliveryAddressDao deliveryAddressDao;
	
	@RequestMapping("list.json")
	@ResponseBody
	public PagingResult<ShopDeliveryAddress> list() {
		PagingParam pagingParam = new PagingParam();
		pagingParam.addSort("delivery_address_id", "ASC");
		PagingResult<ShopDeliveryAddress> result = deliveryAddressDao.findPaging(ShopDeliveryAddress.class, pagingParam);
		return result;
	}
}
