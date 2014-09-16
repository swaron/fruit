package me.suisui.framework.repo.reader;

import static org.springframework.batch.support.DatabaseType.MYSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.sql.DataSource;

import me.suisui.framework.paging.Sorter;
import me.suisui.framework.repo.jdbc.BeanRowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.support.DatabaseType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

public class JdbcPagingReader<T> {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private PagingQueryProvider queryProvider;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private RowMapper<T> rowMapper;

	private String allSql;
	private String countSql;
	private Map<String, Object> parameterValues;

	public JdbcPagingReader(PagingQueryProvider provider, DataSource dataSource, RowMapper<T> rowMapper) {
		this.setQueryProvider(provider);
		this.setDataSource(dataSource);
		this.setRowMapper(rowMapper);
	}

	private void setRowMapper(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	private void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	private void setQueryProvider(PagingQueryProvider queryProvider) {
		this.queryProvider = queryProvider;
		this.allSql = queryProvider.generateAllQuery();
		this.countSql = queryProvider.generateCountQuery();
	}

	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public int readCount() {
		int count;
		if (logger.isDebugEnabled()) {
			logger.debug("SQL used for counting page: [" + countSql + "]");
		}
		if (parameterValues != null && parameterValues.size() > 0) {
			if (this.queryProvider.isUsingNamedParameters()) {
				count = namedParameterJdbcTemplate.queryForInt(countSql, getParameterMap(parameterValues, null));
			} else {
				count = getJdbcTemplate().queryForInt(countSql, getParameterList(parameterValues, null).toArray());
			}
		} else {
			count = getJdbcTemplate().queryForInt(countSql);
		}
		return count;
	}
	
	public int readCount(Map<String, Object> parameterValues) {
		this.setParameterValues(parameterValues);
		return readCount();
	}

	public List<T> readAll() {
		if (logger.isDebugEnabled()) {
			logger.debug("SQL used for reading page: [" + allSql + "]");
		}
		List<T> records;
		if (parameterValues != null && parameterValues.size() > 0) {
			if (this.queryProvider.isUsingNamedParameters()) {
				records = namedParameterJdbcTemplate.query(allSql, getParameterMap(parameterValues, null), rowMapper);
			} else {
				records = getJdbcTemplate().query(allSql, getParameterList(parameterValues, null).toArray(), rowMapper);
			}
		} else {
			records = getJdbcTemplate().query(allSql, rowMapper);
		}
		return records;
	}
	public List<T> readAll(Map<String, Object> parameterValues) {
		this.setParameterValues(parameterValues);
		return readAll();
	}

	public List<T> readPage(Map<String, Object> parameterValues, Integer start, Integer limit) {
		this.setParameterValues(parameterValues);
		return readPage(start, limit);
	}
	public List<T> readPage(Integer start, Integer limit) {
		if (start == null && limit == null) {
			return readAll();
		}
		int _start = start == null ? 0 : start;
		int _limit = limit == null ? (Integer.MAX_VALUE - _start): limit;
		if (logger.isDebugEnabled()) {
			logger.debug("SQL used for reading page: [" + queryProvider.generatePagingQuery(_start,_limit) + "]");
		}
		List<T> records;
		if (parameterValues != null && parameterValues.size() > 0) {
			if (this.queryProvider.isUsingNamedParameters()) {
				records = namedParameterJdbcTemplate
						.query(queryProvider.generatePagingQuery(_start,_limit), getParameterMap(parameterValues, null), rowMapper);
			} else {
				records = getJdbcTemplate().query(queryProvider.generatePagingQuery(_start,_limit), getParameterList(parameterValues, null).toArray(),
						rowMapper);
			}
		} else {
			records = getJdbcTemplate().query(queryProvider.generatePagingQuery(_start,_limit), rowMapper);
		}
		return records;
	}

	private JdbcTemplate getJdbcTemplate() {
		return (JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations();
	}

	private Map<String, Object> getParameterMap(Map<String, Object> values, Map<String, Object> sortKeyValues) {
		Map<String, Object> parameterMap = new LinkedHashMap<String, Object>();
		if (values != null) {
			parameterMap.putAll(values);
		}
		if (sortKeyValues != null && !sortKeyValues.isEmpty()) {
			for (Map.Entry<String, Object> sortKey : sortKeyValues.entrySet()) {
				parameterMap.put("_" + sortKey.getKey(), sortKey.getValue());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Using parameterMap:" + parameterMap);
		}
		return parameterMap;
	}

	private List<Object> getParameterList(Map<String, Object> values, Map<String, Object> sortKeyValue) {
		SortedMap<String, Object> sm = new TreeMap<String, Object>();
		if (values != null) {
			sm.putAll(values);
		}
		List<Object> parameterList = new ArrayList<Object>();
		parameterList.addAll(sm.values());
		if (sortKeyValue != null && sortKeyValue.size() > 0) {
			List<Map.Entry<String, Object>> keys = new ArrayList<Map.Entry<String, Object>>(sortKeyValue.entrySet());

			for (int i = 0; i < keys.size(); i++) {
				for (int j = 0; j < i; j++) {
					parameterList.add(keys.get(j).getValue());
				}

				parameterList.add(keys.get(i).getValue());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Using parameterList:" + parameterList);
		}
		return parameterList;
	}

	public static <E> Builder<E> builder(DataSource dataSource, Class<E> clazz) {
		Builder<E> builder = new Builder<E>(dataSource);
		builder.setRowMapper(BeanRowMapper.get(clazz));
		return builder;
	}

	public static class Builder<T> {
		private PagingQueryProvider provider;
		private DataSource dataSource;

		private DatabaseType databaseType;
		private String selectClause;
		private String countClause;
		private String fromClause;
		private String whereClause;

		private String groupClause;
		private Map<String, Order> sortKeys;

		private RowMapper<T> rowMapper;

		private static Map<DatabaseType, Class<? extends PagingQueryProvider>> providers = new HashMap<DatabaseType, Class<? extends PagingQueryProvider>>();
		static {
			// refer to SqlPagingQueryProviderFactoryBean
			providers.put(MYSQL, MySqlPagingQueryProvider.class);
			providers.put(DatabaseType.POSTGRES, PostgresPagingQueryProvider.class);
		}

		public Builder(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		public void setDatabaseType(DatabaseType databaseType) {
			this.databaseType = databaseType;
		}

		/**
		 * @param groupClause
		 *            SQL GROUP BY clause part of the SQL query string
		 */
		public void setGroupClause(String groupClause) {
			this.groupClause = groupClause;
		}

		/**
		 * @param dataSource
		 *            the dataSource to set
		 */
		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		/**
		 * @param fromClause
		 *            the fromClause to set
		 */
		public void setFromClause(String fromClause) {
			this.fromClause = fromClause;
		}

		/**
		 * @param whereClause
		 *            the whereClause to set
		 */
		public void setWhereClause(String whereClause) {
			this.whereClause = whereClause;
		}

		/**
		 * @param selectClause
		 *            the selectClause to set
		 */
		public void setSelectClause(String selectClause) {
			this.selectClause = selectClause;
		}

		public void setSortKey(String key) {
			Assert.doesNotContain(key, ",", "String setter is valid for a single ASC key only");
			Map<String, Order> keys = new LinkedHashMap<String, Order>();
			keys.put(key, Order.ASCENDING);
			this.sortKeys = keys;
		}

		/**
		 * @param sortKeys
		 *            the sortKeys to set
		 */
		public void setSortKeys(Map<String, Order> sortKeys) {
			this.sortKeys = sortKeys;
		}

		public void setSortKeys(List<Sorter> sort) {
			if (this.sortKeys == null) {
				this.sortKeys = Maps.newLinkedHashMap();
			}
			for (Sorter sorter : sort) {
				Order order = "DESC".equals(sorter.getDirection()) ? Order.DESCENDING : Order.ASCENDING;
				sortKeys.put(sorter.getProperty(), order);
			}
		}

		public JdbcPagingReader<T> build() {
			DatabaseType type;
			try {
				type = databaseType != null ? databaseType : DatabaseType.fromMetaData(dataSource);
			} catch (MetaDataAccessException e) {
				throw new IllegalArgumentException(
						"Could not inspect meta data for database type.  You have to supply it explicitly.", e);
			}
			Assert.isTrue(providers.containsKey(type),
					"Database unsupported: missing PagingQueryProvider for DatabaseType=" + type);

			try {
				provider = providers.get(type).newInstance();
			} catch (InstantiationException e) {
				throw new IllegalStateException("Should not happen: InstantiationException for Class ="
						+ providers.get(type));
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Should not happen: IllegalAccessException for Class ="
						+ providers.get(type));
			}

			provider.setFromClause(fromClause);
			provider.setWhereClause(whereClause);
			if (sortKeys != null) {
				provider.setSortKeys(sortKeys);
			}
			if (StringUtils.hasText(selectClause)) {
				provider.setSelectClause(selectClause);
			}
			if (StringUtils.hasText(countClause)) {
				provider.setCountCause(countClause);
			}
			if (StringUtils.hasText(groupClause)) {
				provider.setGroupClause(groupClause);
			}
			provider.init();
			JdbcPagingReader<T> jdbcPagingReader = new JdbcPagingReader<T>(provider, dataSource, rowMapper);
			return jdbcPagingReader;

		}

		private Map<String, Order> generateSortKey(String selectClause) {
			String select = removeKeyWord("select", selectClause);
			select = removeKeyWord("distinct", select);
			String[] split = org.apache.commons.lang3.StringUtils.split(select, " ,()");
			if (split.length > 0) {
				this.setSortKey(split[0]);
			}
			return this.sortKeys;
		}

		private String removeKeyWord(String keyWord, String clause) {
			String temp = clause.trim();
			String keyWordString = keyWord + " ";
			if (temp.toLowerCase().startsWith(keyWordString) && temp.length() > keyWordString.length()) {
				return temp.substring(keyWordString.length());
			} else {
				return temp;
			}
		}

		public void setRowMapper(RowMapper<T> rowMapper) {
			this.rowMapper = rowMapper;
		}

		public void setCountClause(String countClause) {
			this.countClause = countClause;
		}

	}

}
