package me.suisui.integration.spring.cache;

import java.util.HashMap;
import java.util.Map;

import me.suisui.framework.util.AppCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;

public class GuavaCacheManager extends org.springframework.cache.guava.GuavaCacheManager {
	public static final String DEFAULT_SPEC_NAME = "default";
	Logger logger = LoggerFactory.getLogger(getClass());
	Map<String, String> cacheBuilderSpecs = new HashMap<String, String>(0);
	Map<String, CacheLoader<Object, Object>> cacheLoaders = Maps.newHashMap();

	@Override
	protected Cache<Object, Object> createNativeGuavaCache(String name) {
		String spec = cacheBuilderSpecs(name);
		CacheLoader<Object, Object> cacheLoader = cacheLoaders.get(name);
		logger.debug("create guava cache for {} with spec:{}.", name, spec);
		return AppCache.createCache(name, spec, cacheLoader);
	}

	public String cacheBuilderSpecs(String name) {
		String spec = cacheBuilderSpecs.get(name);
		if (spec == null) {
			spec = cacheBuilderSpecs.get(DEFAULT_SPEC_NAME);
		}
		return spec;
	}

	public void setCacheBuilderSpecs(Map<String, String> cacheBuilderSpecs) {
		Assert.notNull(cacheBuilderSpecs, "null specs is not allowed.");
		this.cacheBuilderSpecs = cacheBuilderSpecs;
	}
}
