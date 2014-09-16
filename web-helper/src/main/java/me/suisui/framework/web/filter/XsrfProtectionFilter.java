package me.suisui.framework.web.filter;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

/**
 * we set a UID cookie to identify a user.
 * 
 * @author swaron
 * 
 */

public class XsrfProtectionFilter implements Filter {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public static final String HEADER_NAME = "X-XSRF-TOKEN";
	public static final String COOKIE_NAME = "XSRF-TOKEN";
	private String pageExtension;
	private String jsonExtension;

	// String domain;

	public XsrfProtectionFilter() {
		// addRequiredProperty("domain");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		pageExtension = filterConfig.getInitParameter("pageExtension");
		jsonExtension = filterConfig.getInitParameter("jsonExtension");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			
			String fileName = WebUtils.extractFullFilenameFromUrlPath(httpRequest.getRequestURI());
			if(fileName.equals("") || fileName.endsWith(pageExtension)){
				//主页面，如果cookie不存在，则添加
				Cookie cookie = WebUtils.getCookie(httpRequest, COOKIE_NAME);
				if(cookie == null){
					String sessionId = WebUtils.getSessionId(httpRequest);
					if(sessionId == null){
						//匿名，没权限执行危险动作
						sessionId = "";
					}
					Cookie sessionCookie = new Cookie(COOKIE_NAME,  DigestUtils.md5Hex((sessionId + "salt" ).getBytes() )  );
//					sessionCookie.setPath("/");
					httpResponse.addCookie(sessionCookie);
				}
			}else if(fileName.endsWith(jsonExtension)){
				//没有一个地方为Json同意加no cache，这边顺便加一下吧
				httpResponse.setHeader("Cache-Control", "no-cache"); //HTTP 1.1    
				String header = httpRequest.getHeader(HEADER_NAME);
				Cookie cookie = WebUtils.getCookie(httpRequest, COOKIE_NAME);
				if(cookie == null){
					logger.warn("json请求里面没有xsrf的cookie，无法判断是否xsrf攻击。");
				}else if(header == null || !StringUtils.equals(header, cookie.getValue()) ){
					//2.xsrf
					throw new ServletException("检测到XSRF攻击，已被阻止。");
				}
			}
		}
		filterChain.doFilter(request, response);
	}

public static void main(String[] args) {
	String extractFullFilenameFromUrlPath = WebUtils.extractFullFilenameFromUrlPath("/asdf.html?sda=23&dsds=23#adf333");
	System.out.println(extractFullFilenameFromUrlPath);
}
	@Override
	public void destroy() {
	}
}
