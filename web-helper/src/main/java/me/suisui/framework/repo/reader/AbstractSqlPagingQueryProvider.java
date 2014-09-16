package me.suisui.framework.repo.reader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.database.JdbcParameterUtils;
import org.springframework.batch.item.database.Order;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public abstract class AbstractSqlPagingQueryProvider implements PagingQueryProvider{

	private String selectClause;
	private String countClause;

	private String fromClause;

	private String whereClause;

	private Map<String, Order> sortKeys = new LinkedHashMap<String, Order>();

	private String groupClause;

	private String sortClause;

	private int parameterCount;

	private boolean usingNamedParameters;

	/**
	 * The setter for the group by clause
	 * 
	 * @param groupClause
	 *            SQL GROUP BY clause part of the SQL query string
	 */
	public void setGroupClause(String groupClause) {
		if (StringUtils.hasText(groupClause)) {
			this.groupClause = removeKeyWord("group by", groupClause);
		} else {
			this.groupClause = null;
		}
	}

	/**
	 * The getter for the group by clause
	 * 
	 * @return SQL GROUP BY clause part of the SQL query string
	 */
	public String getGroupClause() {
		return this.groupClause;
	}

	/**
	 * @param selectClause
	 *            SELECT clause part of SQL query string
	 */
	public void setSelectClause(String selectClause) {
		this.selectClause = removeKeyWord("select", selectClause);
	}

	/**
	 * 
	 * @return SQL SELECT clause part of SQL query string
	 */
	protected String getSelectClause() {
		return selectClause;
	}

	/**
	 * @param fromClause
	 *            FROM clause part of SQL query string
	 */
	public void setFromClause(String fromClause) {
		this.fromClause = removeKeyWord("from", fromClause);
	}

	/**
	 * 
	 * @return SQL FROM clause part of SQL query string
	 */
	protected String getFromClause() {
		return fromClause;
	}

	/**
	 * @param whereClause
	 *            WHERE clause part of SQL query string
	 */
	public void setWhereClause(String whereClause) {
		if (StringUtils.hasText(whereClause)) {
			this.whereClause = removeKeyWord("where", whereClause);
		} else {
			this.whereClause = null;
		}
	}

	/**
	 * 
	 * @return SQL WHERE clause part of SQL query string
	 */
	protected String getWhereClause() {
		return whereClause;
	}

	public String getCountClause() {
		if (countClause == null) {
			if(this.getSelectClause().contains(",")){
				return "count(*)";
			}else{
				return "count(" + this.getSelectClause() + ")";
			}

		} else {
			return countClause;

		}
	}

	public void setCountCause(String countClause) {
		this.countClause = removeKeyWord("select", countClause);
	}

	/**
	 * @param sortKeys
	 *            key to use to sort and limit page content
	 */
	public void setSortKeys(Map<String, Order> sortKeys) {
		this.sortKeys = sortKeys;
	}

	/**
	 * A Map<String, Boolean> of sort columns as the key and boolean for
	 * ascending/descending (assending = true).
	 * 
	 * @return sortKey key to use to sort and limit page content
	 */
	public Map<String, Order> getSortKeys() {
		return sortKeys;
	}

	public int getParameterCount() {
		return parameterCount;
	}

	public boolean isUsingNamedParameters() {
		return usingNamedParameters;
	}

	/**
	 * The sort key placeholder will vary depending on whether named parameters
	 * or traditional placeholders are used in query strings.
	 * 
	 * @return place holder for sortKey.
	 */
	public String getSortKeyPlaceHolder(String keyName) {
		return usingNamedParameters ? ":_" + keyName : "?";
	}

	/**
	 * Check mandatory properties.
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void init() {
		Assert.hasLength(selectClause, "selectClause must be specified");
		Assert.hasLength(fromClause, "fromClause must be specified");
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(selectClause);
		sql.append(" FROM ").append(fromClause);
		if (whereClause != null) {
			sql.append(" WHERE ").append(whereClause);
		}
		if (groupClause != null) {
			sql.append(" GROUP BY ").append(groupClause);
		}
		List<String> namedParameters = new ArrayList<String>();
		parameterCount = JdbcParameterUtils.countParameterPlaceholders(sql.toString(), namedParameters);
		if (namedParameters.size() > 0) {
			if (parameterCount != namedParameters.size()) {
				throw new InvalidDataAccessApiUsageException(
						"You can't use both named parameters and classic \"?\" placeholders: " + sql);
			}
			usingNamedParameters = true;
		}
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

	private static void buildWhereClause(AbstractSqlPagingQueryProvider provider, StringBuilder sql) {
		if (StringUtils.hasText(provider.getWhereClause())) {
			sql.append(" WHERE ");
			sql.append(provider.getWhereClause());
		}

	}

	private static void buildGroupByClause(AbstractSqlPagingQueryProvider provider, StringBuilder sql) {
		if (StringUtils.hasText(provider.getGroupClause())) {
			sql.append(" GROUP BY ");
			sql.append(provider.getGroupClause());
		}
	}

	private static void buildOrderByClause(AbstractSqlPagingQueryProvider provider, StringBuilder sql) {
		if (StringUtils.hasText(provider.getSortClause())) {
			sql.append(" ORDER BY ");
			sql.append(provider.getSortClause());
		}
	}

	private String getSortClause() {
		if (this.sortClause == null) {
			StringBuilder builder = new StringBuilder();
			String prefix = "";

			for (Map.Entry<String, Order> sortKey : this.getSortKeys().entrySet()) {
				builder.append(prefix);

				prefix = ", ";

				builder.append(sortKey.getKey());

				if (sortKey.getValue() != null && sortKey.getValue() == Order.DESCENDING) {
					builder.append(" DESC");
				} else {
					builder.append(" ASC");
				}
			}
			this.sortClause = builder.toString();

		}
		return this.sortClause;
	}

	public static String generateLimitSqlQuery(AbstractSqlPagingQueryProvider provider, String selectClause, String limitClause) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(selectClause);
		sql.append(" FROM ").append(provider.getFromClause());
		buildWhereClause(provider, sql);
		buildGroupByClause(provider, sql);
		buildOrderByClause(provider, sql);
		if (StringUtils.hasText(limitClause)) {
			sql.append(" " + limitClause);
		}
		return sql.toString();
	}

	public static String generateRowNumSqlQuery(AbstractSqlPagingQueryProvider provider, String selectClause,
			String rowNumClause) {
		StringBuilder sql = new StringBuilder();
		if (StringUtils.hasText(rowNumClause)) {
			sql.append("SELECT * FROM (SELECT ").append(selectClause);
			sql.append(" FROM ").append(provider.getFromClause());
			buildWhereClause(provider, sql);
			buildGroupByClause(provider, sql);
			buildOrderByClause(provider, sql);
			sql.append(")");
			sql.append(" WHERE ").append(rowNumClause);
		} else {
			sql.append("SELECT ").append(selectClause);
			sql.append(" FROM ").append(provider.getFromClause());
			buildWhereClause(provider, sql);
			buildGroupByClause(provider, sql);
			buildOrderByClause(provider, sql);
		}
		return sql.toString();
	}

	public static String generateRowNumSqlQueryWithNesting(AbstractSqlPagingQueryProvider provider,
			String selectClause, String rowNumClause) {
		return generateRowNumSqlQueryWithNesting(provider, selectClause, selectClause, rowNumClause);
	}

	public static String generateRowNumSqlQueryWithNesting(AbstractSqlPagingQueryProvider provider,
			String innerSelectClause, String outerSelectClause, String rowNumClause) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ")
				.append(outerSelectClause)
				.append(" FROM (SELECT ")
				.append(outerSelectClause)
				.append(", ")
				.append(StringUtils.hasText(provider.getGroupClause()) ? "MIN(ROWNUM) as TMP_ROW_NUM"
						: "ROWNUM as TMP_ROW_NUM");
		sql.append(" FROM (SELECT ").append(innerSelectClause).append(" FROM ").append(provider.getFromClause());
		buildWhereClause(provider, sql);
		buildGroupByClause(provider, sql);
		buildOrderByClause(provider, sql);
		sql.append(")) WHERE ").append(rowNumClause);

		return sql.toString();

	}

	/**
	 * Generate SQL query string using a TOP clause
	 * 
	 * @param provider
	 *            {@link AbstractSqlPagingQueryProvider} providing the
	 *            implementation specifics
	 * @param topClause
	 *            the implementation specific top clause to be used
	 * @return the generated query
	 */
	public static String generateTopSqlQuery(AbstractSqlPagingQueryProvider provider, String topClause) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append(topClause).append(" ").append(provider.getSelectClause());
		sql.append(" FROM ").append(provider.getFromClause());
		buildWhereClause(provider, sql);
		buildGroupByClause(provider, sql);
		buildOrderByClause(provider, sql);

		return sql.toString();
	}
}
