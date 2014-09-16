package me.suisui.web.springsecurity;

import javax.servlet.http.Cookie;

import junit.framework.Assert;
import me.suisui.framework.http.WebSession;
import me.suisui.web.BaseWebTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

public class UserSessionRepositoryImplTest extends BaseWebTest {

	@Autowired
	UserSessionRepositoryImpl userSessionRepositoryImpl;

	@Test
	public void test() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		Cookie cookies = new Cookie("ACCESS_TOKEN", "DYFZknfhEDG4bzOh0vbHLwAAAUabVgq9hgxPNVfiTc6m_Jt2KKeX7Q");
		request.setCookies(cookies);
		WebSession userSession = userSessionRepositoryImpl.getUserSession(request, false);
		Assert.assertNull("context in db, create is false.", userSession);
		userSession = userSessionRepositoryImpl.getUserSession(request, true);
		Assert.assertNotNull("session should be created.", userSession);
		userSession = userSessionRepositoryImpl.getUserSession(request, false);
		Assert.assertNotNull("session should be in memory.", userSession);
	}

}
