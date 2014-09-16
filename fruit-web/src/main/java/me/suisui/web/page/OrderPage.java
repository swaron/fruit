package me.suisui.web.page;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.suisui.data.domain.order.CreateOrderParam;
import me.suisui.data.domain.shopping.ShoppingProduct;
import me.suisui.data.domain.user.ShiroPrincipal;
import me.suisui.data.jdbc.code.OrderStatus;
import me.suisui.data.jdbc.code.QueryOrderTimePeriod;
import me.suisui.data.jdbc.po.fruit.GoodsOrder;
import me.suisui.data.jdbc.po.fruit.GoodsOrder2;
import me.suisui.data.jdbc.po.fruit.OrderProduct3;
import me.suisui.data.jdbc.po.fruit.TuanEpisode;
import me.suisui.data.jdbc.po.pub.UsrAccount;
import me.suisui.framework.paging.PagingResult;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.repo.jdbc.dao.fruit.EpisodeDao;
import me.suisui.repo.jdbc.dao.fruit.OrderDao;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/page/order")
public class OrderPage {

	@Autowired
	OrderDao orderDao;
	
	@Autowired
	EpisodeDao episodeDao;

	@RequestMapping("/confirm-order")
	@RequiresUser
	public void confirmOrder() {
	}

	@RequestMapping("/order-submitted")
	@RequiresUser
	public void orderSubmitted() {
	}
	
	@RequestMapping("/order-canceled")
	@RequiresUser
	public void orderCanceled() {
	}

	@RequestMapping("/center")
	@RequiresUser
	public void center() {
	}
	
	@RequestMapping("/view-order")
	@RequiresUser
	public void viewOrder() {
	}

	@RequestMapping("/create-order.json")
	@ResponseBody
	@RequiresUser
	public ActionResult createOrder(@RequestBody CreateOrderParam param) {
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			throw new UnauthenticatedException();
		}
		ShiroPrincipal principal = (ShiroPrincipal) subject.getPrincipal();
		UsrAccount account = principal.getAccount();
		principal.getSession();
		UUID accountId = account.getAccountId();
		String tel = account.getTel();
		// JsonUtils.readString(account.getAttrs(),"lastRecipientName");
		param.setAccountId(accountId);
		param.setTel(tel);
		GoodsOrder goodsOrder = orderDao.findLastOrder(accountId);
		if (goodsOrder != null) {
			param.setRecipient(goodsOrder.getRecipient());
			param.setBackupContact(goodsOrder.getBackupContact());
			param.setTel(tel);
		}
		List<ShoppingProduct> items = param.getItems();
		Iterator<ShoppingProduct> iterator = items.iterator();
		while (iterator.hasNext()) {
			ShoppingProduct entry = iterator.next();
			if(BooleanUtils.isNotTrue(entry.getEnabled()) ){
				iterator.remove();
			}
		}
		
		UUID episodeId = param.getEpisodeId();
		TuanEpisode episode = episodeDao.find(TuanEpisode.class, episodeId);
		if(DateUtils.addHours(episode.getDeliveryDate(), 2).before(new Date()) ){
			return ActionResult.errorResult("创建订单失败，购物车的团购已过期。");
		}
		
