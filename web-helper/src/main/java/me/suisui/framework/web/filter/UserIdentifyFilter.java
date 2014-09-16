package me.suisui.framework.web.filter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.suisui.framework.web.support.WebRequestUtils;

import org.apache.commons.codec.binary.Base64;

import com.google.common.net.InetAddresses;

/**
 * we set a UID cookie to identify a user.
 * 
 * @author swaron
 * 
 */

public class UserIdentifyFilter implements Filter {
	public static final String COOKIE_NAME = "UID";
	public String cookiePath = "/";
	// String domain;

	public UserIdentifyFilter() {
		// addRequiredProperty("domain");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
//		if(filterConfig.getServletContext().getContextPath().length() > 0 ){
//			cookiePath = filterConfig.getServletContext().getContextPath();
//		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			// check cookies
			// if exist :continue
			// if not exist: add to response
			String uid = WebRequestUtils.getUid(httpRequest);
			if (uid == null) {
				Cookie sessionCookie = new Cookie(COOKIE_NAME, createUserIdentity(httpRequest));
				sessionCookie.setMaxAge(2 * 365 * 24 * 3600 );
				sessionCookie.setPath("/");
				httpResponse.addCookie(sessionCookie);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String createUserIdentity(HttpServletRequest request) {
		String ipAddr = WebRequestUtils.getClientIpAddr(request);
		long now = System.currentTimeMillis();
		ByteBuffer buffer = ByteBuffer.allocate(20);
		int ipv4 = InetAddresses.coerceToInteger(InetAddresses.forString(ipAddr));
		buffer.putInt(ipv4);
		buffer.putLong(now);
		UUID randomUUID = UUID.randomUUID();
		buffer.putLong(randomUUID.getLeastSignificantBits());
		String uid = Base64.encodeBase64URLSafeString(buffer.array());
		return uid;

	}

	@Override
	public void destroy() {
	}
}
