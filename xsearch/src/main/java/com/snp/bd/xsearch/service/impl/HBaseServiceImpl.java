package com.snp.bd.xsearch.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Bytes;
import org.jboss.logging.Logger;

import com.snp.bd.xsearch.exceptions.HBaseException;
import com.snp.bd.xsearch.service.IHBaseService;
import com.snp.bd.xsearch.utils.LoginUtil;
import com.snp.bd.xsearch.utils.ResourceFormatUtil;

public class HBaseServiceImpl implements IHBaseService {

	private static Logger logger = Logger.getLogger("HBase服务");
	private static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";
	private static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";
	private static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop";

	private static Configuration conf = null;
	private static String krb5File = null;
	private static String userName = null;
	private static String userKeytabFile = null;

	private static Connection conn = null;

	static {
		try {
			init();
			login();
			logger.info("连接认证HBase服务成功!");
			conn = ConnectionFactory.createConnection(conf);
			logger.info("连接HBase服务成功!");
		} catch (Exception e) {
			logger.warn("连接认证HBase服务异常：" + e.getMessage(), e);
		}
	}

	public static void main(String args[]) {
		IHBaseService hbase = new HBaseServiceImpl();
//		List<String> rowkeys = new ArrayList<String>();
//		rowkeys.add("430902198702288511");
//		rowkeys.add("430725198610243037");
//		rowkeys.add("430111198402141748");
		Map<String, Object> m = hbase.get("rkxx", "430111198402141748",null);
		logger.info(m);
	}

	@Override
	public Map<String, Object> get(String table, String rowkey,List<String> fields) {
		logger.info("请求查询HBase，查询表[" + table + "],查询rowkey:[" + rowkey + "]");
		Map<String, Object> map = new HashMap<String, Object>();
		// Specify the column family name.
		// byte[] familyName = Bytes.toBytes("f1");
		// Specify the column name.
		// byte[][] qualifier = { Bytes.toBytes("name"),
		// Bytes.toBytes("address") };
		// Specify RowKey.

		byte[] rowKey = Bytes.toBytes(rowkey);

		Table t = null;
		try {
			// Create the Configuration instance.
			t = conn.getTable(TableName.valueOf(table));

			// Instantiate a Get object.
			Get get = new Get(rowKey);

			// Set the column family name and column name.
			// Submit a get request.
			Result result = t.get(get);

			if(result.isEmpty())throw new HBaseException(Status.NOT_FOUND, "未查询到指定资源记录:"+rowkey);
			map.put(ResourceFormatUtil.DEFAULT_ID, rowkey);
			
			
			
			String field;
			// Print query results.
			for (Cell cell : result.rawCells()) {
				field = Bytes.toString(CellUtil.cloneQualifier(cell));
				if(fields == null || fields.contains(field))
				map.put(field, Bytes.toString(CellUtil.cloneValue(cell)));
				// logger.info(Bytes.toString(CellUtil.cloneRow(cell)) + ":"
				// + Bytes.toString(CellUtil.cloneFamily(cell)) + ","
				// + Bytes.toString(CellUtil.cloneQualifier(cell)) + ","
				// + Bytes.toString(CellUtil.cloneValue(cell)));
			}
			// LOG.info("Get data successfully.");
		} catch (IOException e) {
			logger.warn("从hbase表读取数据异常:" + e.getMessage(), e);
			// LOG.error("Get data failed ", e);
			throw new HBaseException(Status.INTERNAL_SERVER_ERROR, "从HBase表读取数据异常:"+e.getMessage());
		} finally {
			if (t != null) {
				try {
					t.close();
				} catch (IOException e) {
					logger.warn("hbase表关闭异常:" + e.getMessage(), e);
				}
			}
		}
		return map;
	}

	private static void init() throws IOException {
		// Default load from conf directory
		conf = HBaseConfiguration.create();
		String userdir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		conf.addResource(new Path(userdir + "core-site.xml"));
		conf.addResource(new Path(userdir + "hdfs-site.xml"));
		conf.addResource(new Path(userdir + "hbase-site.xml"));

	}

	private static void login() throws IOException {
		if (User.isHBaseSecurityEnabled(conf)) {
			String userdir = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
			userName = "nifi";
			userKeytabFile = userdir + "user.keytab";
			krb5File = userdir + "krb5.conf";

			/*
			 * if need to connect zk, please provide jaas info about zk. of
			 * course, you can do it as below:
			 * System.setProperty("java.security.auth.login.config", confDirPath
			 * + "jaas.conf"); but the demo can help you more : Note: if this
			 * process will connect more than one zk cluster, the demo may be
			 * not proper. you can contact us for more help
			 */
			LoginUtil.setJaasConf(ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME, userName, userKeytabFile);
			LoginUtil.setZookeeperServerPrincipal(ZOOKEEPER_SERVER_PRINCIPAL_KEY, ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);
			LoginUtil.login(userName, userKeytabFile, krb5File, conf);
		}
	}

	@Override
	public Map<String, Map<String, Object>> get(String table, List<String> rowkeys,List<String> fields) {
		logger.info("请求批量查询HBase，查询表[" + table + "],查询rowkey:[" + Arrays.toString(rowkeys.toArray()) + "]");
		Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

		Table t = null;
		try {
			t = conn.getTable(TableName.valueOf(table));
			String field;
			for (String rowkey : rowkeys) {

				try {
					byte[] rk = Bytes.toBytes(rowkey);
					Get get = new Get(rk);
					Result result = t.get(get);
					Map<String, Object> m = new HashMap<String, Object>();
					for (Cell cell : result.rawCells()) {
						field =  Bytes.toString(CellUtil.cloneQualifier(cell));
						if(fields == null || fields.contains(field))
						m.put(field, Bytes.toString(CellUtil.cloneValue(cell)));
					}
					map.put(rowkey, m);
				} catch (Exception e) {
					logger.warn("获取记录异常:" + rowkey, e);
					continue;
				}
			}
		} catch (IOException e) {
			logger.warn("从hbase表读取数据异常:" + e.getMessage(), e);
			throw new HBaseException(Status.INTERNAL_SERVER_ERROR, "从HBase表读取数据异常:"+e.getMessage());
		} finally {
			if (t != null) {
				try {
					t.close();
				} catch (IOException e) {
					logger.warn("hbase表关闭异常:" + e.getMessage(), e);
				}
			}
		}
		return map;
	}

	@Override
	public void put(String table, String rowkey, String family, String column, String value) {
		// TODO Auto-generated method stub
		logger.info("请求写入数据到表:"+table+"\trowkey:"+rowkey+"\t列簇:"+family+"\t列:"+column+"\t值"+value);
		
		try {
			Table t = conn.getTable(TableName.valueOf(table));
			
			Put put = new Put(Bytes.toBytes(rowkey));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(value));
			
			t.put(put);
		} catch (IOException e) {
			logger.warn("写数据到hbase表异常:"+e.getMessage(),e);
			throw new HBaseException(Status.INTERNAL_SERVER_ERROR, "写数据到hbase表异常:"+e.getMessage());
		}
		
	
	}

}
