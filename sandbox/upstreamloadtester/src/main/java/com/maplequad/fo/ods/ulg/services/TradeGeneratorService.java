package com.maplequad.fo.ods.ulg.services;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.ulg.model.TradeAttribute;
import com.maplequad.fo.ods.ulg.model.TradeInfo;

/***
 * Generator - takes sample trade json and gives hashmap for each new trade
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeGeneratorService.class);
    private static final String COMMA = ",";
    private static final String REGEX_VALUES = "(\\$\\s*\\{[^\\]]*\\})";
    private String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-trade.json";
    private TradeInfo tradeInfo;

    /***
     * Default constructor is made private
     */
    private TradeGeneratorService(){
        throw new IllegalArgumentException("ERROR : Please use constructor with assetClass as input");
    }

    /**
     * This constructor is used  to create an instance of TradeGeneratorService for a specific asset class
     *
     * @param assetClass
     */
    public TradeGeneratorService(String assetClass) {

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
     * This method actually generates a trade by randomising the values as per configuration json file.
     *
     * @return Map of key value pairs with randomised trade data
     */
    private Map<String, String> generate() {
        LOGGER.info("Entered generate");
        Map<String, String> tradeMap = new HashMap<>();
        for (TradeAttribute attribute : tradeInfo.getAttributes()) {
            switch (attribute.getType()) {
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
                    tradeMap.put(attribute.getName(), String.valueOf(val));
                    break;
                case "int":
                    int min = Integer.parseInt(attribute.getMinRange());
                    int max = Integer.parseInt(attribute.getMaxRange());
                    tradeMap.put(attribute.getName(), String.valueOf(new Random().nextInt(max - min) + min));
                    break;
                case "float":
                    int minf = Integer.parseInt(attribute.getMinRange());
                    int maxf = Integer.parseInt(attribute.getMaxRange()) - 1;
                    StringBuilder floatBuilder = new StringBuilder();
                    floatBuilder.append(String.valueOf(new Random().nextInt(maxf - minf) + minf));
                    floatBuilder.append(".");
                    floatBuilder.append(new Random().nextInt(99 - 10) + 10);
                    tradeMap.put(attribute.getName(), floatBuilder.toString());
                    break;
                case "multiply":
                    String[] mpvalues = attribute.getFormula().split("\\*");
                    float mpvalue = 1;
                    for (int i = 0; i < mpvalues.length; i++) {
                        mpvalue = mpvalue * Float.parseFloat(tradeMap.get(mpvalues[i]));
                    }
                    tradeMap.put(attribute.getName(), String.valueOf(mpvalue));
                    break;
                case "add":
                    String[] apvalues = attribute.getFormula().split("\\+");
                    float apvalue = 1;
                    for (int i = 0; i < apvalues.length; i++) {
                        apvalue = apvalue + Float.parseFloat(tradeMap.get(apvalues[i]));
                    }
                    tradeMap.put(attribute.getName(), String.valueOf(apvalue));
                    break;
                case "date":
                    final Instant instant = new Date().toInstant();
                    final DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern(attribute.getFormat()).withZone(ZoneId.systemDefault());
                    tradeMap.put(attribute.getName(), formatter.format(instant));
                    break;
                case "timestamp":
                    tradeMap.put(attribute.getName(), new Date().toInstant().toString());
                    break;
                case "longdate":
                    tradeMap.put(attribute.getName(), String.valueOf(new Date().getTime()));
                    break;
                case "uuid":
                    tradeMap.put(attribute.getName(), String.valueOf(UUID.randomUUID()));
                    break;
                case "string":
                    if (attribute.getValue() != null) {
                        tradeMap.put(attribute.getName(), attribute.getValue());
                    } else if (attribute.getValues() != null) {
                        String[] values = attribute.getValues().split(COMMA);
                        tradeMap.put(attribute.getName(), values[new Random().nextInt(values.length - 1)]);
                    } else if (attribute.getFormat() != null) {
                        String format = attribute.getFormat();
                        Matcher m = Pattern.compile(REGEX_VALUES).matcher(format);
                        StringBuilder valueBuilder = new StringBuilder();
                        String[] values;
                        String value;
                        int count;
                        while (m.find()) {
                            value = m.group(0);
                            if (value.contains(COMMA)) {
                                values = value.split(COMMA);
                                count = Integer.parseInt(values[1]);
                                if ("S".equals(values[0])) {
                                    IntStream.range(0, count).parallel().forEach(v ->
                                        valueBuilder.append((char) (new Random().nextInt(90 - 65) + 65))
                                    );
                                } else if ("N".equals(values[0])) {
                                    IntStream.range(0, count).parallel().forEach(v ->
                                        valueBuilder.append(new Random().nextInt(9))
                                    );
                                }
                            }
                            format = format.replace(value, valueBuilder.toString());
                        }
                        tradeMap.put(attribute.getName(), format);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute type: " + attribute.getType());
            }
        }
        LOGGER.info("Exited generate");
        return tradeMap;
    }

    /**
     * This method calls generate method recursively to generate  a list of trades in input format.
     *
     * @param tradeCount
     * @return List of tradeMap with key value pairs
     */
    public List<Map<String, String>> serve(int tradeCount) {
        LOGGER.info("Entered serve");
        List<Map<String, String>> tradeList = new ArrayList<>();

        if (tradeCount <= Integer.MAX_VALUE) {
            IntStream.range(0, tradeCount).parallel().forEach(v ->
                tradeList.add(this.generate())
            );
        } else {
            throw new IllegalArgumentException("ERROR : Please provide tradeCount less than 2147483647");
        }
        LOGGER.info("Exited serve");
        return tradeList;
    }
}