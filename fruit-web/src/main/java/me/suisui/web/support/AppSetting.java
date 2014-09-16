package me.suisui.web.support;

import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.CharSetUtils;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.util.EncodingUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.google.common.base.Charsets;

@Component
@Lazy(false)
public class AppSetting {

	@Autowired(required = false)
	ServletContext servletContext;

	@Autowired(required = false)
	RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Autowired(required = false)
	List<AbstractHandlerMapping> handlerMappings;

	private String initAppVersion;

	public AppSetting() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		DateTimeZone.forID("Asia/Shanghai");
	}
	
	@PostConstruct
	public void initApp() {
		if (servletContext != null) {
			initWebApp();
		}
	}
	public String getInitAppVersion() {
		return this.initAppVersion;
	}

	public void initWebApp() {
		String appVersion = servletContext.getInitParameter("appVersion");
		if (!StringUtils.hasText(appVersion) || appVersion.equals("${appVersion}")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			appVersion = format.format(new Date());
		}
		servletContext.setAttribute("appVersion", appVersion);
		initAppVersion = appVersion;
		// 10秒钟还连不上数据库的话就报错
		DriverManager.setLoginTimeout(10);

		/**
		 * 设置 url找不到mapping时的默认mapping
		 */
//		if (requestMappingHandlerMapping != null) {
//			requestMappingHandlerMapping.setDefaultHandler(defaultHandler());
//		}

		/**
		 * 设置 AlwaysUseFullPath = true， 这样的mapping比较直观。
		 */
		if (handlerMappings != null) {
			for (AbstractHandlerMapping handlerMapping : handlerMappings) {
				handlerMapping.getUrlPathHelper().setDefaultEncoding(Charsets.UTF_8.name());
				handlerMapping.setAlwaysUseFullPath(true);
			}
		}
	}

	@Bean
	public UrlFilenameViewController defaultHandler() {
		UrlFilenameViewController defaultHandler = new UrlFilenameViewController();
		return defaultHandler;
	}
}
