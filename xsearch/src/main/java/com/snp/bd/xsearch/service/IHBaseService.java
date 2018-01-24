package com.snp.bd.xsearch.service;

import java.util.List;
import java.util.Map;

/**
 * hbase 管理服务
 * @author Administrator
 *
 */
public interface IHBaseService {

	/**
	 * 根据rowkey查询记录
	 * @param table
	 * @param rowkey
	 * @return
	 */
	public Map<String,Object> get(String table, String rowkey,List<String> fields);
	
	/**
	 * 批量查询rowkey记录 
	 * @param table
	 * @param rowkeys
	 * @return
	 */
	public Map<String,Map<String,Object>> get(String table,List<String> rowkeys,List<String> fields);
	
	/**
	 * 写数据到数据库表
	 * @param table    表名
	 * @param rowkey   主键
	 * @param family   列簇
	 * @param column   列
	 * @param value    值
	 */
	public void put(String table, String rowkey, String family, String column, String value);
}
