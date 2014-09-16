package me.suisui.framework.extjs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.google.common.base.Function;

@JsonInclude(Include.NON_NULL)
public class Column {
	@JsonIgnore
	Function transfromer;
	
    @JsonIgnore
    String columnName;
    // align:left

    Boolean hidden;
    String dataIndex;
    String text;
    ColumnEditor editor = null;
    @JsonRawValue
    String renderer;// in Ext.util.Format
    String xtype;// "gridcolumn";
    // for date,number column
    String format;
    // xtype == object column 时的内嵌column设置
    Boolean sortable;
    Integer width;
    Column column;
    // for multiple grid head
    List<Column> columns;

    public Column() {
    }

    public Column(String dataIndex) {
        this.dataIndex = dataIndex;
    }
    
    public Column(String text, String dataIndex) {
    	this.text = text;
    	this.dataIndex = dataIndex;
    }
    public Column(String text, String dataIndex,Function transfromer) {
    	this.text = text;
    	this.dataIndex = dataIndex;
    	this.transfromer = transfromer;
    }
    public Column(String text, String dataIndex,Integer width) {
    	this.text = text;
    	this.dataIndex = dataIndex;
    	this.width = width;
    }
    public Column(String text, String dataIndex,Integer width, String format) {
    	this.text = text;
    	this.dataIndex = dataIndex;
    	this.width = width;
    	this.format = format;
    }

    public void setTransfromer(Function transfromer) {
		this.transfromer = transfromer;
	}
    public Function getTransfromer() {
		return transfromer;
	}
    
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public ColumnEditor getEditor() {
        return editor;
    }

    public void setEditor(ColumnEditor editor) {
        this.editor = editor;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public String getXtype() {
        return xtype;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Boolean getSortable() {
        return sortable;
    }

    public void setSortable(Boolean sortable) {
        this.sortable = sortable;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

}
