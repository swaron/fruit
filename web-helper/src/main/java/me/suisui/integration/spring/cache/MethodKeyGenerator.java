package me.suisui.integration.spring.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * the empty string and null are treated as the same.
 * 
 * generate cached key to be used by spring cache.
 * 
 * @author aaron
 * 
 */
public class MethodKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		return new MethodKey(method, params);
	}
}
