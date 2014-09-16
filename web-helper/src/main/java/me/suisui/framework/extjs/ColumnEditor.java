package me.suisui.framework.extjs;

import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * 注意：目前这里的做法是强耦合的。 这些功能目前还没有一个更简单的做法，
 * Lib.EnumCode.store 和 "Lib.TypeCode.store 是js端实现的一个自动转换代码的类。
 * @author aaron
 *
 */
public class ColumnEditor {
	// col.renderer = App.Code.converter(col.tableName, col.columnName);
	// col.field = {
	// xtype : 'combobox',
	// store : App.Code.store(col.tableName, col.columnName),
	// queryMode : 'local',
	// displayField : 'name',
	// valueField : 'code'
	// }
	public static final String dict_store = "Lib.EnumCode.store(':table', ':column')";
	public static final String type_store = "Lib.TypeCode.store(':type_table',':type_id_column',':type_name_column')";
	String xtype = "combobox";
	@JsonRawValue
	String store = dict_store;
	String queryMode = "local";
	String displayField = "name";
	String valueField = "code";

	public static ColumnEditor dictStoreEditor(String table, String column) {
		ColumnEditor editor = new ColumnEditor();
		editor.store = dict_store.replace(":table", table).replace(":column", column);
		return editor;
	}

	public static ColumnEditor typeStoreEditor(String typeTable, String idColumn, String nameColumn) {
		ColumnEditor editor = new ColumnEditor();
		editor.store = type_store.replace(":type_table", typeTable).replace(":type_id_column", idColumn)
				.replace(":type_name_column", nameColumn);
		return editor;
	}
}
