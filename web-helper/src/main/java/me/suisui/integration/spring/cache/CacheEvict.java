package me.suisui.integration.spring.cache;

@org.springframework.cache.annotation.CacheEvict(value = { "" })
public @interface CacheEvict {
	Class<?> value();
}
