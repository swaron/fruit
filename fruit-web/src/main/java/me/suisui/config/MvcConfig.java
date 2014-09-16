package me.suisui.config;

import me.suisui.config.MvcConfig.ClassNameGenerator;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;

/**
 * extend directly from WebMvcConfigurationSupport to override methods as
 * necessary
 * 
 * @author swaron
 *
 */
@Configuration
@ComponentScan(basePackages = "me.suisui.web", nameGenerator = ClassNameGenerator.class)
@ImportResource({ "classpath:/META-INF/spring/mvc-context.xml" })
public class MvcConfig extends WebMvcConfigurationSupport {

	/**
	 * remove @bean annotation，没用的，bean是会继承的
	 */
	@Override
	public BeanNameUrlHandlerMapping beanNameHandlerMapping() {
		return super.beanNameHandlerMapping();
	}

	/**
	 * remove @bean annotation
	 */
	@Override
	public HandlerMapping viewControllerHandlerMapping() {
		return super.viewControllerHandlerMapping();
	}

	public static class ClassNameGenerator extends AnnotationBeanNameGenerator {
		@Override
		protected String buildDefaultBeanName(BeanDefinition definition) {
			return definition.getBeanClassName();
		}
	}
}
