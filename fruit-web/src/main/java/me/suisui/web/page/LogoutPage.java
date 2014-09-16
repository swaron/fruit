package me.suisui.web.page;

import me.suisui.framework.web.result.ActionResult;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/page/")
public class LogoutPage {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping("/logout.json")
	@ResponseBody
	public ActionResult logout() {
		 Subject subject = SecurityUtils.getSubject();
        //try/catch added for SHIRO-298:
        try {
            subject.logout();
        } catch (SessionException ise) {
            logger.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }
        return ActionResult.successResult();
	}
}
