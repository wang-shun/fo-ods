package com.maplequad.fo.ods.tradecore.lcm.services;

import com.codahale.metrics.*;
import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.tradecore.dao.Column;
import com.maplequad.fo.ods.tradecore.lcm.model.Table;
import com.maplequad.fo.ods.tradecore.lcm.model.TradeAttribute;
import com.maplequad.fo.ods.tradecore.lcm.model.TradeInfo;
import com.maplequad.fo.ods.tradecore.store.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Timer;

/***
 * Convertor - takes hashmap and convertor json and gives bigtable ready hashmap
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeConverterService extends AbstractTradeCoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeConverterService.class);
    private static final String COLON = ":";
    //private String tradeJson = "WEB-INF/classes/${ASSET_CLASS}/${ASSET_CLASS}-converter.json";
    private String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-converter.json";
    private TradeInfo tradeInfo;
    private static final String NO_OF_ROWS = "NoOfRows";

    /***
     * Default constructor is made private
     */
    private TradeConverterService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with assetClass as input");
    }

    /**
     * This constructor is used  to create an instance of TradeConverterService for a specific asset class
     *
     * @param assetClass
     */
    public TradeConverterService(String assetClass) {

        tradeJson = tradeJson.replace("${ASSET_CLASS}", assetClass);

        try {
            tradeInfo =
                    JsonIterator.deserialize(FileUtils.readFileToString(new File(CONF+tradeJson), ENC), TradeInfo.class);
        } catch (IOException ioe) {
            try {
                tradeInfo =
                        JsonIterator.deserialize(FileUtils.readFileToString
                                (new File(this.getClass().getClassLoader().getResource(tradeJson).getFile()),
                                        ENC), TradeInfo.class);
            }
            catch (IOException ioe2) {
                LOGGER.error("ERROR : Unable to find trade file at {}", tradeJson);
            }
        }
        //timer = this.startMetric("convert", LOGGER);
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register("convert", timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);
    }

    /**
     * This method calls convert method recursively and create a list of trades with
     * converted values.
     *
     * @param tradeList
     * @return List of trades with converted values suitable as per database structure
     */
    public List<Map<String, List<List<Column>>>> serve(List<Map<String, String>> tradeList) {
        LOGGER.trace("Entered serve");
        List<Map<String, List<List<Column>>>> cTradeList = new ArrayList<>();
        Iterator tradeItr = tradeList.iterator();
        while (tradeItr.hasNext()) {
            withTimer(timer, "convert", () -> { cTradeList.add(this.convert((Map<String, String>) tradeItr.next())); return null;});
        }
        LOGGER.trace("Exited serve");
        return cTradeList;

    }

    /**
     * This method converts a trade from input values into a format that is inline with
     * database structure.
     *
     * @param inMap
     * @return trade with converted values suitable as per database structure
     */
    private Map<String, List<List<Column>>> convert(Map<String, String> inMap) {
        LOGGER.trace("Entered convert");
        Map<String, List<List<Column>>> cMap = null;
        if(inMap != null) {
            cMap = new HashMap<>();
            Table table;
            TradeAttribute tAttribute;
            List<Column> columnsList = new ArrayList<>();
            List<List<Column>> rowList = new ArrayList<>();
            Map<String,String> currentMap;
            Iterator tableItr = tradeInfo.getTrade().iterator();
            currentMap = new HashMap<>();
            while (tableItr.hasNext()) {
                table = (Table) tableItr.next();
                rowList = new ArrayList<>();
                int rowCount = 1;
                int genCount = 0;
                while(genCount < rowCount) {
                    Iterator attrItr = table.getAttributes().iterator();
                    columnsList = new ArrayList();
                    while (attrItr.hasNext()) {
                        tAttribute = (TradeAttribute) attrItr.next();
                        switch (tAttribute.getType()) {
                            case "straight-pull":
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), inMap.get(tAttribute.getName())));
                                currentMap.put(tAttribute.getColumn(), inMap.get(tAttribute.getName()));
                                break;
                            case "fixed":
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), tAttribute.getValue()));
                                currentMap.put(tAttribute.getColumn(), tAttribute.getValue());
                                break;
                            case "current":
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), currentMap.get(tAttribute.getName())));
                                break;
                            case "sequence":
                                final UUID uid = UUID.randomUUID();
                                final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
                                buffer.putLong(uid.getLeastSignificantBits());
                                buffer.putLong(uid.getMostSignificantBits());
                                final BigInteger bi = new BigInteger(buffer.array());
                                long val = bi.longValue() & Long.MAX_VALUE;
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), String.valueOf(val)));
                                currentMap.put(tAttribute.getColumn(), String.valueOf(val));
                                break;
                            case "timestamp":
                                String timestamp = new Date().toInstant().toString();
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), timestamp));
                                currentMap.put(tAttribute.getColumn(), timestamp);
                                break;
                            case "rowcounter":
                                String value = String.valueOf(genCount+1);
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), value));
                                rowCount = Integer.parseInt(currentMap.get(NO_OF_ROWS));
                                break;
                            case "rowcount":
                                columnsList.add(new Column(tAttribute.getColumn(), tAttribute.getDatatype(), inMap.get(tAttribute.getName())));
                                currentMap.put(NO_OF_ROWS, inMap.get(tAttribute.getName()));
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid attribute type: " + tAttribute.getType());
                        }
                    }
                    rowList.add(columnsList);
                    genCount++;
                }
                cMap.put(table.getTableName(), rowList);
            }
            LOGGER.trace("cMap : {}", cMap);
            LOGGER.trace("Exited convert");
        }
        return cMap;
    }
}