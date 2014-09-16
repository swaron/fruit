package me.suisui.integration.spring.cache;

@org.springframework.cache.annotation.CachePut(value = { "" })
public @interface CachePut {
	Class<?> value();
}
