package com.maplequad.fo.ods.ulg.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.maplequad.fo.ods.ulg.dao.BigTableHandle;
import com.maplequad.fo.ods.ulg.repository.TradesTable;
import com.maplequad.fo.ods.ulg.utils.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/***
 * PersistenceService - takes bigtable ready hashmap and inserts database row in bigtable
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradePersistenceService extends AbstractTradeCoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradePersistenceService.class);
    private static final String COLON = ":";
    private static final String DOT = ".";
    private static final String TRADE_ID = "headers:osTradeId";
    private static final String TRADE_VALID_TIME = "event:executionTimestamp";
    private byte[] tableName = null;
    private Table table;
    private Connection conn = null;
    private static int batchSize = 1;

    /***
     * Default constructor is made private
     */
    private TradePersistenceService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with tradeTableName and iBatchSize as input");
    }

    /**
     * This constructor is used  to create an instance of TradePersistenceService
     *
     * @param tradeTableName
     * @param iBatchSize
     * @throws Exception
     */
    public TradePersistenceService(String tradeTableName, int iBatchSize) {
        this.tableName = Bytes.toBytes(tradeTableName);
        this.conn = BigTableHandle.getInstance().getConnection();
        this.batchSize = iBatchSize;
        try {
            TradesTable.createTable(tradeTableName);
            table = this.conn.getTable(TableName.valueOf(tableName));
        } catch (Exception ioe) {
            LOGGER.error("ERROR : Exception occured while connecting to Bigtable {}", ioe);
        }
        //timer = this.startMetric("persist", LOGGER);
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register("persist", timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);
    }

    /**
     * This method calls persist method recursively to insert trades into the bigtable.
     *
     * @param tradeList
     */
    public List<String[]> serve(List<Map<String, String>> tradeList) {
        LOGGER.info("Entered serve to persist {} records", tradeList.size());
        List rowKeys = new ArrayList();
        Iterator<Map<String, String>> trdItr = tradeList.iterator();
        List<Put> putList = new ArrayList<>();

        while (trdItr.hasNext()) {
            while (putList.size() < batchSize && trdItr.hasNext()) {
                Map map = trdItr.next();
                if(map != null) {
                    putList.add(this.prepare(map));
                }
            }
            this.put(putList);
            putList = new ArrayList<>();
        }

        if (putList.size() != 0) {
            this.put(putList);
        }

        tradeList.forEach((m) -> {
            if(m != null) {
                String tradeId = ((Map<String, String>) m).get(TRADE_ID);
                String tradeValidtime = ((Map<String, String>) m).get(TRADE_VALID_TIME);
                rowKeys.add(new String[]{tradeId, tradeValidtime});
            }
        });
        LOGGER.info("Exited serve after inserting {} records",rowKeys.size());
        return rowKeys;
    }

    private void put(List<Put> putList) {
        LOGGER.trace("Persisting {} trades.", putList.size());
        this.withTimer(timer, "persist", () -> {
            try {
                table.put(putList);
            } catch (IOException ioe) {
                LOGGER.error("ERROR : Exception occured while persisting data in table {}", ioe.getMessage());
            }
            return null;
        });
        /*try {
            table.put(putList);
        } catch (IOException ioe) {
            LOGGER.error("ERROR : Exception occured while persisting data in table {}", ioe.getMessage());
        }*/
        LOGGER.trace("Persisted {} trades.", putList.size());
    }

    /**
     * This generic method can be used to persist trade of any product and asset class
     *
     * @param trade
     */
    private Put prepare(Map trade) {
        LOGGER.trace("Entered persist");
        String rowKey = createRowKey(trade);

        Put put = new Put(Bytes.toBytes(rowKey));

        trade.forEach((k, v) -> {
            String[] keys = ((String) k).split(COLON);
            //LOGGER.info("k keys {}, {}",k, keys);
            put.addColumn(Bytes.toBytes(keys[0]), Bytes.toBytes(keys[1]), Bytes.toBytes(v.toString()));
        });
        LOGGER.trace("Exited persist");
        return put;
    }


    /**
     * This method creates a rowkey based on the given trade information
     *
     * @param trade
     * @return String with rowkey to be used for inserting new trade
     */
    private String createRowKey(Map trade) {
        LOGGER.trace("Entered createRowKey");
        String tradeId = trade.get(TRADE_ID).toString();
        String tradeValidTime = trade.get(TRADE_VALID_TIME).toString();
        String rowKey = StringUtils.concat(tradeId, DOT, tradeValidTime);
        LOGGER.trace("rowKey={}",rowKey);
        LOGGER.trace("Exited createRowKey");
        return rowKey;
    }

}
