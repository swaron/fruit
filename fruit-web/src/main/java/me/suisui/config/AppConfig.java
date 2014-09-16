package me.suisui.config;

import java.util.Map;

import me.suisui.domain.config.DomainConfig;
import me.suisui.integration.config.SupportConfig;
import me.suisui.repo.config.CacheConfig;
import me.suisui.repo.config.RepoConfig;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.google.common.collect.Maps;

@Configuration
@PropertySource("classpath:app.properties")
@ImportResource({ "classpath:/META-INF/spring/app-context.xml" })
@Import({ SupportConfig.class, RepoConfig.class, DomainConfig.class, MvcConfig.class, CacheConfig.class,
		ShiroConfig.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

	@Autowired
	Environment env;

	/**
	 * used with @Value annotation
	 * http://stackoverflow.com/questions/13728000/value
	 * -not-resolved-when-using-propertysource-annotation-how-to-configure-prop
	 * 
	 * @return
	 */
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//		return new PropertySourcesPlaceholderConfigurer();
//	}

	@Bean(name = "envProps")
	public Map<String, String> envProps() {
		Map<String, String> prop = Maps.newHashMap();
		prop.put("spring.profiles.active", StringUtils.join(env.getActiveProfiles(), ','));
		return prop;
	}
}
