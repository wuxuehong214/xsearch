package com.snp.bd.xsearch.exceptions;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;



@Provider
public class RestExceptionHandler implements ExceptionMapper<RestException> {
	
	@Override
	public Response toResponse(RestException r) {
		Map<String,String> msg = new HashMap<String,String>();
		msg.put("eror-msg", r.getService()+":"+r.getMessage());
		return Response.status(r.getStatus()).type(MediaType.APPLICATION_JSON).entity(com.alibaba.fastjson.JSONObject.toJSON(msg)).build();
	}

}
