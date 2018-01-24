package com.snp.bd.xsearch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.Response.Status;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jboss.logging.Logger;

import com.snp.bd.xsearch.exceptions.SolrException;
import com.snp.bd.xsearch.model.ResourceCountRequestModel;
import com.snp.bd.xsearch.model.ResourceRequestModel;
import com.snp.bd.xsearch.model.ResourceResponseModel;
import com.snp.bd.xsearch.service.IHBaseService;
import com.snp.bd.xsearch.service.ISolrService;
import com.snp.bd.xsearch.utils.ResourceFormatUtil;
import com.snp.bd.xsearch.utils.SolrLoginUtil;

public class SolrServiceImpl implements ISolrService {

	private static Logger logger = Logger.getLogger("sorl服务");
	// 是否启用Kerberos认证
	private static String SOLR_KBS_ENABLED;
	private static int zkClientTimeout;
	private static int zkConnectTimeout;
	// Zookeeper 访问url
	private static String ZK_URL;
	// zookeeper principle
	private static String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL;
	// 索引名称
	private static String COLLECTION_NAME;
	private static String DEFAULT_CONFIG_NAME;
	private static int shardNum;
	private static int replicaNum;
	// 登录认证账号
	private static String principal;

	private static CloudSolrClient cloudSolrClient;
	
	static {
		try {
			initProperties();
			if (SOLR_KBS_ENABLED.equals("true")) {
				login();
			}
			logger.info("连接认证Solr服务成功!");
			cloudSolrClient = new CloudSolrClient(ZK_URL);
			cloudSolrClient.setZkClientTimeout(zkClientTimeout);
			cloudSolrClient.setZkConnectTimeout(zkConnectTimeout);
			cloudSolrClient.connect();
			logger.info("连接solr服务成功!");
		} catch (Exception e) {
			logger.warn("连接认证Solr服务异常：" + e.getMessage(), e);
		}
	}

	public static void main(String args[]) {
		ISolrService solr = new SolrServiceImpl();
		solr.queryIndex("rkxx", "\"魏一国\"", null);
	}

	/**
	 * 初始化读取配置文件
	 */
	private static void initProperties() throws Exception {
		Properties properties = new Properties();
		String proPath = System.getProperty("user.dir") + File.separator + "conf" + File.separator
				+ "solr-example.properties";
		try {
			properties.load(new FileInputStream(new File(proPath)));
		} catch (IOException e) {
			throw new Exception("Failed to load properties file : " + proPath);
		}
		SOLR_KBS_ENABLED = properties.getProperty("SOLR_KBS_ENABLED");
		zkClientTimeout = Integer.valueOf(properties.getProperty("zkClientTimeout"));
		zkConnectTimeout = Integer.valueOf(properties.getProperty("zkConnectTimeout"));
		ZK_URL = properties.getProperty("ZK_URL");
		ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = properties.getProperty("ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL");
		COLLECTION_NAME = properties.getProperty("COLLECTION_NAME");
		DEFAULT_CONFIG_NAME = properties.getProperty("DEFAULT_CONFIG_NAME");
		shardNum = Integer.valueOf(properties.getProperty("shardNum"));
		replicaNum = Integer.valueOf(properties.getProperty("replicaNum"));
		principal = properties.getProperty("principal");
	}

