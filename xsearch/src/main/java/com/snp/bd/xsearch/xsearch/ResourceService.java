package com.snp.bd.xsearch.xsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.snp.bd.xsearch.model.ResourceCountRequestModel;
import com.snp.bd.xsearch.model.ResourceRequestModel;
import com.snp.bd.xsearch.model.ResourceResponseModel;
import com.snp.bd.xsearch.service.IHBaseService;
import com.snp.bd.xsearch.service.IResourceService;
import com.snp.bd.xsearch.service.ISolrService;
import com.snp.bd.xsearch.service.impl.HBaseServiceImpl;
import com.snp.bd.xsearch.utils.ResourceFormatUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("v1/resources")
@Api("智能检索服务")
public class ResourceService {
	
	private Logger logger = Logger.getLogger("REST-智能检索服务");
	
	@Autowired
	private IResourceService resourceService;
	
	@Autowired
	private IHBaseService hbaseService;
	@Autowired
	private ISolrService solrService;
	
	
	@GET
	@Path("/rwhx/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="请求任务画像信息，请求参数为手机号")
	public Response get(@PathParam("id")String id){
		logger.info("请求查询人物画像:"+id);
		Map<String,Object> map = hbaseService.get(ResourceFormatUtil.getHBaseTable("rwhx"), id,null);
		Map<String,String> descp = new HashMap<String, String>();
		descp.put("a", "居住地");
		descp.put("b", "常活动地");
		descp.put("c", "电话接次数");
		descp.put("d", "电话打次数");
		descp.put("e", "通话时长");
		descp.put("f", "联系的人数");
		descp.put("g", "发短信次数");
		descp.put("h", "接短信次数");
		descp.put("i", "轨迹匹配天数");
		descp.put("j", "本地待天数");
		descp.put("k", "去网吧次数");
		descp.put("l", "去某一网吧次数(最大)");
		descp.put("m", "一次上网最大时长");
		descp.put("n", "上网总时长");
		descp.put("o", "寄快递次数");
		descp.put("p", "收快递次数");
		descp.put("q", "轨迹未发生明显变化天数");
		descp.put("hd", "基于话单数据分析的关联手机号信息，多个手机号以逗号分隔");
		
		
		
		
		map.put("desc", descp);
		return Response.ok(map).header("charset", "utf-8").build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="根据ID查询资源")
	public Response get(@PathParam("id")String id,@QueryParam("res")String res){
		logger.info("请求查询资源id:"+id);
		Map<String,Object> map = hbaseService.get(ResourceFormatUtil.getHBaseTable(res), id,null);
		return Response.ok(map).header("charset", "utf-8").build();
	}
	
//	@GET
//	@Path("")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	@ApiOperation(value="单资源智能检索接口")
//	public Response search(@QueryParam("res")String res,@QueryParam("q")String q){
//		logger.info("请求单资源智能检索:res"+res+"\tq:"+q);
//		ResourceResponseModel<List<Map<String,Object>>> response = solrService.queryIndex(res, q, null);
////		logger.info(JSONObject.toJSONString(response));
//		return Response.ok(response).build();
//	}
	
	
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="单资源智能检索接口")
	public Response search(ResourceRequestModel request){
		logger.info("请求单资源智能检索");
//		ResourceRequestModel request = JSONObject.parseObject(r, ResourceRequestModel.class);
		ResourceResponseModel<List<Map<String,Object>>> response = solrService.queryIndex(request);
		return Response.ok(response).build();
	}
	
	@POST
	@Path("/count")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value="统计资源记录数")
	public Response count(ResourceCountRequestModel request){
		logger.info("请求请求资源记录数");
		ResourceResponseModel<Map<String,Long>> response = solrService.countIndex(request);
		return Response.ok(response).build();
	}
	

}
