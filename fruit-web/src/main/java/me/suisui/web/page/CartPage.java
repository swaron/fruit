package me.suisui.web.page;

import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.suisui.data.domain.shopping.ShoppingAction;
import me.suisui.data.domain.shopping.ShoppingCart;
import me.suisui.data.domain.shopping.ShoppingProduct;
import me.suisui.data.domain.user.ShiroPrincipal;
import me.suisui.data.jdbc.po.fruit.TuanEpisode;
import me.suisui.data.jdbc.po.fruit.TuanEpisodeProduct;
import me.suisui.data.jdbc.po.fruit.TuanEpisodeProduct2;
import me.suisui.data.jdbc.po.pub.UsrAccount;
import me.suisui.domain.shopping.ShoppingRepository;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.framework.web.support.WebRequestUtils;
import me.suisui.repo.jdbc.dao.fruit.EpisodeDao;
import me.suisui.repo.jdbc.dao.fruit.EpisodeProductDao;
import me.suisui.web.support.uadetector.CachedUserAgentStringParser;
import net.sf.uadetector.ReadableUserAgent;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/page/cart")
public class CartPage {
	
	@Autowired
	EpisodeProductDao episodeProductDao;
	
	
	@Autowired
	EpisodeDao episodeDao;
	
	@RequestMapping("")
	public void index() {
		
	}
	
	@Autowired
	ShoppingRepository shoppingRepository;
	CachedUserAgentStringParser userAgentStringParser = new CachedUserAgentStringParser();
	
	
	/**
	 * 把购物车的内容按照批次归类，同时返回批次与产品详情给页面显示
	 * @param cart
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart-detail.json")
	@ResponseBody
	public ActionResult cartDetail(@RequestBody ShoppingCart cart, HttpServletRequest request) {
		Map<UUID, ShoppingProduct> items = cart.getItems();
		Set<Entry<UUID,ShoppingProduct>> entrySet = items.entrySet();
		ArrayListMultimap<UUID, TuanEpisodeProduct2> eproductByEpisode = ArrayListMultimap.create();
		for (Entry<UUID, ShoppingProduct> entry : entrySet) {
			UUID epid = entry.getValue().getEpid();
			TuanEpisodeProduct2 episodeProduct2 = episodeDao.loadEpisodeProduct(epid);
			UUID episodeId = episodeProduct2.getEpisodeId();
			eproductByEpisode.put(episodeId, episodeProduct2);
		}
		Set<UUID> episodes = eproductByEpisode.keySet();
		Map<UUID,EpisodeCart> cartByEpisode = Maps.newHashMap();
		for (UUID episodeId : episodes) {
			TuanEpisode tuanEpisode = episodeDao.find(TuanEpisode.class, episodeId);
			List<TuanEpisodeProduct2> itemsByEpisode = eproductByEpisode.get(episodeId);
			cartByEpisode.put(episodeId, new EpisodeCart(tuanEpisode,itemsByEpisode));
		}
		return ActionResult.successResult(cartByEpisode);
		
	}
	/**
	 * 只包含同一个episode的购物车内容
	 * @author swaron
	 *
	 */
	@Data
	@AllArgsConstructor
	public static class EpisodeCart{
		private TuanEpisode episode;
		private List<TuanEpisodeProduct2> items;
	}
	
	/**
	 * 购物车的更改通过cart.applyUpdates来。
	 * 如果是提交订单页面，这通过设置actions.getForce()来
	 * @param actions
	 * @param request
	 * @return
	 */
	@RequestMapping("/update-cart.json")
	@ResponseBody
	public ActionResult setCartItem(@RequestBody ShoppingActions actions,HttpServletRequest request) {
		ShoppingCart cart = actions.getCart();
		if(cart == null){
			cart = new ShoppingCart();
		}
		//填充一些信息
		Date now = new Date();
		ReadableUserAgent userAgent = userAgentStringParser.parse(request.getHeader("User-Agent"));
		//ip和浏览器
		String ipAddr = WebRequestUtils.getClientIpAddr(request);
		String device = userAgent.getDeviceCategory().getName();
		
		for (ShoppingAction action : actions.getUpdates()) {
			action.setDev(device);
			action.setIp(ipAddr);
			action.setSubmitTime(now);
			if(action.getEpisodeId() == null){
				UUID epid = action.getEpid();
				TuanEpisodeProduct product = episodeProductDao.find(TuanEpisodeProduct.class, epid);
				if(product != null){
					action.setEpisodeId(product.getEpisodeId());
				}
			}
		}
		
		ShiroPrincipal principal = (ShiroPrincipal) SecurityUtils.getSubject().getPrincipal();
		if(principal == null){
			//计算变化
			cart.applyUpdates(actions.getUpdates());
			ShoppingActionResult result = new ShoppingActionResult( ShoppingActionResult.SUCCESS,cart);
			return ActionResult.successResult(result);
		}else{
			UsrAccount account = principal.getAccount();
			ShoppingCart dbCart = shoppingRepository.getShoppingCart(account.getAccountId());
			if(BooleanUtils.isTrue(actions.getForce()) || dbCart == null){
				cart.applyUpdates(actions.getUpdates());
				shoppingRepository.saveShoppingCart(account.getAccountId(), cart,actions.getUpdates());
				ShoppingActionResult result = new ShoppingActionResult( ShoppingActionResult.SUCCESS,cart);
				return ActionResult.successResult(result);
			}else{
				dbCart.applyUpdates(actions.getUpdates());
				shoppingRepository.saveShoppingCart(account.getAccountId(), dbCart,actions.getUpdates());
				ShoppingActionResult result = new ShoppingActionResult( ShoppingActionResult.SUCCESS,dbCart);
				return ActionResult.successResult(result);
			}
			//send cart changed message via web socket.
		}
	}

	@Data
	public static class ShoppingActionResult {
		public ShoppingActionResult(int code, ShoppingCart cart) {
			this.code = code;
			this.cart = cart;
		}

		public static final int SUCCESS = 1;
		public static final int MERGED = 2;
		ShoppingCart cart;
		int code;//
		List<ShoppingAction> merged;
	}

	@Data
	public static class ShoppingActions {
		Boolean force = false;
		ShoppingCart cart;
		List<ShoppingAction> updates;

		public List<ShoppingAction> getUpdates() {
			if (updates == null) {
				updates = Lists.newArrayList();
			}
			return updates;
		}
	}
}
