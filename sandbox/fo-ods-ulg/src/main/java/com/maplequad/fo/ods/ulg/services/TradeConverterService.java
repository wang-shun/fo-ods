package com.maplequad.fo.ods.ulg.services;

import com.codahale.metrics.*;
import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.ulg.model.TradeAttribute;
import com.maplequad.fo.ods.ulg.model.TradeInfo;
import com.maplequad.fo.ods.ulg.utils.StringUtils;
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
    public List<Map<String, String>> serve(List<Map<String, String>> tradeList) {
        LOGGER.trace("Entered serve");
        List<Map<String, String>> cTradeList = new ArrayList<>();
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
    private Map<String, String> convert(Map<String, String> inMap) {
        LOGGER.trace("Entered convert");
        Map<String, String> cMap = null;
        if(inMap != null) {
            cMap = new HashMap<>();
            TradeAttribute attribute;
            Iterator attrItr = tradeInfo.getAttributes().iterator();
            while (attrItr.hasNext()) {
                attribute = (TradeAttribute) attrItr.next();
                //LOGGER.info("attribute {}", attribute);
                switch (attribute.getType()) {
                    case "straight-pull":
                        cMap.put(StringUtils.concat(attribute.getFamily(), COLON, attribute.getQualifier()),
                                inMap.get(attribute.getName()));
                        break;
                    case "fixed":
                        cMap.put(StringUtils.concat(attribute.getFamily(), COLON, attribute.getQualifier()),
                                attribute.getValue());
                        break;
                    case "sequence":
                        long val = -1;
                        while (val < 0) {
                            final UUID uid = UUID.randomUUID();
                            final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
                            buffer.putLong(uid.getLeastSignificantBits());
                            buffer.putLong(uid.getMostSignificantBits());
                            final BigInteger bi = new BigInteger(buffer.array());
                            val = bi.longValue();
                        }
                        cMap.put(StringUtils.concat(attribute.getFamily(), COLON, attribute.getQualifier()),
                                String.valueOf(val));
                        break;
                    case "timestamp":
                        cMap.put(StringUtils.concat(attribute.getFamily(), COLON, attribute.getQualifier()),
                                new Date().toInstant().toString());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid attribute type: " + attribute.getType());
                }
            }
            LOGGER.trace("cMap : {}", cMap);
            LOGGER.trace("Exited convert");
        }
        return cMap;
    }
}
