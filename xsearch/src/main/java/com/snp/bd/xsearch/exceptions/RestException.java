package com.snp.bd.xsearch.exceptions;

import javax.ws.rs.core.Response.Status;

public class RestException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8067180108236372827L;
	private String service;
	private Status status;
	private String message;
	
	public RestException(String service,Status status,String message){
		this.service = service;
		this.status = status;
		this.message = message;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
