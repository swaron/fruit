package me.suisui.framework.paging;

/**
 * work with JoinParam to specify which columns are used to join
 * 
 * @author aaron
 * 
 */
public class JoinColumn {
	String masterField;
	String referField;

	public JoinColumn() {
	}
	
	
	public JoinColumn(String masterField, String referField) {
		super();
		this.masterField = masterField;
		this.referField = referField;
	}

	public String getMasterField() {
		return masterField;
	}

	public void setMasterField(String masterField) {
		this.masterField = masterField;
	}

	public String getReferField() {
		return referField;
	}

	public void setReferField(String referField) {
		this.referField = referField;
	}

}
