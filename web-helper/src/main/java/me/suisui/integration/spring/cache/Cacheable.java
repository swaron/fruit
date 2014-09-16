package me.suisui.integration.spring.cache;

@org.springframework.cache.annotation.Cacheable(value = { "" })
public @interface Cacheable {
	Class<?> value();
}
