package me.suisui.integration.spring.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

public class GuavaCache implements Cache {
	private final String name;
	private final com.google.common.cache.Cache<Object, Object> cache;

	/**
	 * Create a {@link GuavaCache} instance.
	 * @param name the name of the cache
	 * @param cache backing Guava Cache instance
	 */
	public GuavaCache(String name, com.google.common.cache.Cache<Object, Object> cache) {
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(cache, "Cache must not be null");
		this.name = name;
		this.cache = cache;
	}


	@Override
	public final String getName() {
		return this.name;
	}

	@Override
	public final com.google.common.cache.Cache<Object, Object> getNativeCache() {
		return this.cache;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object value = this.cache.getIfPresent(key);
		return (value != null ? new SimpleValueWrapper(value) : null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = this.cache.getIfPresent(key);
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
		}
		return (T) value;
	}

	@Override
	public void put(Object key, Object value) {
		this.cache.put(key, value);
	}

	@Override
	public void evict(Object key) {
		this.cache.invalidate(key);
	}

	@Override
	public void clear() {
		this.cache.invalidateAll();
	}


}