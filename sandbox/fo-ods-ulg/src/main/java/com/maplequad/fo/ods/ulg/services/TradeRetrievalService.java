package com.maplequad.fo.ods.ulg.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.ulg.dao.*;
import com.maplequad.fo.ods.ulg.dao.Query;
import com.maplequad.fo.ods.ulg.exceptions.RecordNotFoundException;
import com.maplequad.fo.ods.ulg.model.TradeAttribute;
import com.maplequad.fo.ods.ulg.model.TradeInfo;
import com.maplequad.fo.ods.ulg.repository.TradesTable;
import com.maplequad.fo.ods.ulg.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.EQUAL;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.GREATER;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.LESS;
import static com.maplequad.fo.ods.ulg.dao.Predicate.allVersions;

/***
 * TradeRetrievalService - reads trades from the bigtable for the given criteria
 *
 * @author Madhav Mindhe
 * @since :   04/08/2017
 */
public class TradeRetrievalService extends AbstractTradeCoreService {

    private static final Long MAX_RESULTS = Long.MAX_VALUE;
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeRetrievalService.class);
    private static final String DOT = ".";
    private byte[] tableName = null;
    private Table table;
    private Connection conn = null;
    private String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-converter.json";
    private TradeInfo tradeInfo;

    /***
     * Default constructor is made private
     */
    private TradeRetrievalService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with tradeTableName as input");
    }

    /**
     * This constructor is used  to create an instance of TradeRetrievalService
     *
     * @param tradeTableName
     * @throws Exception
     */
    public TradeRetrievalService(String assetClass, String tradeTableName) {
        this.tableName = Bytes.toBytes(tradeTableName);
        this.conn = BigTableHandle.getInstance().getConnection();
        try {
            TradesTable.createTable(tradeTableName);
            table = this.conn.getTable(TableName.valueOf(tableName));
        } catch (Exception ioe) {
            LOGGER.error("ERROR : Exception occurred while connecting to Bigtable {}", ioe);
        }

        tradeJson = tradeJson.replace("${ASSET_CLASS}", assetClass);

        try {
            tradeInfo =
                    JsonIterator.deserialize(FileUtils.readFileToString(new File(CONF+tradeJson), ENC), TradeInfo.class);
        } catch (IOException ioe) {
            try {
                tradeInfo =
                        JsonIterator.deserialize(FileUtils.readFileToString
                                (new File(this.getClass().getClassLoader().getResource(tradeJson).getFile()), ENC), TradeInfo.class);
            }
            catch (IOException ioe2) {
                LOGGER.error("ERROR : Unable to find trade file at {}", tradeJson);
            }
        }

        //timer = this.startMetric("retrieve", LOGGER);
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register("retrieve", timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);
    }

    //getByRowKeys
    public List<Map> serve(List<String[]> rowKeys) {
        LOGGER.trace("Entered serve");
        List tradeList = new ArrayList();
        rowKeys.forEach((rowKey) -> {
            tradeList.add(this.getByRowKey(rowKey[0],rowKey[1]));
            //tradeList.add(this.getAllVersions(rowKey[0]));
        });
        LOGGER.trace("Exited serve");
        return tradeList;
    }

    //getByRowKey
    public Map getByRowKey(String tradeId, String tradeValidTime){
        String rowKey = createRowKey(tradeId, tradeValidTime);
        LOGGER.trace("Get a single trade by row key={}", rowKey);

        Result result = withTimer(this.timer, "readByRowKey", () -> table.get(new Get(Bytes.toBytes(rowKey))));

        if (result.isEmpty()) {
            throw new RecordNotFoundException("Cannot find record by rowKey = " + rowKey);
        }

        Map trade = this.readTrade(result);
        return trade;
    }

    public Map getLatest(String tradeId) {
        return getLatestAsOn(tradeId,0,0);
    }

    //getLatest
    public Map getLatestAsOn(String tradeId, long minTransTime, long maxTransTime) {

        Query query = Query.forAll(1, allVersions("tradeId", tradeId));
        List<Map> trades = this.readByQuery(query, minTransTime, maxTransTime);
        if (trades.isEmpty()) {
            throw new RecordNotFoundException("Cannot find a latest trade by id = " + tradeId);
        }
        if (trades.size() > 1) {
            throw new RuntimeException("Got more than one latest trade for trade by id = " + tradeId);
        }

        Map res = trades.get(0);
        return res;
    }

    //getAllVersionsAsOn
    public List<Map> getAllVersionsAsOn(String tradeId, long minTransTime, long maxTransTime)  {
        return this.readByQuery(Query.forAll(allVersions("tradeId", tradeId)));
    }

    //getAllVersions
    public List<Map> getAllVersions(String tradeId) {
        return this.getAllVersionsAsOn(tradeId,0,0);
    }


    public List<Map> getTradesForExchange(String exch){

       return this.readByQuery( Query.forAll(new Predicate("product","exchange",exch, EQUAL)));
    }
    public List<Map> getTradesForCFICode(String cfiCode){

        return this.readByQuery( Query.forAll(new Predicate("product","cfiCode",cfiCode, EQUAL)));
    }
    /**
     * This method just converts the result of a single trade into tradeMap.
     *
     * @param result
     * @return Map - containing key as family.qualifier and value as value of the qualifier
     */
    private Map readTrade(Result result) {
        Map tradeMap = new HashMap();
        String key, family, qualifier;
        TradeAttribute attribute;
        Iterator attrItr = tradeInfo.getAttributes().iterator();
        while(attrItr.hasNext()){
            attribute = (TradeAttribute) attrItr.next();
            family = attribute.getFamily();
            qualifier = attribute.getQualifier();
            key = StringUtils.concat(family,DOT,qualifier);
            tradeMap.put(key,Bytes.toString(result.getValue(family.getBytes(), qualifier.getBytes())));
        }
        LOGGER.trace("tradeMap : {} ",tradeMap);
        return tradeMap;
    }


    private List<Map> readByQuery(IQuery query) {
        return readByQuery(query, 0,0);
    }

        /**
         * This method calls retrieve method to read trades from the bigtable.
         *
         * @param query
         * @param minTransTime
         * @param maxTransTime
         * @return List<Map> List of trades in a map structure
         */

    private List<Map> readByQuery(IQuery query, long minTransTime, long maxTransTime) {
        LOGGER.info("Executing query {}", query);

        Scan scan = new Scan();
        scan.setFilter(query.getFilter());
        if(minTransTime != 0 || maxTransTime != 0) {
            try {
                scan.setTimeRange(minTransTime, maxTransTime);
            }catch (IOException ioe){
                LOGGER.error("ERROR : Unable to set the dateRange for Scan - {}", ioe);
            }
        }
        long maxResultSize = query.getMaxResultSize().orElse(MAX_RESULTS);

       // List<Map> trades = withTimer(timer, "retrieve", () -> retrieve(scan, maxResultSize));
        List<Map> trades = null;
        try {
            trades = retrieve(scan, maxResultSize);
        }catch (IOException ioe){
            LOGGER.error("ERROR : Unable retrieve trades - {}", ioe);
        }
        return trades;

    }


    /***
     * This method calls readTrade method recursively to read trades from the bigtable.
     * @param scan
     * @param maxResultSize
     * @return List<Map> List of trades in a map structure
     * */
    private List<Map> retrieve(Scan scan, long maxResultSize) throws IOException {
        List<Map> result = new ArrayList<>();
        try (ResultScanner scanner = table.getScanner(scan)) {
            Iterator<Result> rowIt = scanner.iterator();
            while (result.size() < maxResultSize && rowIt.hasNext()) {
                Map trade = this.readTrade(rowIt.next());
                result.add(trade);
                LOGGER.debug("read {}", trade);
            }
            LOGGER.info("Done. Read {} trades", result.size());
            return result;
        }
    }

    /**
     * This method creates a rowkey based on the given tradeId and travelValidTime
     *
     * @param tradeId
     * @param tradeValidTime
     * @return String with rowkey to be used for inserting new trade
     */
    private String createRowKey(String tradeId, String tradeValidTime) {
        LOGGER.trace("Entered createRowKey");
        String rowKey = StringUtils.concat
                (new String[]{tradeId, DOT, tradeValidTime});
        LOGGER.trace("Exited createRowKey");
        return rowKey;
    }
}
