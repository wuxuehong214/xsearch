package com.snp.bd.xsearch.exceptions;

import javax.ws.rs.core.Response.Status;

public class SolrException extends RestException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1240828886567276214L;

	public SolrException(Status status, String message) {
		super("Solr",status, message);
	}

}
