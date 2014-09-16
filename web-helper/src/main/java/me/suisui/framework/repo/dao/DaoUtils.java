package me.suisui.framework.repo.dao;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import me.suisui.framework.paging.AndFilter;
import me.suisui.framework.paging.Filter;
import me.suisui.framework.paging.OrFilter;
import me.suisui.framework.paging.Sorter;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class DaoUtils {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected EntityManager entityManager;

	public DaoUtils(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	// 当没有设置timezone的时候的 默认 timezone，
	// 确保和AppSetting里面设置的一样
	private static TimeZone defaultTimeZone = TimeZone.getTimeZone("GMT+8:00");

	protected StdDateFormat stdDateFormat = new StdDateFormat(defaultTimeZone);

	
	private <T> Predicate buildOrFilter(Root<T> root, OrFilter filter) {
		List<Predicate> filters = buildFilters(root, filter.getFilters());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		if(!filters.isEmpty()){
			Predicate[] filterArray = filters.toArray(new Predicate[filters.size()]);
			return builder.or(filterArray);
		}else{
			return null;
		}
	}
	private <T> Predicate buildAndFilter(Root<T> root, AndFilter filter) {
		List<Predicate> filters = buildFilters(root, filter.getFilters());
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		if(!filters.isEmpty()){
			Predicate[] filterArray = filters.toArray(new Predicate[filters.size()]);
			return builder.and(filterArray);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<Predicate> buildFilters(Root<T> root, List<Filter> list) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		ArrayList<Predicate> predicates = new ArrayList<Predicate>();
		if (list == null) {
			return predicates;
		}
		for (Filter e : list) {
			try {
				if(e instanceof OrFilter){
					predicates.add(buildOrFilter(root, (OrFilter)e));
					continue;
				}else if(e instanceof AndFilter){
					predicates.add(buildAndFilter(root,(AndFilter)e));
					continue;
				}
				
				e.normalize();
				String operator = e.getOperator();
				String key = e.getProperty();
				Object value = e.getValue();
				Path<Object> property;
				if (key.contains(".")) {
					String[] paths = StringUtils.split(key, '.');
					property = (Path<Object>) root;
					for (String path : paths) {
						property = property.get(path);
					}
				} else {
					property = root.get(key);
				}

				// 特殊处理2个特别的操作符。
				if (Filter.OPERATOR_NULL.equalsIgnoreCase(operator)) {
					predicates.add(builder.isNull(property));
					continue;
				} else if (Filter.OPERATOR_NOT_NULL.equalsIgnoreCase(operator)) {
					predicates.add(builder.isNotNull(property));
					continue;
				}

				if(value == null){
					// 如果值为null，不是空字符串
					continue;
				}
				//如果传了一个空数组，那么让查询返回空数据。
				if(value != null && value.getClass().isArray() ){
					if(Array.getLength(value) ==0 ){
						predicates.add(builder.disjunction());
                        continue;
					}
				}
				if(value != null && value instanceof Collection ){
					if(((Collection) value).isEmpty()){
						predicates.add(builder.disjunction());
                        continue;
					}
				}
				

				Class<? extends Object> javaType = property.getJavaType();
				if (ClassUtils.isAssignable(javaType, String.class, true)) {
					addStringPredicate(predicates, builder, property, operator, value);
				} else if (ClassUtils.isAssignable(javaType, long.class, true)) {
					addNumberPredicate(predicates, builder, property, operator, value);
				} else if (ClassUtils.isAssignable(javaType, double.class, true)) {
					addFloatPredicate(predicates, builder, property, operator, value);
				} else if (ClassUtils.isAssignable(javaType, boolean.class, true)) {
					addBooleanPredicate(predicates, builder, property, operator, value);
				} else if (ClassUtils.isAssignable(javaType, Date.class, true)) {
					addDatePredicate(predicates, builder, property, operator, value);
				} else if (ClassUtils.isAssignable(javaType, Object.class, true) && isEntity(javaType)) {
					addEntityPredicate(predicates, builder, property, operator, value);
				} else {
					logger.warn("unsupported filter property. type is {}, property name is {}.", javaType, key);
				}
			} catch (RuntimeException ex) {
				logger.warn("filter " + e.getProperty() + "  not applied.", ex);
			}
		}

		return predicates;
	}

	private <T> void addNumberPredicate(List<Predicate> predicates, CriteriaBuilder builder, Path<T> property,
			String operator, Object value) {
		// handle long,int,short,char

		Path<Number> numProperty = (Path<Number>) property;
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;
			predicates.add(numProperty.in(values));
			return;
		} else if (value instanceof Collection) {
			Collection<Object> values = (Collection<Object>) value;
			predicates.add(numProperty.in(values));
			return;
		}
		Number opValue = null;
		if (value instanceof Number) {
			opValue = (Number) value;
		} else {
			opValue = Long.valueOf(value.toString());
		}
		try {
			if (operator != null) {
				if ("==".equals(operator) || "=".equals(operator)) {
					predicates.add(builder.equal(numProperty, opValue));
				} else if ("<".equals(operator)) {
					predicates.add(builder.lt(numProperty, opValue));
				} else if (">".equals(operator)) {
					predicates.add(builder.gt(numProperty, opValue));
				} else if ("<=".equals(operator)) {
					predicates.add(builder.le(numProperty, opValue));
				} else if (">=".equals(operator)) {
					predicates.add(builder.ge(numProperty, opValue));
				} else if ("<>".equals(operator) || "!=".equals(operator)) {
					predicates.add(builder.notEqual(numProperty, opValue));
				} else {
					logger.warn("unsupported number operator, operator is {}", operator);
				}
			} else {
				Predicate equal = builder.equal(numProperty, opValue);
				predicates.add(equal);
			}
		} catch (NumberFormatException ex) {
			logger.warn("unable to parse long while appling filter, string value is {}", value);
		}
	}

	private <T> void addFloatPredicate(List<Predicate> predicates, CriteriaBuilder builder, Path<T> property,
			String operator, Object value) {
		// handle double,float
		Path<Double> numProperty = (Path<Double>) property;
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;
			predicates.add(numProperty.in(values));
			return;
		} else if (value instanceof Collection) {
			Collection<Object> values = (Collection<Object>) value;
			predicates.add(numProperty.in(values));
			return;
		}
		Double opValue = null;
		if (value instanceof Double) {
			opValue = (Double) value;
		} else if(value instanceof Float){
			opValue = ((Float)value).doubleValue();
		} else{
			opValue = Double.valueOf(value.toString());
		}
		try {
			if (operator != null) {
				if ("==".equals(operator) || "=".equals(operator)) {
					predicates.add(builder.equal(numProperty, opValue));
				} else if ("<".equals(operator)) {
					predicates.add(builder.lt(numProperty, opValue));
				} else if (">".equals(operator)) {
					predicates.add(builder.gt(numProperty, opValue));
				} else if ("<=".equals(operator)) {
					predicates.add(builder.le(numProperty, opValue));
				} else if (">=".equals(operator)) {
					predicates.add(builder.ge(numProperty, opValue));
				} else if ("<>".equals(operator) || "!=".equals(operator)) {
					predicates.add(builder.notEqual(numProperty, opValue));
				} else {
					logger.warn("unsupported number operator, operator is {}", operator);
				}
			} else {
				Predicate equal = builder.equal(numProperty, opValue);
				predicates.add(equal);
			}
		} catch (NumberFormatException ex) {
			logger.warn("unable to parse double while appling filter, string value is {}", value);
		}
	}

	private <T> void addBooleanPredicate(List<Predicate> predicates, CriteriaBuilder builder, Path<T> property,
			String operator, Object value) {
		// 布尔类型，支持字符串格式的 0,1. 和true，false
		Path<Boolean> booleanProperty = (Path<Boolean>) property;
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;
			predicates.add(booleanProperty.in(values));
			return;
		} else if (value instanceof Collection) {
			Collection<Object> values = (Collection<Object>) value;
			predicates.add(booleanProperty.in(values));
			return;
		}
		String stringValue = value.toString();

		boolean requestTrue;
		try {
			boolean revert = false;
			String opValue = value.toString();
			if (operator != null) {
				if ("==".equals(operator) || "=".equals(operator)) {
				} else if ("<>".equals(operator) || "!=".equals(operator)) {
					revert = true;
				} else {
					logger.warn("unsupported boolean operator, operator is {}", operator);
				}
			}
			if (NumberUtils.isDigits(opValue)) {
				// try to treat Natural Number other than 0 as true
				long longValue = Long.parseLong(opValue);
				requestTrue = (longValue != 0);
			} else {
				// treat case insensitive 'true' as true, others as
				// false
				requestTrue = Boolean.parseBoolean(opValue);
			}
			if (revert) {
				requestTrue = !requestTrue;
			}
			if (requestTrue) {
				predicates.add(builder.isTrue(booleanProperty));
			} else {
				predicates.add(builder.isFalse(booleanProperty));
			}
		} catch (NumberFormatException ex) {
			logger.warn("unable to parse Long while appling filter, string value is {}", stringValue);
		}
	}

	private <T> void addDatePredicate(List<Predicate> predicates, CriteriaBuilder builder, Path<T> property,
			String operator, Object value) {
		// 日期类型
		try {
			Path<Date> dateProperty = (Path<Date>) property;
			if (value instanceof Object[]) {
				Object[] values = (Object[]) value;
				predicates.add(dateProperty.in(values));
				return;
			} else if (value instanceof Collection) {
				Collection<Object> values = (Collection<Object>) value;
				predicates.add(dateProperty.in(values));
				return;
			}
			// 有带比较符的情况
			if (operator != null) {
				// 支持的格式 ("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
				// "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd")
				// notes: "yyyy-MM-dd HH:mm:ss" is not supported
				// yet,
				// 值的例子 pattern: >= 2013-4-16, < 2013-01-06T13:02:03
				// , < 2013-01-06T13:02:03.000+800 <1350024476440
				Date opValue = null;
				if (value instanceof Date) {
					opValue = (Date) value;
				} else {
					opValue = stdDateFormat.parse(value.toString());
				}

				if ("==".equals(operator) || "=".equals(operator)) {
					predicates.add(builder.equal(dateProperty, opValue));
				} else if ("<".equals(operator)) {
					predicates.add(builder.lessThan(dateProperty, opValue));
				} else if (">".equals(operator)) {
					predicates.add(builder.greaterThan(dateProperty, opValue));
				} else if ("<=".equals(operator)) {
					predicates.add(builder.lessThanOrEqualTo(dateProperty, opValue));
				} else if (">=".equals(operator)) {
					predicates.add(builder.greaterThanOrEqualTo(dateProperty, opValue));
				} else if ("<>".equals(operator) || "!=".equals(operator)) {
					predicates.add(builder.notEqual(dateProperty, opValue));
				} else {
					logger.warn("unsupported date operator, operator is {}", operator);
				}
			} else {
				Date opValue = null;
				if (value instanceof Date) {
					opValue = (Date) value;
				} else {
					opValue = stdDateFormat.parse(value.toString());
				}
				Predicate equal = builder.equal(dateProperty, opValue);
				predicates.add(equal);
			}
		} catch (ParseException ex) {
			logger.warn("unable to parse date while appling filter, date value is {}", value, ex);
		}
	}

	private <T> void addEntityPredicate(List<Predicate> predicates, CriteriaBuilder builder, Path<T> property,
			String operator, Object value) {
		// entity is the same with number, the primary key of entity MUST be
		// long/int
		addNumberPredicate(predicates, builder, property, operator, value);
	}

	private <T> void addStringPredicate(List<Predicate> predicates, CriteriaBuilder builder, Path<T> property,
			String operator, Object value) {
		// handle string property

		Path<String> stringProperty = (Path<String>) property;

		if (value instanceof String[]) {
			Object[] values = (String[]) value;
			predicates.add(stringProperty.in(values));
			return;
		} else if (value instanceof Collection) {
			Collection<String> values = (Collection<String>) value;
			predicates.add(stringProperty.in(values));
			return;
		}
		String stringValue = value.toString();
		if("=".equals(operator)){
			predicates.add(builder.equal(stringProperty, stringValue));
		}else if ("<".equals(operator)) {
			predicates.add(builder.lessThan(stringProperty, stringValue));
		} else if (">".equals(operator)) {
			predicates.add(builder.greaterThan(stringProperty, stringValue));
		} else if ("<=".equals(operator)) {
			predicates.add(builder.lessThanOrEqualTo(stringProperty, stringValue));
		} else if (">=".equals(operator)) {
			predicates.add(builder.greaterThanOrEqualTo(stringProperty, stringValue));
		} else if ("<>".equals(operator) || "!=".equals(operator)) {
			predicates.add(builder.notEqual(stringProperty, stringValue));
		}else if("like".equals(operator)){
			predicates.add(builder.like(stringProperty, stringValue));
		}else if("not like".equals(operator)){
			predicates.add(builder.notLike(stringProperty, stringValue));
		}else{
			logger.warn("unsupported date operator, operator is {}", operator);
		}
	}

	private boolean isEntity(Class<? extends Object> javaType) {
		boolean present = javaType.isAnnotationPresent(Table.class);
		return present;
	}

	public <T> List<Order> buildOrders(Root<T> root, List<Sorter> list) {
		List<Order> orders = new ArrayList<Order>();
		if (list == null) {
			return orders;
		}
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		for (Sorter sort : list) {
			try {

				String key = sort.getProperty();
				Path<Object> property;
				if (key.contains(".")) {
					String[] paths = StringUtils.split(key, '.');
					property = (Path<Object>) root;
					for (String path : paths) {
						property = property.get(path);
					}
				} else {
					property = root.get(key);
				}

				if (sort.getDirection().equalsIgnoreCase("ASC")) {
					orders.add(builder.asc(property));
				} else {
					orders.add(builder.desc(property));
				}
			} catch (IllegalArgumentException e) {
				logger.warn("sorting parameter not applied, the field name " + sort.getProperty() + " NOT MATCHED", e);
			}
		}
		return orders;
	}

	public EntityType<?> resolveEntityByName(String name) {
		Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
		EntityType<?> entity = null;
		for (EntityType<?> entityType : entities) {
			String entityName = entityType.getName();
			if (StringUtils.equals(name, entityName)) {
				entity = entityType;
				break;
			}
		}
		for (EntityType<?> entityType : entities) {
			Class<?> javaType = entityType.getJavaType();
			if (javaType.isAnnotationPresent(Table.class)) {
				String annotationName = javaType.getAnnotation(Table.class).name();
				if (StringUtils.equals(name, annotationName)) {
					entity = entityType;
					break;
				}
			}
		}
		return entity;
	}

	public Class<?> resolveClassByName(String name) {
		EntityType<?> entityType = resolveEntityByName(name);
		return entityToCls(entityType);
	}

	public Class<? extends Object> entityToCls(EntityType<?> enttity) {
		if (enttity == null) {
			return null;
		} else {
			return enttity.getJavaType();
		}
	}

	public EntityType<?> clsToEntity(Class<? extends Object> cls) {
		EntityType<? extends Object> entity = entityManager.getMetamodel().entity(cls);
		return entity;
	}

}
