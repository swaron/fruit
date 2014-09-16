package me.suisui.framework.repo.jdbc;

import javax.sql.DataSource;

public class SqlUpdate extends org.springframework.jdbc.object.SqlUpdate {
	public SqlUpdate() {
	}

	public SqlUpdate(DataSource ds, String sql) {
		super(ds, sql);
	}

	public SqlUpdate(DataSource dataSource) {
		setDataSource(dataSource);
	}
}
