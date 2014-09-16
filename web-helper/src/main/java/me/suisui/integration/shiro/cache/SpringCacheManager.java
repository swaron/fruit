package me.suisui.integration.shiro.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.guava.GuavaCache;

public class SpringCacheManager extends AbstractCacheManager {
	private CacheManager cacheManager;

	public SpringCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	protected org.apache.shiro.cache.Cache<Object, Object> createCache(String name) throws CacheException {
		org.springframework.cache.Cache cache = cacheManager.getCache(name);
		return new DelegateCache(cache);
	}

	public static class DelegateCache implements org.apache.shiro.cache.Cache<Object, Object> {
		private org.springframework.cache.Cache cache;

		public Cache getDelegate() {
			return cache;
		}

		public DelegateCache(org.springframework.cache.Cache cache) {
			this.cache = cache;
		}

		@Override
		public Object get(Object key) throws CacheException {
			return cache.get(key, Object.class);
		}

		@Override
		public Object put(Object key, Object value) throws CacheException {
			cache.put(key, value);
			return value;
		}

		@Override
		public Object remove(Object key) throws CacheException {
			Object value = cache.get(key, Object.class);
			cache.evict(key);
			return value;
		}

		@Override
		public void clear() throws CacheException {
			cache.clear();
		}

		@Override
		public int size() {
			if (cache instanceof GuavaCache) {
				return (int) ((GuavaCache) cache).getNativeCache().size();
			} else if (cache instanceof ConcurrentMapCache) {
				return ((ConcurrentMapCache) cache).getNativeCache().size();
			} else if (cache instanceof EhCacheCache) {
				// Ehcache ehcache = ((EhCacheCache) cache).getNativeCache() ;
			}
			return -1;
		}

		@Override
		public Set<Object> keys() {
			if (cache instanceof GuavaCache) {
				return ((GuavaCache) cache).getNativeCache().asMap().keySet();
			} else if (cache instanceof ConcurrentMapCache) {
				return ((ConcurrentMapCache) cache).getNativeCache().keySet();
			} else if (cache instanceof EhCacheCache) {
				// Ehcache ehcache = ((EhCacheCache) cache).getNativeCache() ;
			}
			return null;
		}

		@Override
		public Collection<Object> values() {
			if (cache instanceof GuavaCache) {
				return ((GuavaCache) cache).getNativeCache().asMap().values();
			} else if (cache instanceof ConcurrentMapCache) {
				return ((ConcurrentMapCache) cache).getNativeCache().values();
			} else if (cache instanceof EhCacheCache) {
				// Ehcache ehcache = ((EhCacheCache) cache).getNativeCache() ;
			}
			return null;
		}
	}
}
