package me.suisui.web.shiro;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import me.suisui.data.domain.user.ShiroPrincipal;
import me.suisui.data.jdbc.po.pub.UsrAccount;
import me.suisui.data.jdbc.po.pub.UsrSession;
import me.suisui.domain.login.LoginService;
import me.suisui.repo.jdbc.dao.pub.UserDao;
import me.suisui.web.shiro.ShiroAccessTokenFilter.AccessToken;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

public class ShiroAccessTokenRealm extends AuthorizingRealm {
	@Autowired(required=false)
	LoginService loginService;

	@Autowired(required=false)
	UserDao userDao;

	//登录成功的时候把值临时存放在这里
	HashMap<String, UsrSession> oneTimeLoginSessions = Maps.newHashMap();
	
	@Override
	public boolean supports(AuthenticationToken token) {
		return token != null && token instanceof AccessToken;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		ShiroPrincipal principal = (ShiroPrincipal) getAvailablePrincipal(principals);
		UsrAccount account = principal.getAccount();
		Set<String> roleNames = userDao.findRoles(account.getAccountId());
		Set<String> permissions = userDao.findPermissions(account.getAccountId(), roleNames);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
		info.setStringPermissions(permissions);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		AccessToken t = (AccessToken) token;
		//这个session是登录成功的时候存放过来的。
		UsrSession userSession = oneTimeLoginSessions.remove(t.getAccessToken());
		
		if(userSession == null){
			userSession = loginService.createUserSession(t.getAccessToken(), t.getRequest());
			if (userSession == null) {
				//没有创建成功，说明是无效或者过期的token
				return null;
			}
		}

		UUID accountId = userSession.getAccountId();
		UsrAccount account = userDao.find(UsrAccount.class, accountId);
		ShiroPrincipal principal = new ShiroPrincipal(account, userSession);
		return new SimpleAuthenticationInfo(principal, t.getAccessToken(), getName());
	}
	
	public void addLoginSession(UsrSession usrSession) {
		oneTimeLoginSessions.put(usrSession.getAccessToken(), usrSession);
	}

}
