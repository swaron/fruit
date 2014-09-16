package me.suisui.framework.repo.reader;

public class OraclePagingQueryProvider extends AbstractSqlPagingQueryProvider {

	@Override
	public String generateCountQuery() {
		return generateRowNumSqlQuery(this, getCountClause(), null);
	}

	@Override
	public String generateAllQuery() {
		return generateRowNumSqlQuery(this, getSelectClause(), null);
	}

	@Override
	public String generatePagingQuery(int offset, int limit) {
		return generateRowNumSqlQuery(this, getSelectClause(), buildRowNumClause(offset, limit));
	}

	private String buildRowNumClause(int offset, int limit) {
		return new StringBuilder().append("ROWNUM > ").append(offset).append(" and ROWNUM <= ").append(offset + limit)
				.toString();
	}
}
