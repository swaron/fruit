package me.suisui.framework.extjs;

/**
 * 注意：目前这里的做法是强关联，强耦合的。 这些功能目前还没有一个更简单的做法， Lib.EnumCode.converter 和
 * "Lib.TypeCode.converter 是js端实现的一个自动转换代码的函数
 * 
 * @author aaron
 * 
 */
public class ColumnRenderer {
	// col.renderer = App.Code.converter(col.tableName, col.columnName);
	public static final String DICT_RENDERER = "Lib.EnumCode.converter(':table', ':column')";
	public static final String TYPE_RENDERER = "Lib.TypeCode.converter(':type_table',':type_id_column',':type_name_column')";

	public static String dictRenderer(String tableName, String columnName) {
		return DICT_RENDERER.replace(":table", tableName).replace(":column", columnName);
	}

	public static String typeRenderer(String typeTable, String idColumn, String nameColumn) {
		return TYPE_RENDERER.replace(":type_table", typeTable).replace(":type_id_column", idColumn)
				.replace(":type_name_column", nameColumn);
	}
}
