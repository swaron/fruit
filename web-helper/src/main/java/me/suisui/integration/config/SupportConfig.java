package me.suisui.integration.config;

import me.suisui.integration.spring.jackson.CustomObjectMapper;
import me.suisui.integration.spring.jackson.CustomObjectMapperInjector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SupportConfig {
	@Bean
	public CustomObjectMapper customObjectMapper() {
		return new CustomObjectMapper();
	}
	
	@Bean
	public CustomObjectMapperInjector customObjectMapperInjector() {
		return new CustomObjectMapperInjector();
	}
}
