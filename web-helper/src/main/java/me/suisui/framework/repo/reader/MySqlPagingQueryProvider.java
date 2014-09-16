package me.suisui.framework.repo.reader;

public class MySqlPagingQueryProvider extends AbstractSqlPagingQueryProvider {

	@Override
	public String generateCountQuery() {
		return generateLimitSqlQuery(this, this.getCountClause(), null);
	}

	@Override
	public String generateAllQuery() {
		return generateLimitSqlQuery(this, this.getSelectClause(), null);
	}

	@Override
	public String generatePagingQuery(int offset, int limit) {
		String limitClause = new StringBuilder().append("LIMIT ").append(offset).append(", ").append(limit).toString();
		return generateLimitSqlQuery(this, this.getSelectClause(), limitClause);
	}

}