	/**
	 * 登录
	 * 
	 * @throws Exception
	 */
	private static void login() throws Exception {
		String path = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		path = path.replace("\\", "\\\\");
		try {
			SolrLoginUtil.setJaasFile(principal, path + "user.keytab");
			SolrLoginUtil.setKrb5Config(path + "krb5.conf");
			SolrLoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);

		} catch (IOException e) {
			throw new Exception("Failed to set security conf");
		}

	}

	@Override
	public ResourceResponseModel<List<Map<String, Object>>> queryIndex(String resource, String query,
			Map<String, Object> conditions) {
		SolrQuery q = new SolrQuery();

		q.setQuery(query);

		System.out.println(q.getQuery());
		IHBaseService hbaseService = new HBaseServiceImpl();

		ResourceResponseModel<List<Map<String, Object>>> resp = new ResourceResponseModel<List<Map<String, Object>>>();

		try {
			QueryResponse response = cloudSolrClient.query(ResourceFormatUtil.getSolrIndex(resource), q);

			resp.setTook(response.getQTime());
			SolrDocumentList docs = response.getResults();
			resp.setTotal(docs.getNumFound());

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			resp.setResult(list);

			logger.info("Query wasted time : " + response.getQTime() + "ms");
			logger.info("Total doc num : " + docs.getNumFound());

			for (SolrDocument doc : docs) {
				String sfzh = doc.getFieldValue(ResourceFormatUtil.DEFAULT_ID).toString();
				Map<String, Object> m = hbaseService.get(ResourceFormatUtil.getHBaseTable(resource), sfzh,null);

				// m.putAll(doc.getFieldValueMap());
				Iterator<String> it = doc.getFieldNames().iterator();
				while (it.hasNext()) {
					String key = it.next();
					m.put(key, doc.getFieldValue(key));
				}
				list.add(m);
			}
			return resp;
		} catch (Exception e) {
			logger.error("Failed to query document", e);
			throw new SolrException(Status.INTERNAL_SERVER_ERROR, "检索异常：" + e.getMessage());
		}

	}

	@Override
	public ResourceResponseModel<List<Map<String, Object>>> queryIndex(ResourceRequestModel request) {
		SolrQuery q = new SolrQuery();
		q.setQuery(request.getQuery());
		q.setFilterQueries(request.getFilter());
		q.setStart(request.getFrom());
		q.setRows(request.getSize());
		q.setTimeAllowed(request.getTimeAllowed());

		IHBaseService hbaseService = new HBaseServiceImpl();
		ResourceResponseModel<List<Map<String, Object>>> resp = new ResourceResponseModel<List<Map<String, Object>>>();

		try {
			QueryResponse response = cloudSolrClient.query(ResourceFormatUtil.getSolrIndex(request.getResource()), q);

			resp.setTook(response.getQTime());
			SolrDocumentList docs = response.getResults();
			resp.setTotal(docs.getNumFound());

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			resp.setResult(list);

			logger.info("Query wasted time : " + response.getQTime() + "ms");
			logger.info("Total doc num : " + docs.getNumFound());

			for (SolrDocument doc : docs) {
				String sfzh = doc.getFieldValue(ResourceFormatUtil.DEFAULT_ID).toString();
				Map<String, Object> m = hbaseService.get(ResourceFormatUtil.getHBaseTable(request.getResource()), sfzh,request.getFields());

				// m.putAll(doc.getFieldValueMap());
				Iterator<String> it = doc.getFieldNames().iterator();
				while (it.hasNext()) {
					String key = it.next();
					m.put(key, doc.getFieldValue(key));
				}
				list.add(m);
			}
			return resp;
		} catch (Exception e) {
			logger.error("Failed to query document", e);
			throw new SolrException(Status.INTERNAL_SERVER_ERROR, "检索异常：" + e.getMessage());
		}
	}

	@Override
	public ResourceResponseModel<Map<String, Long>> countIndex(final ResourceCountRequestModel request) {
		// TODO Auto-generated method stub
		if(request.getResources() == null || request.getResources().size()==0){
			logger.warn("请求统计的资源不能为空");
			throw new SolrException(Status.BAD_REQUEST, "请求统计的资源不能为空");
		}
		if(request.getFilters() != null && request.getFilters().size()!=request.getResources().size()){
			logger.warn("过滤条件需要与资源一一对应,当前过滤条件数与资源数不一致");
			throw new SolrException(Status.BAD_REQUEST, "过滤条件需要与资源一一对应,当前过滤条件数与资源数不一致");
		}
		ResourceResponseModel<Map<String, Long>> response = new ResourceResponseModel<Map<String, Long>>();
		final Map<String,Long> m = new HashMap<String,Long>();
		response.setResult(m);
		List<Thread> threads = new ArrayList<Thread>();
		List<Integer> values = new ArrayList<Integer>();
		for(int i=0;i<request.getResources().size();i++){
			Thread t = new Thread(new CountThread(i, request, m, values));
			t.start();
			threads.add(t);
		}
		
		for (Thread t1 : threads) {
			try {
				t1.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int max_took = 0;
		for(Integer value:values){
			if(value>max_took)max_took = value;
		}
		response.setTook(max_took);
		response.setTotal(response.getResult().size());
		
		return response;
	}

	/**
	 * 资源统计线程
	 * @author Administrator
	 *
	 */
	class CountThread implements Runnable{
		int index;
		ResourceCountRequestModel request;
		Map<String,Long> m;
		List<Integer> values;
		CountThread(int index ,ResourceCountRequestModel request,Map<String,Long> m, List<Integer> values){
			this.index = index;
			this.request = request;
			this.m = m;
			this.values = values;
		}
		@Override
		public void run() {
			String resource = request.getResources().get(index);
			String filter = null;
			if(request.getFilters()!=null)filter = request.getFilters().get(index);
			SolrQuery q = new SolrQuery();
			q.setQuery(request.getQuery());
			q.setFilterQueries(filter);
			q.setRows(0);
			q.setTimeAllowed(request.getTimeAllowed());
			
			try {
				QueryResponse response = cloudSolrClient.query(ResourceFormatUtil.getSolrIndex(resource), q);
				SolrDocumentList docs = response.getResults();
				m.put(resource, docs.getNumFound());
				values.add(response.getQTime());
			} catch (Exception e) {
				logger.warn("统计资源时异常:"+resource,e);
			}
			
		}
	}

	
	
}
