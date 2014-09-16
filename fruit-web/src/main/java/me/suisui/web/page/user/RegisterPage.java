package me.suisui.web.page.user;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;
import me.suisui.data.jdbc.po.pub.UsrAccount;
import me.suisui.domain.user.UserService;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.repo.jdbc.dao.pub.UserDao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/page/user/register")
public class RegisterPage {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private UserDao userDao;

	private EmailValidator emailValidator = new EmailValidator();

	private boolean setUsername(String username, UsrAccount account) {
		boolean isEmail = emailValidator.isValid(username, null);
		if (isEmail) {
			account.setEmail(username);
			return true;
		}
		boolean isQQ = StringUtils.isNumeric(username) && (username.length() >= 5 && username.length() <= 10);
		if (isQQ) {
			account.setQq(username);
			return true;
		}
		boolean isTel = StringUtils.isNumeric(username) && (username.length() == 11);
		if (isTel) {
			account.setTel(username);
			return true;
		}
		return false;
	}

	@RequestMapping("")
	public void registerPage() {

	}

	@RequestMapping("/submit.json")
	@ResponseBody
	public ActionResult register(@RequestBody RegParam param) {
		UsrAccount account = userService.findAccount(param.getUsername());
		if (account != null) {
			return ActionResult.errorResult("用户名" + param.getUsername() + "已存在。");
		}
		account = new UsrAccount();
		account.setAttrs("{}");
		boolean username = setUsername(param.getUsername(), account);
		if (username == false) {
			return ActionResult.errorResult("用户名不合规范，可以选择的有邮箱带@和后缀，QQ或手机号码。");
		}
		account.setRegisterTime(new Timestamp(System.currentTimeMillis()));
		userDao.persist(UsrAccount.class, account);
		UUID uuid = account.getAccountId();
		String encoded = userService.encodePassword(uuid.toString(), param.getPassword());
		account.setPwd(encoded);
		userDao.update(account);
		return ActionResult.successResult();
	}

	@Data
	public static class RegParam {
		String username;
		String password;
	}
}
