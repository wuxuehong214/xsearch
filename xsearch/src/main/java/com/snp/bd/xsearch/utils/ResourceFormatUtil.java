package com.snp.bd.xsearch.utils;

/**
 * 资源定义标准工具
 * @author Administrator
 *
 */
public class ResourceFormatUtil {
	
	public static final String DEFAULT_ID = "id"; //solr-doc-id  唯一字段名
	
	public static String getSolrIndex(String res){
		return res+"_index";
	}
	
	public static String getHBaseTable(String res){
		return res+"_table";
	}

}
