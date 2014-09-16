package me.suisui.web.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;

import me.suisui.data.jdbc.po.pub.UsrAccount;
import me.suisui.framework.repo.jdbc.BeanRowMapper;
import me.suisui.framework.web.result.ActionResult;
import me.suisui.web.BaseWebTest;
import me.suisui.web.page.LoginPage;
import me.suisui.web.page.LoginPage.HmacChallenge;
import me.suisui.web.page.LoginPage.LoginResult;
import me.suisui.web.shiro.ShiroAccessTokenRealm;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jayway.jsonpath.JsonPath;

public class LoginResourceTest extends BaseWebTest {

	@Autowired
	LoginPage loginResource;
	@Autowired
	WebSecurityManager securityManager;
	@Autowired
	ShiroAccessTokenRealm shiroAccessTokenRealm;
	
	@PostConstruct
	public void init() {
		SecurityUtils.setSecurityManager(securityManager);
	}
	
	@Test
	public void testUserId() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/page/login/step1.json")
						.accept(MediaType.parseMediaType("application/json;charset=UTF-8"))
						.param("username", "hy@school.com")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.result.userId").value("0d815992-77e1-1031-b86f-33a1d2f6c72f"))
				.andExpect(jsonPath("$.result.challenge").value(NotNull.NOT_NULL));
	}

	@Test
	public void testStep2() throws NoSuchAlgorithmException, InvalidKeyException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		ActionResult actionResult = loginResource.step1(request, "hy@school.com");
		HmacChallenge challenge = (HmacChallenge) actionResult.getResult();
		UsrAccount account = jdbcTemplate.queryForObject("select * from usr_account where email = ?",
				BeanRowMapper.get(UsrAccount.class), "hy@school.com");
		SecretKeySpec keySpec = new SecretKeySpec(account.getPwd().getBytes(), "HmacSHA256");

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		byte[] result = mac.doFinal(challenge.challenge.getBytes());
		String answer = Base64.encodeBase64URLSafeString(result);
		
		ActionResult actionResult2 = loginResource.step2(request, response,"hy@school.com", "0d815992-77e1-1031-b86f-33a1d2f6c72f",
				answer,true);
		LoginResult loginResult = (LoginResult) actionResult2.getResult();
		Cookie cookie = response.getCookie("ACCESS_TOKEN");
		assertThat(cookie, Matchers.notNullValue());
		Assert.assertTrue(actionResult2.isSuccess());
		jdbcTemplate.update("delete from usr_session where access_token = ?", cookie.getValue());
	}

	@Test
	public void testLogin() throws Exception {
		// pwd is sha encoded
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/page/login/submit.json")
				.accept(MediaType.parseMediaType("application/json;charset=UTF-8")).param("username", "hy@school.com")
				.param("password", "7e15093b4a26cc9f9ac2221a2d32eee721a5fdcec3e90d1c9f4a1b4d474f9659");
		MvcResult result = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.result.userId");
		String accessToken = JsonPath.read(result.getResponse().getContentAsString(), "$.result.accessToken");

		assertThat(userId, Matchers.is("0d815992-77e1-1031-b86f-33a1d2f6c72f"));
		assertThat("not expose token for security issue", accessToken, Matchers.nullValue());
		jdbcTemplate.update("delete from usr_session where access_token = ?", accessToken);
	}

}
