package me.suisui.framework.repo.reader;

import java.util.Map;

import org.springframework.batch.item.database.Order;

public interface PagingQueryProvider {

	boolean isUsingNamedParameters();

	void setFromClause(String fromClause);

	void setWhereClause(String whereClause);

	void setSortKeys(Map<String, Order> sortKeys);

	void setSelectClause(String selectClause);

	void setGroupClause(String groupClause);

	void setCountCause(String countClause);

	String generateCountQuery();

	String generateAllQuery();

	String generatePagingQuery(int offset, int limit);

	void init();

}
