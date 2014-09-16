package me.suisui.config;

import java.util.Map;

import javax.annotation.Resource;

import me.suisui.integration.shiro.cache.SpringCacheManager;
import me.suisui.web.shiro.*;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Configuration
@Component
public class ShiroConfig {

	@Resource(name = "guavaCacheProperties")
	private Map<String, String> guavaCacheProperties;

	@Bean(name = "guavaCacheProperties")
	public PropertiesFactoryBean guavaCacheProperties() {
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation(new ClassPathResource("/META-INF/cache/guava-cache.properties"));
		return factory;
	}

	@Bean
	public ShiroAccessTokenFilter shiroAccessTokenFilter() {
		ShiroAccessTokenFilter filter = new ShiroAccessTokenFilter();
		return filter;
	}

	/**
	 * 从方法端通过annotation来验证权限
	 * 
	 * @return
	 */
	@Bean
	public ShiroFilterFactoryBean shiroFilter(CacheManager springCacheManager) {
		ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
		factoryBean.setSecurityManager(securityManager(springCacheManager));
		factoryBean.setLoginUrl("/page/login.html");
		factoryBean.setSuccessUrl("/");
		factoryBean.setUnauthorizedUrl("/page/user/unauthorized.html");
		Map<String, String> filterMap = Maps.newLinkedHashMap();
		filterMap.put("/static/**", DefaultFilter.anon.name());
		filterMap.put("/page/logout.html", DefaultFilter.logout.name());
		filterMap.put("/page/user/**", DefaultFilter.anon.name());
		filterMap.put("/page/pub/**", DefaultFilter.anon.name());
		filterMap.put("/page/**", "shiroAccessTokenFilter");
//		filterMap.put("/admin/system/**", "shiroAccessTokenFilter, authc, perms[system]");//perms[document:read]
		filterMap.put("/admin/**", "shiroAccessTokenFilter,authc, roles[admin]");//perms[document:read]
		filterMap.put("/**", DefaultFilter.anon.name());
		factoryBean.setFilterChainDefinitionMap(filterMap);
		return factoryBean;
	}

	@Bean
	public WebSecurityManager securityManager(CacheManager springCacheManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(realm());
		securityManager.setSessionManager(sessionManager());
		if (springCacheManager != null) {
			securityManager.setCacheManager(new SpringCacheManager(springCacheManager));
		}
		securityManager.setRealm(realm());
		//用rememberme来处理auth_token和auth_flag
		securityManager.setRememberMeManager(rememberMeManager());
		SecurityUtils.setSecurityManager(securityManager);
		return securityManager;
	}

	@Bean
	public RememberMeManager rememberMeManager() {
		RememberMeManager manager = new ShiroRememberMeManager();
		return manager;
	}

	/**
	 * 不能用了， @Configuration 不处理@Autowired
	 */
	@Autowired(required = false)
	private org.springframework.cache.CacheManager springCacheManager;

	@Bean
	public SessionManager sessionManager() {
		ShiroWebSessionManager sessionManager = new ShiroWebSessionManager();
		sessionManager.setGlobalSessionTimeout(AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT);
		sessionManager.setSessionDAO(sessionDAO());
		// guava cache will invalid expired entry
		sessionManager.setSessionValidationSchedulerEnabled(false);
		// Cookie sessionIdCookie = new SimpleCookie("ACCESS_TOKEN");
		// sessionIdCookie.setHttpOnly(true); // more secure, protects against
		// XSS
		// // attacks
		// sessionManager.setSessionIdCookie(sessionIdCookie);
		return sessionManager;
	}

	@Bean
	public SessionDAO sessionDAO() {
		return new ShiroSessionRepository();
	}

	@Bean
	public Realm realm() {
		ShiroAccessTokenRealm jdbcRealm = new ShiroAccessTokenRealm();
		return jdbcRealm;
	}

	@Bean
	public LifecycleBeanPostProcessor shiroLifecycleBeanPostProcessor() {
		return new org.apache.shiro.spring.LifecycleBeanPostProcessor();
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		return new AuthorizationAttributeSourceAdvisor();
	}
}
