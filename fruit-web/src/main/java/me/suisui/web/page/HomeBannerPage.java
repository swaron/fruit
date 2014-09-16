package me.suisui.web.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.suisui.data.jdbc.po.fruit.ArtHomeBanner;
import me.suisui.data.jdbc.po.fruit.ArtHomeBannerPlusDa;
import me.suisui.framework.paging.PagingResult;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.repo.jdbc.dao.fruit.HomeBannerDao;

import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

@Controller()
@RequestMapping(value = "/page/home-banner")
public class HomeBannerPage {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private HomeBannerDao homeBannerDao;

	@RequestMapping("/intro.html")
	public void index() {
	}
//	ArtHomeBannerPlusDa

	@RequestMapping(value = "/detail.json")
	@ResponseBody
	public ActionResult detail(Integer homeBannerId) {
		ArtHomeBannerPlusDa banner = homeBannerDao.detail(homeBannerId);
		return ActionResult.successResult(banner);
	}
	/**
	 * 1. weight>0有符合的，就从这里面按照weight取。 2. 只有weight=0的，随机取
	 * 
	 * @param address
	 * @return
	 */
	@RequestMapping("/current.json")
	@ResponseBody
	public ActionResult currentData(Integer deliveryAddressId) {
		List<ArtHomeBanner> list = homeBannerDao.list(deliveryAddressId);
		List<ArtHomeBanner> hasWeight = Lists.newArrayList();
		List<ArtHomeBanner> noWeight = Lists.newArrayList();
		for (ArtHomeBanner artHomeBanner : list) {
			if (artHomeBanner.getBannerWeight() != null && artHomeBanner.getBannerWeight() > 0) {
				hasWeight.add(artHomeBanner);
			} else {
				noWeight.add(artHomeBanner);
			}
		}
		if (hasWeight.isEmpty()) {
			return PagingResult.from(weightSelectOne(noWeight));
		}else{
			return PagingResult.from(weightSelectOne(hasWeight));
		}
	}

	private ArrayList<ArtHomeBanner> weightSelectOne(List<ArtHomeBanner> weightList) {
		if(weightList.isEmpty()){
			return Lists.newArrayList();
		}
		int[] weights = new int[weightList.size()];
		for (int i = 0; i < weights.length; i++) {
			Integer weight = weightList.get(i).getBannerWeight();
			weights[i] = weight;
		}
		int index = randomWeight(weights);
		ArrayList<ArtHomeBanner> singleImage = Lists.newArrayList(weightList.get(index));
		return singleImage;
	}

	private int randomWeight(int[] weights) {
		int[] sums = new int[weights.length];
		int count = 0;
		for (int i = 0; i < weights.length; i++) {
			int w = weights[i];
			count += w;
			sums[i] = count;
		}
		int index = RandomUtils.nextInt(count);
		for (int i = 0; i < sums.length; i++) {
			if (index < sums[i]) {
				return i;
			}
		}
		return 0;
	}
}
