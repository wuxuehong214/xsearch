package com.snp.bd.xsearch.service;

import java.util.List;
import java.util.Map;

import com.snp.bd.xsearch.model.ResourceCountRequestModel;
import com.snp.bd.xsearch.model.ResourceRequestModel;
import com.snp.bd.xsearch.model.ResourceResponseModel;

/**
 * solr检索服务
 * @author Administrator
 *
 */
public interface ISolrService {

	
	public ResourceResponseModel<List<Map<String,Object>>> queryIndex(String resource,String query, Map<String,Object> conditions);
	
	
	/**
	 * 资源检索
	 * @param request
	 * @return
	 */
	public ResourceResponseModel<List<Map<String,Object>>> queryIndex(ResourceRequestModel request);
	
	/**
	 * 资源统计
	 * @param request
	 * @return
	 */
	public ResourceResponseModel<Map<String,Long>> countIndex(ResourceCountRequestModel request);
	
}
