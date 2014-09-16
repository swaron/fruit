package me.suisui.web.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class RootPage {
	@RequestMapping("")
	public String index() {
		return "page/episode/current";
	}

}
