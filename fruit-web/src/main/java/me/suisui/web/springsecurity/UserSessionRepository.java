package me.suisui.web.springsecurity;

import javax.servlet.http.HttpServletRequest;

import me.suisui.framework.http.WebSession;

public interface UserSessionRepository {
	public WebSession getUserSession(HttpServletRequest request, boolean create);

}
