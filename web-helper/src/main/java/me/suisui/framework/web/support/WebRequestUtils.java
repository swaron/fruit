package me.suisui.framework.web.support;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public abstract class WebRequestUtils {
	public static Map<String, String> getParametersMap(HttpServletRequest request) {
		@SuppressWarnings("unchecked")
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String> params = new TreeMap<String, String>();
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] values = request.getParameterValues(paramName);
			params.put(paramName, processValue(paramName, values));
		}
		return params;
	}

	public static String processValue(String name, String[] values) {
		String[] sensitiveNames = { "answer", "password", "creditcard" };
		for (String it : sensitiveNames) {
			if (name.toLowerCase().contains(it)) {
				return StringUtils.repeat("*", it.length());
			}
		}
		return Arrays.toString(values);
	}

	public static void setContentDispositionHeader(HttpServletRequest request, HttpServletResponse response,
			String filename) throws UnsupportedEncodingException {
		if (request != null && isBelowInternetExplorer8(request)) {
			String encoded = URLEncoder.encode(filename, "UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + encoded + "\"");
		} else {
			response.setHeader("Content-Disposition", MimeHelper.encodeContentDisposition("attachment", filename));
		}
	}

	private static boolean isBelowInternetExplorer8(HttpServletRequest request) {
		String userAgentHeader = request.getHeader("User-Agent");
		if (userAgentHeader == null) {
			return false;
		}
		int ieStartIndex = userAgentHeader.toLowerCase().indexOf("msie");
		if (ieStartIndex == -1) {
			return false;
		}
		int ieEndIndex = userAgentHeader.indexOf(";", ieStartIndex + "msie".length());
		if (ieEndIndex == -1) {
			return false;
		}
		String versionString = userAgentHeader.substring(ieStartIndex + "msie".length(), ieEndIndex).trim();
		try {
			Float version = Float.parseFloat(versionString);
			return version < 9;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String getUid(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("UID".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static String getAccessToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("ACCESS_TOKEN".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		String header = request.getHeader("ACCESS_TOKEN");
		if (header != null) {
			return header;
		}
		String parameter = request.getParameter("ACCESS_TOKEN");
		if (parameter != null) {
			return parameter;
		}
		return null;
	}

	public static String getClientIpAddr(HttpServletRequest request) {
		String forwordFor = request.getHeader("X-Forwarded-For");
		// no forward is the simplest case, remote address is client address
		if (forwordFor == null) {
			return request.getRemoteAddr();
		}
		String forword = StringUtils.substringBefore(forwordFor, ",");
		return forword;
	}
}
