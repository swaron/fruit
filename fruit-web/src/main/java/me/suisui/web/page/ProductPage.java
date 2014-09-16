package me.suisui.web.page;

import java.util.UUID;

import me.suisui.repo.jdbc.dao.fruit.EpisodeDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/page/product")
public class ProductPage {
	@Autowired
	EpisodeDao episodeDao;

	@RequestMapping("/{epid}/{pid}")
	public void main(@PathVariable UUID epid, @PathVariable UUID pid) {

	}

}
