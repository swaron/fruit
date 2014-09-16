package me.suisui.web.shiro;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

public class ShiroRememberMeManager  implements RememberMeManager {
	private Cookie accessToken;
	private Cookie authFlag;

	public ShiroRememberMeManager() {
		Cookie cookie = new SimpleCookie("ACCESS_TOKEN");
        cookie.setHttpOnly(true);
        this.accessToken = cookie;
        this.authFlag = new SimpleCookie("AUTH-FLAG");
	}
	
	@Override
	public PrincipalCollection getRememberedPrincipals(SubjectContext subjectContext) {
		//remember是通过access_token 完成的
		return null;
	}

	@Override
	public void forgetIdentity(SubjectContext subjectContext) {
		//登录rememberme token 失败
		if (WebUtils.isHttp(subjectContext)) {
            HttpServletRequest request = WebUtils.getHttpRequest(subjectContext);
            HttpServletResponse response = WebUtils.getHttpResponse(subjectContext);
            accessToken.removeFrom(request, response);
            authFlag.removeFrom(request, response);
        }
	}

	@Override
	public void onSuccessfulLogin(Subject subject, AuthenticationToken token, AuthenticationInfo info) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailedLogin(Subject subject, AuthenticationToken token, AuthenticationException ae) {
		if (WebUtils.isHttp(subject)) {
			HttpServletRequest request = WebUtils.getHttpRequest(subject);
			HttpServletResponse response = WebUtils.getHttpResponse(subject);
			accessToken.removeFrom(request, response);
			authFlag.removeFrom(request, response);
		}
	}

	@Override
	public void onLogout(Subject subject) {
		if (WebUtils.isHttp(subject)) {
			HttpServletRequest request = WebUtils.getHttpRequest(subject);
			HttpServletResponse response = WebUtils.getHttpResponse(subject);
			accessToken.removeFrom(request, response);
			authFlag.removeFrom(request, response);
		}
	}
	

}
