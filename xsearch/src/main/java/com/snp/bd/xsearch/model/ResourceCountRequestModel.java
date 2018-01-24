package com.snp.bd.xsearch.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="资源统计接口")
public class ResourceCountRequestModel {
	
	@ApiModelProperty(value="需要统计的资源",required=true)
	private List<String> resources;
	@ApiModelProperty(value="查询内容,lucene语法")
	private String query;
	@ApiModelProperty(value="过滤条件,lucene语法")
	private List<String> filters;
	@ApiModelProperty(value="超时返回时间ms")
	private int timeAllowed = 10000;  //ms
	
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public int getTimeAllowed() {
		return timeAllowed;
	}
	public void setTimeAllowed(int timeAllowed) {
		this.timeAllowed = timeAllowed;
	}
	public List<String> getFilters() {
		return filters;
	}
	public void setFilters(List<String> filters) {
		this.filters = filters;
	}
	
	
}
