package me.suisui.web.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/page/user")
public class UserPage {
	
	@RequestMapping("/unauthorized")
	private void unauthorized() {

	}
}
