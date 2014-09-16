package me.suisui.framework.extjs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Field {
	// convert
	// String dateFormat = "time";
	// dateReadFormat
	// dateWriteFormat
	// @JsonRawValue
	// String defaultValue = "undefined";
	// String mapping = "field";
	String name;
	// boolean persist = true;
	// serialize
	// String sortDir = "ASC"; // or "DESC"
	// sortType
	String type;//"auto" // string,int,float,boolean,date

	Boolean useNull;

	public Field(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public Boolean getUseNull() {
        return useNull;
    }

    public void setUseNull(Boolean useNull) {
        this.useNull = useNull;
    }

}