		if(param.getItems() == null || param.getItems().isEmpty() ){
			return ActionResult.errorResult("创建订单失败，订单内容为空。");
		}
		GoodsOrder order = orderDao.createBlankOrder(param);
		if(order != null){
			return ActionResult.successResult(order.getOrderId());
		}else{
			return ActionResult.errorResult("创建订单失败。订单包含不同批次的商品。一个订单，只能包含相同批次的商品。");
		}
	}
	@RequestMapping("/order-result.json")
	@ResponseBody
	@RequiresUser
	public ActionResult orderResult(@RequestParam UUID orderId) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UUID accountId = principal.getAccount().getAccountId();
		GoodsOrder order = orderDao.findOrder(accountId, orderId);
		return ActionResult.successResult(order); 
	}
	@RequestMapping("/order-detail.json")
	@ResponseBody
	@RequiresUser
	public ActionResult orderDetail(@RequestParam UUID orderId) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UsrAccount account = principal.getAccount();
		UUID accountId = account.getAccountId();
		GoodsOrder order = orderDao.findOrder(accountId, orderId);
		List<OrderProduct3> products = orderDao.findOrderProducts(order.getOrderId());
		OrderDetailResult result = new OrderDetailResult(order, products);
		return ActionResult.successResult(result);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderDetailResult {
		GoodsOrder order;
		List<OrderProduct3> products;
	}

	/**
	 * 订单里面的产品是不会变的， 目前会变的只有Order本身的内容。
	 * 
	 * @param orderResult
	 * @return
	 */
	@RequestMapping("/submit-order.json")
	@ResponseBody
	@RequiresUser
	public ActionResult submitOrder(@RequestBody OrderDetailResult orderResult) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UsrAccount account = principal.getAccount();
		UUID accountId = account.getAccountId();
		GoodsOrder goodsOrder = orderResult.getOrder();
		goodsOrder.getDeliverAddress();
		goodsOrder.getRecipient();
		goodsOrder.getTel();
		goodsOrder.getDeliverNotes();
		UUID orderId = goodsOrder.getOrderId();
		GoodsOrder order = orderDao.findOrder(accountId, orderId);
		if (order != null) {
			if (OrderStatus.待确认.equals(order.getOrderStatus())) {
				Date deliveryDate = order.getDeliveryDate();
				Date bound = DateUtils.addHours(deliveryDate, 2);
				if(bound.before(new Date())){
					String dateString = DateFormatUtils.format(deliveryDate, "yyyy-MM-dd",TimeZone.getTimeZone("GMT"));
					return ActionResult.errorResult("此订单的配送日期为"+dateString
							+ "，已经过期无法提交了，请重新下单。");
				}
				order.setRecipient(goodsOrder.getRecipient());
				order.setDeliverAddress(goodsOrder.getDeliverAddress());
				order.setTel(goodsOrder.getTel());
				order.setBackupContact(goodsOrder.getBackupContact());
				order.setDeliverNotes(goodsOrder.getDeliverNotes());
				order.setOrderStatus(OrderStatus.等待付款);
				orderDao.update(order);
				return ActionResult.successResult(order);
			} else {
				// String desc = order.getOrderStatus().toString();
				// if (order.getOrderStatus() == OrderStatus.正在出库) {
				// desc = "正在出库";
				// } else if (order.getOrderStatus() == OrderStatus.已完成) {
				// desc = "已完成";
				// } else if (order.getOrderStatus() == OrderStatus.已取消) {
				// desc = "已取消";
				// }
				return ActionResult.errorResult("订单已经成功提交，无法修改内容了。", order);
			}
		} else {
			return ActionResult.errorResult("错误，未找到该订单。");
		}
	}

	@RequestMapping("/my-orders.json")
	@ResponseBody
	@RequiresUser
	public PagingResult<GoodsOrder2> myOrders(String period, String query) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UUID accountId = principal.getAccount().getAccountId();
		CharMatcher orderChars = CharMatcher.inRange('0', '9');
		String name = null;
		String orderNum = null;
		if (query != null) {
			if (orderChars.matchesAllOf(query)) {
				orderNum = query;
			}else{
				name = query;
			}
		}
		DateTime startTime = null;
		if (period != null) {
			if (QueryOrderTimePeriod.LAST_3WEEK.equals(period)) {
				startTime = DateTime.now().minusWeeks(3);
			} else if (QueryOrderTimePeriod.LAST_3MONTH.equals(period)) {
				startTime = DateTime.now().minusMonths(3);
			} else if (QueryOrderTimePeriod.LAST_3YEAR.equals(period)) {
				startTime = DateTime.now().minusYears(3);
			}
		}
		if (name != null) {
			List<GoodsOrder2> orders = orderDao.findOrdersWithProductByProductName(accountId, startTime, name);
			return PagingResult.from(orders);
		} else {
			List<GoodsOrder2> orders = orderDao.findOrdersWithProductByOrderId(accountId, startTime, orderNum);
			return PagingResult.from(orders);
		}
	}
	@RequestMapping("/cancel-order.json")
	@ResponseBody
	@RequiresUser
	public ActionResult cancelOrder(@RequestParam UUID orderId) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UUID accountId = principal.getAccount().getAccountId();
		GoodsOrder order = orderDao.findOrder(accountId, orderId);
		DateTime now = DateTime.now();
		if(order == null){
			return ActionResult.errorResult("订单不存在。");
		}
		if(order.getOrderStatus() == OrderStatus.正在出库){
			order.setUserCancelTime(new Timestamp(now.getMillis()));
			orderDao.update(order);
			return ActionResult.errorResult("卖家已经开始拣货，订单不能修改了。");
		}
		if(order.getOrderStatus() == OrderStatus.已完成){
			return ActionResult.errorResult("订单已完成，不能修改了。");
		}
		if(order.getOrderStatus() == OrderStatus.已取消){
			return ActionResult.errorResult("订单已取消，不用重复取消。");
		}
		List<OrderProduct3> products = orderDao.findOrderProducts(order.getOrderId());
		if(products.isEmpty()){
			return ActionResult.errorResult("订单内容为空，没有包含商品。");
		}
		OrderProduct3 product3 = products.iterator().next();
		
		Date date = product3.getEstimateDeliveryDate();
		//送货那天的5点之后不能取消订单
		DateTime lastChancePoint = new DateTime(date).plusHours(5);
		if(now.isAfter(lastChancePoint)){
			order.setUserCancelTime(new Timestamp(now.getMillis()));
			orderDao.update(order);
			return ActionResult.errorResult("过了早上5点，卖家已经开始去拿货了，订单不能修改了。");
		}else{
			order.setUserCancelTime(new Timestamp(now.getMillis()));
			order.setOrderStatus(OrderStatus.已取消);
			orderDao.update(order);
			return ActionResult.successResult(order);
		}
	}
	@RequestMapping("/claim-request.json")
	@ResponseBody
	@RequiresUser
	public ActionResult claimRequest(@RequestParam UUID orderId,String claimRequest) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UUID accountId = principal.getAccount().getAccountId();
		GoodsOrder order = orderDao.findOrder(accountId, orderId);
		if(order == null){
			return ActionResult.errorResult("订单不存在。");
		}
		if(order.getOrderStatus() == OrderStatus.待确认){
			return ActionResult.errorResult("订单还未提交，这个订单还未生效。");
		}
		if(order.getOrderStatus() == OrderStatus.已取消){
			return ActionResult.errorResult("订单已取消，如有问题，请联系客服。");
		}
		order.setClaimRequest(claimRequest);
		orderDao.update(order);
		return ActionResult.successResult(order);
	}
	
	@RequestMapping("/claim-response.json")
	@ResponseBody
	@RequiresUser
	public ActionResult claimResponse(@RequestParam UUID orderId,String claimResponse) {
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		UUID accountId = principal.getAccount().getAccountId();
		GoodsOrder order = orderDao.findOrder(accountId, orderId);
		if(order == null){
			return ActionResult.errorResult("订单不存在。");
		}
		if(order.getOrderStatus() == OrderStatus.待确认){
			return ActionResult.errorResult("订单还未提交，这个订单还未生效。");
		}
		if(order.getOrderStatus() == OrderStatus.已取消){
			return ActionResult.errorResult("订单已取消，取消的订单无理赔申请。");
		}
		order.setClaimResponse(claimResponse);
		orderDao.update(order);
		return ActionResult.successResult(order);
	}
}
