package com.maplequad.fo.ods.tradecore.lcm.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.tradecore.dao.SpannerHandle;
import com.maplequad.fo.ods.tradecore.exceptions.RecordNotFoundException;
import com.maplequad.fo.ods.tradecore.lcm.model.TradeAttribute;
import com.maplequad.fo.ods.tradecore.lcm.model.TradeInfo;
import com.maplequad.fo.ods.tradecore.store.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-converter.json";
    private TradeInfo tradeInfo;
    private SpannerHandle spannerHandle;
    /***
     * Default constructor is made private
     */
    private TradeRetrievalService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with tradeTableName as input");
    }

    /**
     * This constructor is used  to create an instance of TradeRetrievalService
     *
     * @param assetClass
     * @throws Exception
     */
    public TradeRetrievalService(String assetClass) {
        this.spannerHandle = SpannerHandle.getInstance();

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
        LOGGER.trace("Get a single trade by row tradeId={}", tradeId);
        Map trade = null;
        return trade;
    }

    public Map getLatest(String tradeId) {
        return getLatestAsOn(tradeId,0,0);
    }

    //getLatest
    public Map getLatestAsOn(String tradeId, long minTransTime, long maxTransTime) {
        Map res = null;
        return res;
    }

    //getAllVersionsAsOn
    public List<Map> getAllVersionsAsOn(String tradeId, long minTransTime, long maxTransTime)  {
        return this.readByQuery("");
    }

    //getAllVersions
    public List<Map> getAllVersions(String tradeId) {
        return this.getAllVersionsAsOn(tradeId,0,0);
    }


    public List<Map> getTradesForExchange(String exch){

       return this.readByQuery("");
    }
    public List<Map> getTradesForCFICode(String cfiCode){

        return this.readByQuery("");
    }
    /**
     * This method just converts the result of a single trade into tradeMap.
     *
     * @param tradeId
     * @return Map - containing key as family.qualifier and value as value of the qualifier
     */
    private Map readTrade(String tradeId) {
        Map tradeMap = new HashMap();
        LOGGER.trace("tradeMap : {} ",tradeMap);
        return tradeMap;
    }


    private List<Map> readByQuery(String query) {
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

    private List<Map> readByQuery(String query, long minTransTime, long maxTransTime) {
        LOGGER.info("Executing query {}", query);
        List<Map> trades = null;
        return trades;

    }


    /***
     * This method calls readTrade method recursively to read trades from the bigtable.
     * @param maxResultSize
     * @return List<Map> List of trades in a map structure
     * */
    private List<Map> retrieve(int maxResultSize) throws IOException {
        List<Map> result = new ArrayList<>();
            LOGGER.info("Done. Read {} trades", result.size());
            return result;
        }
}
