package me.suisui.framework.extjs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MetaData {
	@JsonIgnore
	String entity;
	@JsonIgnore
	String tableName;

	String root;
	String idProperty;
	List<Field> fields;
	List<Column> columns;

	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getIdProperty() {
		return idProperty;
	}

	public void setIdProperty(String idProperty) {
		this.idProperty = idProperty;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

}
