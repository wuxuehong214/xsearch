package com.snp.bd.xsearch.model;

/**
 * hbase表 列记录  数据模型
 * @author Administrator
 *
 */
public class HBaseRecordModel {
	
	private String table;
	private String rowkey;
	private String family;
	private String column;
	private String value;
	
	
	public String getRowkey() {
		return rowkey;
	}
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
