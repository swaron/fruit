package me.suisui.web.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page/help")
public class HelpPage {
	
	@RequestMapping("/*")
	public void index() {
		
	}
}
