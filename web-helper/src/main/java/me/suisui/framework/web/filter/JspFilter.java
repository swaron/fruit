package me.suisui.framework.web.filter;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.servlet.view.JstlView;

/**
 * 目前jsp生成的内容只和这些变量有关： 1. contextPath ： 不会变 2. appVersion:
 * 用启动时间来控制。理想是最好一个文件提交时间 3. debug变量和profile环境。 产品debug只在url参数上，profile不会变
 * 
 * 
 * @author swaron
 *
 */
public class JspFilter implements Filter {
	ServletContextResourceLoader resourceLoader;

	/**
	 * 版本控制js和css，如果版本变了，jsp文件没变。输出的内容还是要变。
	 */
	private static long startUpTime = System.currentTimeMillis();

	private String[] excludes = { "/jsp/","/admin/" };

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		resourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			String servletPath = httpServletRequest.getServletPath();
			boolean ignore = false;
			for (String path : excludes) {
				if (servletPath.startsWith(path)) {
					ignore = true;
				}
			}
			Resource resource = resourceLoader.getResource(servletPath);
			if (resource != null && !ignore) {
				long lastModified = Math.max(resource.lastModified(), startUpTime);
				if (checkNotModified(httpServletRequest, httpServletResponse, lastModified)) {
					return;
				}
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

	/**
	 * return true if not modified。 1. 用等于来判断 modified 2. 设置last-modified的同时设置
	 * max-age，不设置max-age会引起问题，不同浏览器会有不同的默认max-age。
	 * 
	 * @param request
	 * @param response
	 * @param lastModifiedTimestamp
	 * @return
	 */
	public boolean checkNotModified(HttpServletRequest request, HttpServletResponse response, long lastModifiedTimestamp) {
		lastModifiedTimestamp = lastModifiedTimestamp / 1000 * 1000;
		String method = request.getMethod();
		boolean notModified = false;
		boolean eligible = lastModifiedTimestamp >= 0 && response != null && !response.containsHeader("Last-Modified")
				&& !response.containsHeader("Cache-Control");
		if (eligible) {
			long ifModifiedSince = request.getDateHeader("If-Modified-Since");
			// 特意用等于号来判断，所以才不用spring的ServletWebRequest的方法
			notModified = (ifModifiedSince == (lastModifiedTimestamp));
			if (notModified && ("GET".equals(method) || "HEAD".equals(method))) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			} else {
				response.setDateHeader("Last-Modified", lastModifiedTimestamp);
				response.setHeader("Cache-Control", "max-age=60");
			}
		}
		return notModified;
	}

	public static void setStartUpTime(long startUpTime) {
		JspFilter.startUpTime = startUpTime;
	}
}
