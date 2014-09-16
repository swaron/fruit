package me.suisui.web.springsecurity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.suisui.framework.web.support.WebRequestUtils;

import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

/**
 * A {@code SecurityContextRepository} implementation which stores the security
 * context in the {@code UserSessionRepository} between requests.
 * <p>
 * 
 * @since 3.0
 */
public class StatelessSecurityContextRepository implements SecurityContextRepository {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected UserSessionRepository userSessionRepository;

	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		HttpServletRequest request = requestResponseHolder.getRequest();
		Session userSession = userSessionRepository.getUserSession(request, true);
		if (userSession != null) {
			Authentication authentication = new UsernamePasswordAuthenticationToken(userSession, userSession.getId(),
					AuthorityUtils.NO_AUTHORITIES);
			context.setAuthentication(authentication);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No SecurityContext was available in memory and db for token {}.",WebRequestUtils.getAccessToken(request));
			}
		}
		return context;
	}

	@Override
	public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = context.getAuthentication();
		//nothing todo 
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		throw new UnsupportedOperationException("not yet implemented.");
		// String accessToken = WebRequestUtils.getAccessToken(request);
		// UserSession userSession =
		// userSessionRepository.getUserSession(accessToken);
		// return userSession != null;
	}
}
