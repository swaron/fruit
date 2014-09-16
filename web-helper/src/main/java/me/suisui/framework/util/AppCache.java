package me.suisui.framework.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
/**
 * 系统的Cache都是从这里创建的， 包括GuavaCacheManager 和 CacheableDao 和spring自定义Cache Annotation
 * @author swaron
 *
 */
public class AppCache {
	public static final String DEFAULT_SPEC_NAME = "default";
	public static Logger logger = LoggerFactory.getLogger(AppCache.class);
	public static Map<String, String> cacheBuilderSpecs = new HashMap<String, String>(0);
	public static Map<String, CacheLoader<Object, Object>> cacheLoaders = Maps.newHashMap();
	private static ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>(16);

	public static Cache getCache(String name){
		return cacheMap.get(name);
	}
	public static <K1 , V1> Cache<K1, V1>  createCache(String name,String spec){
		return createCache(name, spec, null);
	}
	public static <K1 , V1> Cache<K1, V1> createCache(String name,String spec,CacheLoader<Object, Object> cacheLoader){
		synchronized (cacheMap) {
			Cache cache = cacheMap.get(name);
			if(cache != null){
				logger.warn("duplicately create cache with same name.");
				return cache;
			}
			CacheBuilder<Object, Object> builder = CacheBuilder.from(spec);
			if (cacheLoader != null) {
				cache = builder.build(cacheLoader);
			} else {
				cache = builder.build();
			}
			cacheMap.put(name, cache);
			return cache;
		}
	}
	public static Set<String> getCacheNames() {
		return Collections.unmodifiableSet(cacheMap.keySet());
	}
}
