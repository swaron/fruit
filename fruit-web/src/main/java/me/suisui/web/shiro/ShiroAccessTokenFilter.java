package me.suisui.web.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import me.suisui.domain.web.WebRequestService;
import me.suisui.framework.web.support.WebRequestUtils;
import me.suisui.web.support.AppConsole;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ShiroAccessTokenFilter extends PathMatchingFilter {
	Logger logger = LoggerFactory.getLogger(getClass());

	WebRequestService webRequestService = new WebRequestService();

	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		if (isAuthenticated(request, response, mappedValue)) {
			return true;
		}
		attemptLoginWithAccessToken(request, response);

		// always return true to procceed
		return true;
	}

	private void attemptLoginWithAccessToken(ServletRequest request, ServletResponse response) {
		Subject subject = getSubject(request, response);
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String accessToken = WebRequestUtils.getAccessToken(httpServletRequest);
		if (accessToken != null) {
			AuthenticationToken token = new AccessToken(accessToken,httpServletRequest);
			if(logger.isDebugEnabled()){
				String ipAddr = webRequestService.getClientIpAddr(httpServletRequest);
				logger.debug("attempt to login in for {} with access token {}.", ipAddr, accessToken);
			}
			try {
				subject.login(token);
			} catch (AuthenticationException e) {
				logger.debug("failed to login with access token {}, ignore this access token.", accessToken);
			}
		}
	}

	protected boolean isAuthenticated(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);
		return subject.isAuthenticated();
	}

	protected Subject getSubject(ServletRequest request, ServletResponse response) {
		return SecurityUtils.getSubject();
	}

	public static class AccessToken implements AuthenticationToken {
		private static final long serialVersionUID = 1L;
		String accessToken;
		HttpServletRequest request;

		public AccessToken(String accessToken, HttpServletRequest request) {
			this.request = request;
			this.accessToken = accessToken;
		}

		public String getAccessToken() {
			return accessToken;
		}
		public HttpServletRequest getRequest() {
			return request;
		}
		
		@Override
		public Object getPrincipal() {
			return accessToken;
		}

		@Override
		public Object getCredentials() {
			return accessToken;
		}
	}
}
