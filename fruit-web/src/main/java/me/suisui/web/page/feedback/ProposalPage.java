package me.suisui.web.page.feedback;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import me.suisui.data.domain.user.ShiroPrincipal;
import me.suisui.data.jdbc.po.pub.SysFeedback;
import me.suisui.domain.web.WebRequestService;
import me.suisui.framework.paging.PagingParam;
import me.suisui.framework.paging.PagingResult;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.framework.web.support.WebRequestUtils;
import me.suisui.repo.jdbc.dao.pub.FeedbackDao;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/page/feedback/proposal")
public class ProposalPage {
	@Autowired
	FeedbackDao feedbackDao;
	WebRequestService webRequestService = new WebRequestService();

	@RequestMapping("")
	public void index() {
	}

	@RequestMapping("messages.json")
	@ResponseBody
	public PagingResult<SysFeedback> messages() {
		PagingParam pagingParam = new PagingParam();
		pagingParam.addFilter("=", "shielded", false);
		pagingParam.setLimit(100);
		PagingResult<SysFeedback> result = feedbackDao.findPaging(SysFeedback.class, pagingParam);
		return result;
	}

	@RequestMapping("submit.json")
	@ResponseBody
	public ActionResult submit(@RequestBody SysFeedback feedback, HttpServletRequest request) {
		SysFeedback dbFeedback = new SysFeedback();
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated() && subject.getPrincipal() instanceof ShiroPrincipal) {
			ShiroPrincipal principal = (ShiroPrincipal) subject.getPrincipal();
			if (principal != null) {
				dbFeedback.setAccountId(principal.getAccount().getAccountId());
			}
		}
		String ip = webRequestService.getClientIpAddr(request);
		String userAgent = request.getHeader("User-Agent");
		String uid = WebRequestUtils.getUid(request);
		dbFeedback.setShielded(false);
		dbFeedback.setLoginIp(ip);
		dbFeedback.setUserAgent(userAgent);
		dbFeedback.setCookieUid(uid);
		dbFeedback.setContent(feedback.getContent());
		dbFeedback.setUsername(feedback.getUsername());

		PagingParam pagingParam = new PagingParam();
		Date date = LocalDate.now().toDate();
		pagingParam.addFilter("=", "login_ip", ip);
		pagingParam.addFilter(">=", "created_time", date);
		long count = feedbackDao.findCount(SysFeedback.class, pagingParam);
		if (count >= 250) {
			feedbackDao.shieldFeedback(ip, date);
			return ActionResult.errorResult("留言次数太多了");
		} else {
			feedbackDao.persist(SysFeedback.class, dbFeedback);
			return ActionResult.successResult();
		}
	}
}
