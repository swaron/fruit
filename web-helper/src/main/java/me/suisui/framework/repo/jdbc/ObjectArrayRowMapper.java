package me.suisui.framework.repo.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ObjectArrayRowMapper implements RowMapper<Object[]> {
	@Override
	public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
		int size = rs.getMetaData().getColumnCount();
		Object[] result = new Object[size];
		for (int i = 0; i < size; i++) {
			result[i] = rs.getObject(i+1);
		}
		return result;
	}
}
