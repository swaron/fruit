package me.suisui.web.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

public class AppConsole {
	public static boolean intercept = false;
	
	private static boolean redirect = false;
	private static String redirectUrl = null;
	private static DateTime redirectStart = DateTime.now();
	private static DateTime redirectEnd = DateTime.now();

	private static boolean notify = false;
	private static String notifyContent = "";
	// 1. 提示
	// 2. 重定向
	public static void redirectToPage(boolean redirect, String url, String start, String end) {
		if(redirect){
			redirectStart = DateTime.parse(start);
			redirectEnd = DateTime.parse(end);
			redirectUrl = url;
			AppConsole.redirect = redirect;
		}else{
			redirect = false;
		}
	}

	public static void doIntercept(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getRequestURI();
		if(redirect && !path.contains("/admin/") && !redirectUrl.contains(path) ){
			if(redirectUrl != null && redirectStart.isBeforeNow() && redirectEnd.isAfterNow()){
				response.sendRedirect(redirectUrl);
			}
		}
		
	}
}
