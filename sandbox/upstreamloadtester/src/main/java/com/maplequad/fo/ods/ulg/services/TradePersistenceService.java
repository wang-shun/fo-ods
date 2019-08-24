package com.maplequad.fo.ods.ulg.services;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.maplequad.fo.ods.ulg.repository.TradesTable;
import com.maplequad.fo.ods.ulg.utils.StringUtils;

/***
 * PersistenceService - takes bigtable ready hashmap and inserts database row in bigtable
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradePersistenceService {

    public static final int MAX_VERSION = 2_000_000;
    private byte[] tableName = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(TradePersistenceService.class);
    private Table table;
    private static final String COLON = ":";
    private static final String SEPARATOR = "-";
    private static final String TRADE_ID = "headers:osTradeId";
    private static final String TRADE_VERSION = "headers:osTradeVersion";
    private Connection conn = null;
    /***
     * Default constructor is made private
     */
    private TradePersistenceService(){
        throw new IllegalArgumentException("ERROR : Please use constructor with projectId and instanceId as input");
    }

    /**
     * This constructor is used  to create an instance of TradePersistenceService
     * for a given google project id and instance id
     *
     * @param projectId
     * @param instanceId
     * @throws Exception
     */
    public TradePersistenceService(String projectId, String instanceId, String tradeTableName) {
        tableName = Bytes.toBytes(tradeTableName);
        conn = BigtableConfiguration.connect(projectId, instanceId);
        this.setTable(projectId, instanceId);
    }

    private void setTable(String projectId, String instanceId){
        if(table == null) {
            try {
                if(conn == null){
                    conn = BigtableConfiguration.connect(projectId, instanceId);
                }

/*                // The admin API lets us create, manage and delete tables
                Admin admin = conn.getAdmin();
                // [END connecting_to_bigtable]

                if (!admin.tableExists(TableName.valueOf(TABLE_NAME_TRADES))) {
                    // [START creating_a_table]
                    // Create a table with a required column families
                    HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME_TRADES));
                    descriptor.addFamily(new HColumnDescriptor(Bytes.toBytes("headers")));
                    descriptor.addFamily(new HColumnDescriptor(Bytes.toBytes("product")));
                    LOGGER.info("Creating table {}", descriptor.getNameAsString());
                    admin.createTable(descriptor);
                    // [END creating_a_table]
                }*/
                TradesTable.createTable(this.conn,tableName);
                table = conn.getTable(TableName.valueOf(tableName));
                } catch (Exception ioe) {
                    LOGGER.error("ERROR : Exception occured while connecting to Bigtable {}", ioe);
                }
            }
        }

    /**
     * This method calls persist method recursively to insert trades into the bigtable.
     *
     * @param tradeList
     */
    public void serve(List<Map<String, String>> tradeList) {
        LOGGER.info("Entered serve");
        Iterator<Map<String, String>> trdItr = tradeList.iterator();
        while (trdItr.hasNext()) {
            this.persist(trdItr.next());
        }
        LOGGER.info("Exited serve");
    }

    /**
     * This generic method can be used to persist trade of any product and asset class
     *
     * @param trade
     */
    private void persist(Map trade) {
        LOGGER.info("Entered persist");
        String rowKey = createRowKey(trade);

        Put put = new Put(Bytes.toBytes(rowKey));

        trade.forEach((k, v) -> {
            String[] keys = ((String) k).split(COLON);
            //LOGGER.info("k keys {}, {}",k, keys);
            put.addColumn(Bytes.toBytes(keys[0]), Bytes.toBytes(keys[1]), Bytes.toBytes(v.toString()));
        });
        try {
            table.put(put);
        } catch (IOException ioe) {
            LOGGER.error("ERROR : Exception occured while persisting data in table {}", ioe.getMessage());
            ioe.printStackTrace();
        }
        LOGGER.info("Exited persist");
    }


    /**
     * This method creates a rowkey based on the given trade information
     * @param trade
     * @return String with rowkey to be used for inserting new trade
     */
    private String createRowKey(Map trade) {
        LOGGER.info("Entered createRow");
        String tradeId = trade.get(TRADE_ID).toString();
        int tradeVersion = Integer.parseInt(trade.get(TRADE_VERSION).toString());
        String rowKey = StringUtils.concat
                (new String[] {tradeId, SEPARATOR, String.valueOf (MAX_VERSION - tradeVersion)});
        LOGGER.info("Exited createRow");
        return rowKey;
    }
    
    public void close() throws Exception{
    	//this.conn.close();
    }

}
