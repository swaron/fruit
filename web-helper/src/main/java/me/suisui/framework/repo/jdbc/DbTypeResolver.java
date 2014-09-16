package me.suisui.framework.repo.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodes;
import org.springframework.jdbc.support.SQLErrorCodesFactory;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

public class DbTypeResolver {
	private static final Logger logger = LoggerFactory.getLogger(DbTypeResolver.class);
	private static final DbTypeResolver instance = new DbTypeResolver();
	private Map<String, SQLErrorCodes> errorCodesMap;
	private final Map<DataSource, String> dataSourceCache = new WeakHashMap<DataSource, String>(16);
	
	public static DbTypeResolver getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public Map<String, SQLErrorCodes> getErrorCodesMap() {
		return Collections.unmodifiableMap(errorCodesMap);
//		return UnmodifiableMap.decorate(errorCodesMap);
	}
	
	@SuppressWarnings("unchecked")
	public DbTypeResolver() {
		SQLErrorCodesFactory instance = SQLErrorCodesFactory.getInstance();
		try {
			errorCodesMap = (Map<String, SQLErrorCodes>) FieldUtils.readDeclaredField(instance, "errorCodesMap",true);
		} catch (IllegalAccessException e) {
			errorCodesMap = new HashMap<String, SQLErrorCodes>();
			logger.warn("unable te get errorCodesMap from SQLErrorCodesFactory class",e);
		}
	}

	public String normalizeDbName(String productName) {
		if (errorCodesMap.containsKey(productName)) {
			return productName;
		}
		for (Entry<String, SQLErrorCodes> entry : this.errorCodesMap.entrySet()) {
			if (PatternMatchUtils.simpleMatch(entry.getValue().getDatabaseProductNames(), productName)) {
				logger.debug("translate product name: {}, to normal name: {}.", productName, entry.getKey());
				return entry.getKey();
			}
		}
		return productName;
	}
	public String getDbName(DataSource dataSource) {
		Assert.notNull(dataSource, "DataSource must not be null");
		// Let's avoid looking up database product info if we can.
		String sec = this.dataSourceCache.get(dataSource);
		if (sec != null) {
			return sec;
		}
		synchronized (this.dataSourceCache) {
			// We could not find it - have to look it up.
			try {
				String dbName = (String) JdbcUtils.extractDatabaseMetaData(dataSource, "getDatabaseProductName");
				if (dbName != null) {
					sec = normalizeDbName(dbName);
					this.dataSourceCache.put(dataSource, sec);
					return sec;
				}
			}
			catch (MetaDataAccessException ex) {
				logger.warn("Error while extracting database product name - return null", ex);
			}
		}
		return null;
	}

}
