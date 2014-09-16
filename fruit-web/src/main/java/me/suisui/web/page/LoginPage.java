package me.suisui.web.page;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;
import me.suisui.data.jdbc.po.pub.UsrAccount;
import me.suisui.data.jdbc.po.pub.UsrSession;
import me.suisui.domain.login.LoginService;
import me.suisui.domain.user.UserService;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.repo.jdbc.dao.pub.UserDao;
import me.suisui.web.shiro.ShiroAccessTokenRealm;
import me.suisui.web.shiro.ShiroAccessTokenFilter.AccessToken;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Joiner;

@Controller
@RequestMapping(value = "/page/login")
public class LoginPage {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserDao userDao;
	@Autowired
	private LoginService loginService;
	@Autowired
	private UserService userService;
	@Autowired
	ServletContext servletContext;

	@Autowired
	ShiroAccessTokenRealm shiroAccessTokenRealm;
	
	private String cookiePath;

	@RequestMapping("")
	public void index() {

	}

	@PostConstruct
	private void init() throws NoSuchAlgorithmException {
		// ensure no exception .
		Mac.getInstance("HmacSHA256");
		if ("".equals(servletContext.getContextPath())) {
			cookiePath = "/";
		} else {
			cookiePath = servletContext.getContextPath();
		}

	}

	/**
	 * retrieve user uuid from variable username
	 * 
	 * <pre>
	 * 		var isEmail = validate.isEmailAddress(val);
	 * 		var isQQ = /^\d{5,10}$/.test(val);
	 * 		var isTel = /^\d{11}$/.test(val);
	 * 		var isIdentity = /^\d{17}\w$/.test(val);
	 * 		if (isEmail || isQQ || isTel || isIdentity) {
	 * 			return true;
	 * 		} else {
	 * 			return false;
	 * 		}
	 * </pre>
	 * 
	 * @param usr
	 * @return
	 */
	@RequestMapping("/step1")
	@ResponseBody
	public ActionResult step1(HttpServletRequest request, String username) {
		Assert.notNull(username, "username is required.");
		UsrAccount account = userService.findAccount(username);
		if (account == null) {
			return ActionResult.errorResult("用户不存在");
		} else {
			String uuid = account.getAccountId().toString();
			String challenge = loginService.addChallenge(uuid);
			return ActionResult.successResult(new HmacChallenge(uuid, challenge));
		}
	}

	@RequestMapping("/submit")
	@ResponseBody
	public ActionResult login(HttpServletRequest request, HttpServletResponse response, String username,
			String password, Boolean autoLogin) {
		UsrAccount account = userService.findAccount(username);
		String pwd = account.getPwd();
		if (pwd.equals(password)) {
			LoginResult loginResult = doLogin(request, response, account, autoLogin);
			loginResult.username = username;
			return ActionResult.successResult(loginResult);
		} else {
			return ActionResult.errorResult("密码错误");
		}
	}

	@RequestMapping("/step2")
	@ResponseBody
	public ActionResult step2(HttpServletRequest request, HttpServletResponse response, String username, String userId,
			String answer, Boolean autoLogin) {
		UUID uuid = UUID.fromString(userId);
		String challenge = loginService.removeChallenge(userId);
		UsrAccount account = userDao.find(UsrAccount.class, uuid);
		String password = account.getPwd();
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte[] key = password.getBytes("UTF-8");
			SecretKey secret = new SecretKeySpec(key, "HMACSHA256");
			mac.init(secret);
			byte[] expectBytes = mac.doFinal(challenge.getBytes("UTF-8"));
			byte[] answerBtyes = Base64.decodeBase64(answer);
			if (Arrays.equals(answerBtyes, expectBytes)) {
				LoginResult loginResult = doLogin(request, response, account, autoLogin);
				loginResult.username = username;
				return ActionResult.successResult(loginResult);
			} else {
				return ActionResult.errorResult("密码错误");
			}
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Hmac algorithm should be available at server.", e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("illegal state! utf8 bytes not supported.", e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException("InvalidKeyException in login init hmac secret", e);
		}

	}

	private LoginResult doLogin(HttpServletRequest request, HttpServletResponse response, UsrAccount account,
			Boolean autoLogin) {
		long expireTime = -1;
		int maxAge = -1;
		if (BooleanUtils.isTrue(autoLogin)) {
			expireTime = DateTime.now().plusMonths(3).getMillis();
			maxAge = (int) ((expireTime - System.currentTimeMillis()) / 1000);
		}
		UsrSession userContext = loginService.createUserSession(account.getAccountId(), expireTime, request);
		String accessToken = userContext.getAccessToken();
		Cookie sessionCookie = new Cookie("ACCESS_TOKEN", accessToken);
		sessionCookie.setHttpOnly(true);
		sessionCookie.setMaxAge(maxAge);
		sessionCookie.setPath(this.cookiePath);
		response.addCookie(sessionCookie);

		Cookie flagCookie = new Cookie("AUTH-FLAG", "true");
		// 这个cookie要让JS能读取到
		flagCookie.setHttpOnly(false);
		flagCookie.setMaxAge(maxAge);
		flagCookie.setPath(this.cookiePath);
		response.addCookie(flagCookie);
		
		shiroAccessTokenRealm.addLoginSession(userContext);
		Subject subject = SecurityUtils.getSubject();
		AuthenticationToken token = new AccessToken(accessToken, request);
		subject.login(token);
		
		//set cookie 不允许缓存
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setDateHeader("Expires", 0);
		
		LoginResult loginResult = new LoginResult();
		loginResult.userId = account.getAccountId().toString();
		Set<String> roles = userDao.findRoles(account.getAccountId());
		loginResult.roles = Joiner.on(',').join(roles);
		loginResult.authenticated = true;
//		loginResult.logout = false;
		loginResult.expireTime = userContext.getExpireTime();
		return loginResult;
	}

	@Data
	public static class LoginResult {
		public boolean authenticated;
//		public boolean logout = false;
		public String username;
		public String userId;
		public Timestamp expireTime;
		public String roles;
		public String role;
	}

	public static class HmacChallenge {
		public String userId;
		public String challenge;

		HmacChallenge(String userId, String challenge) {
			this.userId = userId;
			this.challenge = challenge;
		}
	}
}
