package me.suisui.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.OncePerRequestFilter;

public class EnsureStatelessFilter extends OncePerRequestFilter {

	public static class NoOpFilter implements Filter {
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
				ServletException {
			chain.doFilter(request, response);
		}

		@Override
		public void destroy() {
		}

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		StatelessRequestWrapper requestWrapper = new StatelessRequestWrapper(httpRequest);
		filterChain.doFilter(requestWrapper, response);
	}
}

class StatelessRequestWrapper extends HttpServletRequestWrapper {

	public StatelessRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException("HttpSession is not allowed in this stateless application.");
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (create == true) {
			throw new UnsupportedOperationException("HttpSession is not allowed in this stateless application.");
		} else {
			return null;
		}
	}
}
