package com.snp.bd.xsearch.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.RegionSplitter;
import org.apache.luna.client.LunaAdmin;
import org.apache.luna.filter.FullTextFilter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.Create;

public class LunaSample {

  private final static Log LOG = LogFactory.getLog(LunaSample.class.getName());
  private static Configuration conf;

  public static void main(String[] args) {
    try {
      conf = LoginUtilForSolr.login();
      testFullTextScan();
      System.exit(0);
    } catch (IOException e) {
      LOG.error("Failed to run luna sample.");
      System.exit(-1);
    }
  }

  public static void testFullTextScan() throws IOException {
    String mapping = System.getProperty("user.dir") + File.separator + "conf";

    Create create = new Create();
    create.setCollectionName("testCollection");
    create.setConfigName("confWithHBase");
    create.setNumShards(3);
    create.setReplicationFactor(2);

    HTableDescriptor desc = new HTableDescriptor(TableName.valueOf("testTable"));
    desc.addFamily(new HColumnDescriptor(Bytes.toBytes("f")));

    // 1. create table and collection
    try (LunaAdmin admin = new LunaAdmin(conf, "/solr");) {
      admin.createTable(
          desc, RegionSplitter
              .newSplitAlgoInstance(conf, RegionSplitter.HexStringSplit.class.getName()).split(10),
          create, mapping);
      admin.addCollection(TableName.valueOf("testTable"),
          create.setCollectionName("testCollection2"), mapping);

      // 2. put data.
      Table table = admin.getTable(TableName.valueOf("testTable"));
      int i = 0;
      while (i < 10) {
        byte[] row = Bytes.toBytes(i + "+sohrowkey");
        Put put = new Put(row);
        put.add(Bytes.toBytes("f"), Bytes.toBytes("s"), Bytes.toBytes("sku" + i));
        put.add(Bytes.toBytes("f"), Bytes.toBytes("n"), Bytes.toBytes("name" + i));
        put.add(Bytes.toBytes("f"), Bytes.toBytes("c"), Bytes.toBytes("cat" + i));
        put.add(Bytes.toBytes("f"), Bytes.toBytes("m"), Bytes.toBytes("other_m" + i));
        table.put(put);
        i++;
      }

      // 3. scan table.
      Scan scan = new Scan();
      SolrQuery query = new SolrQuery();
      query.setQuery("name:name1 AND sku:sku1");
      Filter filter = new FullTextFilter(query, "testCollection");
      scan.setFilter(filter);
      ResultScanner scanner = table.getScanner(scan);
      LOG.info("-----------------records----------------");
      for (Result r = scanner.next(); r != null; r = scanner.next()) {
        for (Cell cell : r.rawCells()) {
          LOG.info(Bytes.toString(CellUtil.cloneRow(cell)) + ":"
              + Bytes.toString(CellUtil.cloneFamily(cell)) + ","
              + Bytes.toString(CellUtil.cloneQualifier(cell)) + ","
              + Bytes.toString(CellUtil.cloneValue(cell)));
        }
      }
      LOG.info("-------------------end------------------");

      // 4. delete collection.
      admin.deleteCollection(TableName.valueOf("testTable"), "testCollection");

      // 5. delete table.
      admin.deleteTable(TableName.valueOf("testTable"));
    }
  }

}
