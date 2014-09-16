package me.suisui.integration.guava;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.DelayQueue;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.ForwardingCache;
import com.google.common.collect.Lists;

public class ExpiredCache<K, V> extends ForwardingCache<K, V> {

	private DelayQueue<ExpiredItem<K>> itemQueue = new DelayQueue<ExpiredItem<K>>();
	private final Cache<K, V> delegate;

	public ExpiredCache(Cache<K, V> delegate) {
		this.delegate = Preconditions.checkNotNull(delegate);
//		GuavaCacheManager.register(this);
	}

	@Override
	protected Cache<K, V> delegate() {
		return delegate;
	}

	@Override
	public V getIfPresent(Object key) {
		drainCache();
		return super.getIfPresent(key);
	}

	@Override
	public void invalidate(Object key) {
		super.invalidate(key);
		removeItem(key);
	}

	@Override
	public void invalidateAll() {
		super.invalidateAll();
		this.itemQueue.clear();
	}

	@Override
	public void invalidateAll(Iterable<?> keys) {
		for (Object key : keys) {
			invalidate(key);
		}
	}

	public void put(K key, V value, long expireTime) {
		removeItem(key);
		super.put(key, value);
		itemQueue.put(new ExpiredItem<K>(key, expireTime));
	}

	private void drainCache() {
		ExpiredItem<K> item;
		while ((item = itemQueue.poll()) != null) {
			K ref = item.getReference();
			if (ref != null) {
				delegate().invalidate(ref);
			}
		}
	}

	private void removeItem(Object key) {
		Iterator<ExpiredItem<K>> iterator = itemQueue.iterator();
		while (iterator.hasNext()) {
			ExpiredItem<K> expiredItem = (ExpiredItem<K>) iterator.next();
			if (key.equals(expiredItem.getReference())) {
				iterator.remove();
				return;
			}
		}
	}
}
