package com.snp.bd.xsearch.exceptions;

import javax.ws.rs.core.Response.Status;

public class HBaseException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6686033034714717574L;

	public HBaseException(Status status, String message) {
		super("HBase",status, message);
	}

}
