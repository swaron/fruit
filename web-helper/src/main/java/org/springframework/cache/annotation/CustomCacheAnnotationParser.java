package org.springframework.cache.annotation;

import java.lang.reflect.AnnotatedElement;

import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheableOperation;

public class CustomCacheAnnotationParser extends SpringCacheAnnotationParser {

	private static final long serialVersionUID = 5543947980164754181L;

	@Override
	protected CacheableOperation parseCacheableAnnotation(AnnotatedElement ae, Cacheable caching) {
		CacheableOperation cuo = super.parseCacheableAnnotation(ae, caching);
		if (caching instanceof me.suisui.integration.spring.cache.Cacheable) {
			Class<?> clazz = ((me.suisui.integration.spring.cache.Cacheable) caching).value();
			cuo.setCacheName(clazz.getName());
		}
		return cuo;
	}

	CacheEvictOperation parseEvictAnnotation(AnnotatedElement ae, CacheEvict caching) {
		CacheEvictOperation cuo = super.parseEvictAnnotation(ae, caching);
		if (caching instanceof me.suisui.integration.spring.cache.Cacheable) {
			Class<?> clazz = ((me.suisui.integration.spring.cache.Cacheable) caching).value();
			cuo.setCacheName(clazz.getName());
		}
		return cuo;
	}

	CacheOperation parseUpdateAnnotation(AnnotatedElement ae, CachePut caching) {
		CacheOperation cuo = super.parseUpdateAnnotation(ae, caching);
		if (caching instanceof me.suisui.integration.spring.cache.Cacheable) {
			Class<?> clazz = ((me.suisui.integration.spring.cache.Cacheable) caching).value();
			cuo.setCacheName(clazz.getName());
		}
		return cuo;
	}
}
