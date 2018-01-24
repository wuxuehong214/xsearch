package com.snp.bd.xsearch.model;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 智能检索请求模型
 * 
 * @author Administrator
 *
 */
@ApiModel(value="资源检索请求模型")
public class ResourceRequestModel {

	@ApiModelProperty(value="资源名称",required=true)
	private String resource;
	@ApiModelProperty(value="查询内容,lucene语法")
	private String query;
	@ApiModelProperty(value="分页开始位置,默认0")
	private int from = 0;
	@ApiModelProperty(value="分页大小,默认10")
	private int size = 10;
	@ApiModelProperty(value="过滤条件,lucene语法")
	private String filter;
	@ApiModelProperty(value="需要返回的字段,null则返回全部")
	private List<String> fields;
	@ApiModelProperty(value="超时返回时间ms")
	private int timeAllowed = 10000;  //ms
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public int getTimeAllowed() {
		return timeAllowed;
	}

	public void setTimeAllowed(int timeAllowed) {
		this.timeAllowed = timeAllowed;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

}
