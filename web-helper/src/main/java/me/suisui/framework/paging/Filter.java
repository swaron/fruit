package me.suisui.framework.paging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang3.StringUtils;

public class Filter {

	public static final String[] ALL_OPERATOR = { "<", "=", ">", "<=", ">=", "<>", "!=", "is null", "is not null",
			"in", "not in", "like" };
	public static String OPERATOR_NOT_NULL = "is not null";
	public static String OPERATOR_NULL = "is null";

	String property;
	Object value;
	String operator = "=";

	/**
	 * 抽取value里面的比较符号
	 */
	public void normalize() {
		if (value == null) {
			return;
		}
		if (StringUtils.isNotEmpty(this.operator) && value instanceof String) {
			String stringValue = (String) value;
			if (StringUtils.containsAny(stringValue, "!<>=")) {
				// pattern: >= 12, <=12, =12
				Pattern pattern = Pattern.compile("([!<>=]+)\\s?(.+)");
				Matcher matcher = pattern.matcher(stringValue);
				if (!matcher.matches() || matcher.groupCount() < 2) {
					// 没有值，不处理
				} else {
					String parseOperator = matcher.group(1);
					String parseValue = matcher.group(2);
					this.value = parseValue;
					this.operator = parseOperator;
				}
			}
		}
		if (StringUtils.isEmpty(this.operator)) {
			this.operator = "=";
		}
	}

	public Filter() {
	}

	public Filter(String operator, String property, Object value) {
		this.operator = operator;
		this.property = property;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static Filter and(SingularAttribute<?, ?> k1, Object v1, SingularAttribute<?, ?> k2, Object v2) {
		AndFilter filter = new AndFilter();
		filter.addFilter("=", k1, v1);
		filter.addFilter("=", k2, v2);
		return filter;
	}

	public static Filter or(SingularAttribute<?, ?> k1, Object v1, SingularAttribute<?, ?> k2, Object v2) {
		OrFilter filter = new OrFilter();
		filter.addFilter("=", k1, v1);
		filter.addFilter("=", k2, v2);
		return filter;
	}

}
