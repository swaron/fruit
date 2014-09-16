package me.suisui.web.page;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.suisui.data.jdbc.po.fruit.TuanEpisode;
import me.suisui.data.jdbc.po.fruit.TuanEpisodeProduct2;
import me.suisui.framework.paging.PagingResult;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.repo.jdbc.dao.fruit.EpisodeDao;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

@Controller()
@RequestMapping(value = "/page/episode")
public class EpisodePage {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	EpisodeDao episodeDao;
	ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 首页，当前团购
	 */
	@RequestMapping("/current.html")
	public void currentPage() {

	}
	/**
	 * 首页，当前团购
	 */
	@RequestMapping("/current2.html")
	public void current2Page() {
		
	}
	
	@RequestMapping("/product-index.html")
	public String productIndex(Model model) {
		
		HashMap<UUID, Object> productAttrs = Maps.newHashMap();
		List<TuanEpisodeProduct2> products = episodeDao.findRandomRecentProducts();
		for (TuanEpisodeProduct2 product2 : products) {
			String attrs = product2.getAttrs();
			
			HashMap<String, Object> attrMap = null;
			try {
				attrMap = objectMapper.readValue(attrs, new TypeReference<HashMap<String, Object>>() {
				});
			} catch (IOException e) {
				logger.warn("读取json格式的产品属性出错", e);
				attrMap = Maps.newHashMap();
			}
			productAttrs.put(product2.getProductId(),  attrMap);
		}
		
		model.addAttribute("records", products);
		model.addAttribute("attrs", productAttrs);
		return "jsp/episode/product-index";
	}

	/**
	 * 产品页面，这个批次下的这个产品页面。 title用来回显到新页面的title tag。
	 * 
	 * @param epid
	 * @param title
	 * @return
	 */
	@RequestMapping(value = "/{epid}.html")
	public String product(@PathVariable UUID epid, String title) {
		return "page/episode/product";
	}

	/**
	 * 首页需要的数据
	 * 
	 * @param address
	 * @return
	 */
	@RequestMapping("/current.json")
	@ResponseBody
	public ActionResult currentData(String address) {
		DateTime now = DateTime.now();
		PagingResult<TuanEpisodeProduct2> result = episodeDao.loadBookableEpisodeDetail(address, now);
		for (TuanEpisodeProduct2 product2 : result.getRecords()) {
			String attrs = product2.getAttrs();
			
			HashMap<String, Object> attrMap = null;
			try {
				attrMap = objectMapper.readValue(attrs, new TypeReference<HashMap<String, Object>>() {
				});
				//首页不需要这些属性，移除以减少带宽
				attrMap.remove("产品详细页面HTML内容");
				attrMap.remove("产品文件资源");
				attrMap.remove("产品主图片集");
				attrMap.remove("产品温馨提示");
				product2.setAttrs(objectMapper.writeValueAsString(attrMap));
			} catch (IOException e) {
				logger.warn("读取json格式的产品属性出错", e);
				attrMap = Maps.newHashMap();
			}
		}
		return result;
	}

	/**
	 * 产品的内容
	 * 
	 * @param episodeProductId
	 * @return
	 */
	@RequestMapping("/product.json")
	@ResponseBody
	public ActionResult product(@RequestParam UUID episodeProductId) {
		TuanEpisodeProduct2 product2 = episodeDao.loadEpisodeProduct(episodeProductId);
		if(product2 == null){
			return ActionResult.errorResult("产品数据不存在，可能已删除。");
		}
		UUID episodeId = product2.getEpisodeId();
		TuanEpisode episode = episodeDao.find(TuanEpisode.class, episodeId);
		if(episode != null){
			EpisodeProductResult result = new EpisodeProductResult(product2, episode);
			return ActionResult.successResult(result);
		}else{
			return ActionResult.errorResult("产品数据不存在。可能已删除。");
		}
	}
	@RequestMapping(value = "/episode.json")
	@ResponseBody
	public ActionResult info(@RequestParam UUID episodeId) {
		TuanEpisode episode = episodeDao.find(TuanEpisode.class, episodeId);
		return ActionResult.successResult(episode);
	}
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EpisodeProductResult{
		private TuanEpisodeProduct2 product;
		private TuanEpisode episode;
	}
}
