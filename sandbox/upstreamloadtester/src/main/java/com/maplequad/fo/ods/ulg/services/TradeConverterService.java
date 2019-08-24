package com.maplequad.fo.ods.ulg.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.ulg.model.TradeAttribute;
import com.maplequad.fo.ods.ulg.model.TradeInfo;
import com.maplequad.fo.ods.ulg.utils.StringUtils;

/***
 * Convertor - takes hashmap and convertor json and gives bigtable ready hashmap
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeConverterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeConverterService.class);
    private static final String COLON = ":";
    private String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-converter.json";
    private TradeInfo tradeInfo;

    /***
     * Default constructor is made private
     */
    private TradeConverterService(){
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
                    JsonIterator.deserialize(FileUtils.readFileToString
                            (new File(this.getClass().getClassLoader().getResource(tradeJson).getFile()),
                                    "UTF-8"), TradeInfo.class);
        } catch (IOException ioe) {
            LOGGER.error("ERROR : Unable to find trade file at {}", tradeJson);
        }
    }

    /**
     * This method calls convert method recursively and create a list of trades with
     * converted values.
     *
     * @param tradeList
     * @return List of trades with converted values suitable as per database structure
     */
    public List<Map<String, String>> serve(List<Map<String, String>> tradeList) {
        LOGGER.info("Entered serve");
        List<Map<String, String>> cTradeList = new ArrayList<>();
        Iterator tradeItr = tradeList.iterator();

        while (tradeItr.hasNext()) {
            cTradeList.add(this.convert((Map<String, String>) tradeItr.next()));
        }
        LOGGER.info("Exited serve");
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
        LOGGER.info("Entered convert");
        Map<String, String> cMap = new HashMap<>();
        TradeAttribute attribute;
        Iterator attrItr = tradeInfo.getAttributes().iterator();
        while (attrItr.hasNext()) {
            attribute = (TradeAttribute) attrItr.next();
            //LOGGER.info("attribute {}", attribute);
            switch(attribute.getType()){
                case "straight-pull":
                    cMap.put(StringUtils.concat(new String[]{attribute.getFamily(), COLON, attribute.getQualifier()}),
                        inMap.get(attribute.getName()));
                    break;
                case "fixed":
                    cMap.put(StringUtils.concat(new String[]{attribute.getFamily(), COLON, attribute.getQualifier()}),
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
                    cMap.put(StringUtils.concat(new String[]{attribute.getFamily(), COLON, attribute.getQualifier()}),
                            String.valueOf(val));
                    break;
                case "timestamp":
                    cMap.put(StringUtils.concat(new String[]{attribute.getFamily(), COLON, attribute.getQualifier()}),
                            new Date().toInstant().toString());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute type: " + attribute.getType());
            }
        }
        LOGGER.info("cMap : {}", cMap);
        LOGGER.info("Exited convert");
        return cMap;
    }
}