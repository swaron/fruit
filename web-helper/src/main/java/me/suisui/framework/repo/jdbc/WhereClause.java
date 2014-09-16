package me.suisui.framework.repo.jdbc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.suisui.framework.paging.Filter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.And;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WhereClause {
	public static final String AND = " and ";
	public static final String OR = " or ";
	protected static Logger logger = LoggerFactory.getLogger(WhereClause.class);
	private List<String> expressions;
	private String join = AND;
	private Map<String, Object> params;

	public WhereClause(String join, List<String> expressions, Map<String, Object> params) {
		this.join = join;
		this.expressions = expressions;
		this.params = params;
	}

	public WhereClause(List<String> expressions, Map<String, Object> params) {
		this(AND, expressions, params);
	}

	public static WhereClause from(String expression) {
		ArrayList<String> expressions = Lists.newArrayList(expression);
		return new WhereClause(expressions, Maps.<String, Object> newHashMap());

	}

	public static WhereClause or(WhereClause clause1, WhereClause clause2) {
		List<String> expressions = Lists.newArrayList();
		expressions.add(clause1.getSql());
		expressions.add(clause2.getSql());
		HashMap<String, Object> params = Maps.newHashMap();
		params.putAll(clause1.getParamMap());
		params.putAll(clause2.getParamMap());
		WhereClause whereClause = new WhereClause(WhereClause.OR, expressions, params);
		return whereClause;
	}

	public static WhereClause and(WhereClause clause1, WhereClause clause2) {
		List<String> expressions = Lists.newArrayList();
		safeAdd(expressions, clause1.getSql());
		safeAdd(expressions, clause2.getSql());
		HashMap<String, Object> params = Maps.newHashMap();
		params.putAll(clause1.getParamMap());
		params.putAll(clause2.getParamMap());
		WhereClause whereClause = new WhereClause(WhereClause.AND, expressions, params);
		return whereClause;
	}

	public WhereClause add(WhereClause clause2) {
		String expression2 = clause2.getSql();
		// if (this.join.equals(clause2.join)) {
		// } else {
		// }
		safeAdd(expressions, expression2);

		this.getParamMap().putAll(getParamMap());
		return this;
	}

	private static List<String> safeAdd(List<String> list, String sql) {
		if (StringUtils.isNotBlank(sql)) {
			list.add(sql);
		}
		return list;
	}

	public String getSql() {
		if (expressions != null && !expressions.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("(");
			Joiner.on(join).appendTo(builder, expressions);
			builder.append(")");
			return builder.toString();
		} else {
			return "";
		}
	}

	public String whereSql() {
		String sql = getSql();
		if (sql.isEmpty()) {
			return "";
		} else {
			return " where " + sql;
		}
	}

	public Map<String, Object> getParamMap() {
		return params;
	}

	public static Builder andBuilder() {
		return new Builder(AND);
	}

	public static Builder orBuilder() {
		return new Builder(OR);
	}

	public static class Builder {
		private String join = null;
		final ArrayList<Entry<String, Object>> entries = Lists.newArrayList();
		final ArrayList<String> expressions = Lists.newArrayList();

		// Table<WhereClause, String, Object> table = HashBasedTable.create();
		int filterIndex = 0;

		// private String alias;

		public Builder(String join) {
			this.join = join;
		}

		public Builder emptyIgnoreAdd(String expression, String name, Collection<?> value) {
			if (!CollectionUtils.isEmpty(value)) {
				add(expression, name, value);
			}
			return this;
		}

		public Builder add(String expression, String name, Object value) {

			if (value != null) {
				if (value instanceof Collection) {
					Collection<?> values = (Collection<?>) value;
					if (values.isEmpty()) {
						expression = "1=0";
					} else {
						List<String> names = Lists.newArrayList();
						int i = 1;
						for (Object object : values) {
							String name2 = name + "_" + i++;
							names.add(":" + name2);
							entries.add(Maps.immutableEntry(name2, object));
						}
						expression = expression.replace(":" + name, StringUtils.join(names, ","));
					}
				} else if (value.getClass().isArray()) {
					Object[] objects = (Object[]) value;
					if (Array.getLength(value) == 0) {
						expression = "1=0";
					} else {
						List<String> names = Lists.newArrayList();
						int i = 1;
						for (Object object : objects) {
							String name2 = name + "_" + i++;
							names.add(":" + name2);
							entries.add(Maps.immutableEntry(name2, object));
						}
						expression = expression.replace(":" + name, StringUtils.join(names, ","));
					}
				} else if (name != null) {
					entries.add(Maps.immutableEntry(name, value));
				}
				expressions.add(expression);
			}
			return this;
		}

		public Builder add(String expression) {
			expressions.add(expression);
			return this;
		}

		public Builder fromFilter(List<Filter> filters) {
			if (filters != null) {
				for (Filter filter : filters) {
					fromFilter(filter);
				}
			}
			return this;
		}
		CharMatcher chars = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'));
		public Builder fromFilter(Filter filter) {
			filter.normalize();
			//防止注入
			String[] ops = Filter.ALL_OPERATOR;
			if (!ArrayUtils.contains(ops, filter.getOperator())) {
				return this;
			}
			//防止注入
			SqlChecker.checkColName(filter.getProperty());
			if (filter.getValue() != null) {
				String name = chars.retainFrom(filter.getProperty()) + "_" + filterIndex++;
				String expression = filter.getProperty() + " " + filter.getOperator() + " :" + name;
				Object value = filter.getValue();

				if ("in".equals(filter.getProperty())) {
					expression = filter.getProperty() + " " + filter.getOperator() + "(" + " :" + name + ")";
					if (value != null) {
						Collection<?> values = (Collection<?>) value;
						if (values.isEmpty()) {
							expression = "1=0";
						} else {
							List<String> names = Lists.newArrayList();
							int i = 1;
							for (Object object : values) {
								String name2 = name + "_" + i++;
								names.add(":" + name2);
								entries.add(Maps.immutableEntry(name2, object));
							}
							expression = expression.replace(":" + name, StringUtils.join(names, ","));
						}
					}
				} else {
					if (value != null) {
						entries.add(Maps.immutableEntry(name, value));
					}
				}
				expressions.add(expression);
			}
			return this;
		}

		public static WhereClause fromList(String join, ArrayList<String> expressions,
				ArrayList<Entry<String, Object>> entries) {
			HashMap<String, Object> params = Maps.newHashMap();
			for (Entry<String, Object> entry : entries) {
				params.put(entry.getKey(), entry.getValue());
			}
			return new WhereClause(join, expressions, params);
		}

		// public Builder alias(String alias) {
		// // this.alias = alias;
		// return this;
		// }

		public WhereClause build() {
			Assert.isTrue(AND.equals(join) || OR.equals(join),
					"join parameter should be ' or ' or ' and ', eg. use WhereClause.AND");
			return fromList(join, expressions, entries);
		}
	}
}
