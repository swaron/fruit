package me.suisui.integration.guava;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

public class ExpiredItem<T> implements Delayed {
	private T reference;
	private long expireTime;

	public ExpiredItem(T reference, long expireTime) {
		Preconditions.checkNotNull(reference);
		this.reference = reference;
		this.expireTime = expireTime;
	}

	@Override
	public int compareTo(Delayed o) {
		ExpiredItem<?> other = (ExpiredItem<?>) o;
		return (int) (this.expireTime - other.expireTime);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long current = System.currentTimeMillis();
		long remain = expireTime - current;
		return unit.convert(remain, TimeUnit.MILLISECONDS);
	}

	public T getReference() {
		return reference;
	}

	/**
	 * compare only value field. for used on remove .
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return this.reference.equals(obj);
		} else {
			@SuppressWarnings("unchecked")
			ExpiredItem<T> item = (ExpiredItem<T>) obj;
			return this.reference.equals(item.reference);
		}
	}
}
