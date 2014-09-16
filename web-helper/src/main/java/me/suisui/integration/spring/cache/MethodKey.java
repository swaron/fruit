package me.suisui.integration.spring.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MethodKey implements Serializable {
	private static final long serialVersionUID = 1008345741601274345L;

	// public static final MethodKey EMPTY = new MethodKey();

	private final Object[] params;

	/**
	 * Create a new {@link SimpleKey} instance.
	 * 
	 * @param elements
	 *            the elements of the key
	 */
	public MethodKey(Method method, Object... elements) {
		Assert.notNull(method, "method must not be null");
		this.params = new Object[elements.length + 1];
		this.params[0] = method;
		System.arraycopy(elements, 0, this.params, 1, elements.length);
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof MethodKey && Arrays.equals(this.params, ((MethodKey) obj).params)));
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.params);
	}

	@Override
	public String toString() {
		return "MethodKey [" + StringUtils.arrayToCommaDelimitedString(this.params) + "]";
	}

}
