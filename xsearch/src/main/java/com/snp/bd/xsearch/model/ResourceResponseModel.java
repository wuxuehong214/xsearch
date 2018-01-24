package com.snp.bd.xsearch.model;

/**
 * 资源检索返回模型
 * @author Administrator
 *
 */
public class ResourceResponseModel<T> {
	
	private long took;
	private long total;
	private T result;
	public long getTook() {
		return took;
	}
	public void setTook(long took) {
		this.took = took;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}

}
