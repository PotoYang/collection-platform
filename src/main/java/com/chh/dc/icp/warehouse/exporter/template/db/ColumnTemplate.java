package com.chh.dc.icp.warehouse.exporter.template.db;

public class ColumnTemplate {

	private String name; // column name

	private String property; // property name

	private String format; // format (such as yyyy-mm-dd hh24:mi:ss)

	private String isSpan; // set defaultValue as part of sql. 

	private String defaultValue;

	public ColumnTemplate(String name, String property){
		this.name = name;
		this.property = property;
	}

	public String getName(){
		return name;
	}

	public String getProperty(){
		return property;
	}

	public String getFormat(){
		return format;
	}

	public void setFormat(String format){
		this.format = format;
	}

	public String getDefaultValue(){
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue){
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the isSpan
	 */
	public String getIsSpan(){
		return isSpan;
	}

	/**
	 * @param isSpan the isSpan to set
	 */
	public void setIsSpan(String isSpan){
		this.isSpan = isSpan;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}