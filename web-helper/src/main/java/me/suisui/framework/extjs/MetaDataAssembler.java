package me.suisui.framework.extjs;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * 为了在前台的grid显示一个数据表的内容，根据一个jpa java类组装出前台grid所需要的一些默认参数<br>
 * 1. 组装store所需要的model对应的fields，这个一般可以直接用 . <br>
 * 2. 组装Grid所需要的column设置，这个一般灵活性需求比较高，需要手动指定
 * 
 * @author aaron
 * 
 */
public class MetaDataAssembler {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	LoadingCache<Class<?>, MetaData> classGridCache = CacheBuilder.newBuilder().maximumSize(1000)
			.build(new CacheLoader<Class<?>, MetaData>() {
				public MetaData load(Class<?> clazz) {
					return createGridConfigFromClassImpl(clazz);
				}
			});

	/**
	 * 根据一个数据库表对应的jpa实体类创建默认情况下前台显示使用的model和grid需要显示的列。
	 * grid通常不会用，因为前台通常需要自定义表格的列头文字，而后台没有这个信息，考虑加到数据库？
	 * 
	 * @param entity
	 * @return
	 */
	public MetaData createGridConfigFromClassImpl(Class<? extends Object> clazz) {
		if (clazz == null) {
			return null;
		}
		MetaData metaData = new MetaData();
		String className = clazz.getSimpleName();
		metaData.setEntity(className);
		Table annotation = AnnotationUtils.findAnnotation(clazz, Table.class);
		if (annotation != null) {
			metaData.setTableName(annotation.name());
		}
		List<PropertyDescriptor> descriptors = getFields(clazz);
		// 创建2个主要的属性 fields和columns，fields用于创建extjs的model, columns用于创建simple db
		// grid的列。
		List<me.suisui.framework.extjs.Field> fields = new ArrayList<me.suisui.framework.extjs.Field>();
		metaData.setFields(fields);
		for (PropertyDescriptor descriptor : descriptors) {
			Field field = FieldUtils.getField(clazz, descriptor.getName(), true);
			parseAndAppendField(fields, descriptor, field, metaData);
		}
		return metaData;
	}

	private List<PropertyDescriptor> getFields(Class<? extends Object> clazz) {
		List<PropertyDescriptor> fields = Lists.newArrayList();
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);

		for (PropertyDescriptor descriptor : descriptors) {
			Method method = descriptor.getReadMethod();
			if (method != null && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
				String fieldName = descriptor.getName();
				if("class".equals(fieldName) ){
					continue;
				}
				fields.add(descriptor);
			}
		}
		return fields;
	}

	public MetaData createGridConfigFromClass(Class<? extends Object> clazz) {
		return classGridCache.getUnchecked(clazz);
	}

	/**
	 * 把一个 java的field 解析到extjs model的field设置，追加到fields里面
	 * 
	 * @param fields
	 * @param descriptor
	 * @param field 
	 * @param metaData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void parseAndAppendField(List<me.suisui.framework.extjs.Field> fields, PropertyDescriptor descriptor, Field field, MetaData metaData) {
		Method method = descriptor.getReadMethod();
		me.suisui.framework.extjs.Field jsField = new me.suisui.framework.extjs.Field(descriptor.getName());
		Class<?> fieldClass = descriptor.getPropertyType();
		if (ClassUtils.isAssignable(fieldClass, Timestamp.class, true)) {
			// string,int,float,boolean
			jsField.setType("date");
		} else if (ClassUtils.isAssignable(fieldClass, Date.class, true)) {
			Temporal annotation = method.getAnnotation(Temporal.class);
			if(annotation == null && field!=null){
				annotation = field.getAnnotation(Temporal.class);
			}
			if (annotation != null && annotation.value() == TemporalType.DATE) {
//				jsField.setType("string");
				jsField.setType("date");
			} else {
				jsField.setType("date");
			}
		} else if (ClassUtils.isAssignable(fieldClass, boolean.class, true)) {
			jsField.setType("boolean");
		} else if (ClassUtils.isAssignable(fieldClass, long.class, true)) {
			jsField.setType("int");
		} else if (ClassUtils.isAssignable(fieldClass, double.class, true)) {
			jsField.setType("float");
		} else if (Object.class.isAssignableFrom(fieldClass) && fieldClass.isAnnotationPresent(Entity.class)) {
			jsField.setType("auto");
		}

		if (method.isAnnotationPresent(Id.class) || (field!=null && field.isAnnotationPresent(Id.class)) ) {
			// 设置id属性
			jsField.setUseNull(true);
			metaData.setIdProperty(descriptor.getName());
		}
		fields.add(jsField);
	}

	private String findJpaIdFieldName(Class clazz) {
		Field[] classFields = clazz.getFields();
		for (Field field : classFields) {
			if (field.isAnnotationPresent(Id.class)) {
				return field.getName();
			}
		}
		return null;
	}
}
