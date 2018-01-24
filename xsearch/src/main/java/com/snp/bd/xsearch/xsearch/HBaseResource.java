package com.snp.bd.xsearch.xsearch;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.snp.bd.xsearch.model.HBaseRecordModel;
import com.snp.bd.xsearch.service.IHBaseService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Root resource (exposed at "myresource" path)
 */
@Component
@Path("v1/hbases")
@Api(value="hbase资源管理服务")
public class HBaseResource {

	private Logger logger = Logger.getLogger("REST-HBase服务");
	@Autowired
	private IHBaseService hbaseService;
	
	
	
	@PUT
	@Path("/records")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="单资源智能检索接口")
	public Response put(HBaseRecordModel record){
		logger.info("请求写入列记录到hbase表:"+record.getTable());
		hbaseService.put(record.getTable(), record.getRowkey(), record.getFamily(), record.getColumn(), record.getValue());
		return Response.status(204).entity("{\"created\":\"true\"}").build();
	}
}
