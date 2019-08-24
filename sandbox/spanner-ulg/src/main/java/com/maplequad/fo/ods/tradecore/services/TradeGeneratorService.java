package com.maplequad.fo.ods.tradecore.lcm.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.tradecore.lcm.model.TradeAttribute;
import com.maplequad.fo.ods.tradecore.lcm.model.TradeInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/***
 * Generator - takes sample trade json and gives hashmap for each new trade
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeGeneratorService extends AbstractTradeCoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeGeneratorService.class);
    private static final String COMMA = ",";
    private static final String SEPARATOR_START = "\\$\\{";
    private static final String SEPARATOR_END = "}";
    private static final String STRING_IND = "S";
    private static final String NUMEBR_IND = "N";
    //private static final String REGEX_VALUES = "(\\$\\s*\\{[^\\]]*\\})";
    private static final String REGEX_VALUES = "(\\$*\\{[^\\]]*\\})";
    //private String tradeJson = "WEB-INF/classes/${ASSET_CLASS}/${ASSET_CLASS}-trade.json";
    private String tradeJsonCreate = "${ASSET_CLASS}/${ASSET_CLASS}-trade.json";
    private String tradeJsonAmend = "${ASSET_CLASS}/${ASSET_CLASS}-trade-amend.json";
    private TradeInfo tradeInfo;
    private static final long DAY_IN_MILLIS = 86400000;
    /***
     * Default constructor is made private
     */
    private TradeGeneratorService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with assetClass as input");
    }

    /**
     * This constructor is used  to create an instance of TradeGeneratorService for a specific asset class
     *
     * @param assetClass
     */
    public TradeGeneratorService(String assetClass, String action) {

        String tradeJson = null;

        if("CREATE".equals(action)){
            tradeJson = tradeJsonCreate;
        }
        else{
            tradeJson = tradeJsonAmend;
        }
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
        //timer = this.startMetric("generate", LOGGER);
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register("generate", timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);

    }

    /**
     * This method actually generates a trade by randomising the values as per configuration json file.
     *
     * @return Map of key value pairs with randomised trade data
     */
    private Map<String, String> generate(int counter) {
        LOGGER.trace("Entered generate");
        Map<String, String> tradeMap = new HashMap<>();
        for (TradeAttribute attribute : tradeInfo.getAttributes()) {
            switch (attribute.getType()) {
                case "sequence":
                    long val = 0;
                    final UUID uid = UUID.randomUUID();
                    final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
                    buffer.putLong(uid.getLeastSignificantBits());
                    buffer.putLong(uid.getMostSignificantBits());
                    final BigInteger bi = new BigInteger(buffer.array());
                    val =  bi.longValue() & Long.MAX_VALUE;
                    tradeMap.put(attribute.getName(), String.valueOf(val));
                    break;
                case "list":
                    tradeMap.put(attribute.getName(), attribute.getValues().split(COMMA)[counter]);
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
                    long date = new Date().getTime();
                    if (attribute.getValue() != null) {
                        int adjust = Integer.parseInt(attribute.getValue());
                        date = date + (adjust * DAY_IN_MILLIS);
                    }
                    tradeMap.put(attribute.getName(), String.valueOf(date));
                    break;
                case "uuid":
                    tradeMap.put(attribute.getName(), String.valueOf(UUID.randomUUID()));
                    break;
                case "string":
                    if (attribute.getValue() != null) {
                        tradeMap.put(attribute.getName(), attribute.getValue());
                    } else if (attribute.getValues() != null) {
                        String[] values = attribute.getValues().split(COMMA);
                        tradeMap.put(attribute.getName(), values[new Random().nextInt(values.length - 1) & Integer.MAX_VALUE]);
                    } else if (attribute.getFormat() != null) {
                        String format = attribute.getFormat();
                        Matcher m = Pattern.compile(REGEX_VALUES).matcher(format);
                        StringBuilder valueBuilder = new StringBuilder();
                        String[] values;
                        String[] vars;
                        String[] parts;
                        String value;
                        int count;
                        while (m.find()) {
                            value = m.group(0);
                            values = value.split(SEPARATOR_START);
                            for (String str: values) {
                                if (str.contains(SEPARATOR_END)) {
                                    parts = str.split(SEPARATOR_END);
                                    vars = parts[0].split(COMMA);
                                    if (vars != null && vars.length > 1) {
                                        count = Integer.parseInt(vars[1]);
                                        if (STRING_IND.equals(vars[0])) {
                                            IntStream.range(0, count).parallel().forEach(v ->
                                                    valueBuilder.append((char) (new Random().nextInt(90 - 65) + 65))
                                            );
                                        } else if (NUMEBR_IND.equals(vars[0])) {
                                            IntStream.range(0, count).parallel().forEach(v ->
                                                    valueBuilder.append(new Random().nextInt(9))
                                            );
                                        }
                                    }
                                    if(parts.length > 1){
                                        valueBuilder.append(parts[1]);
                                    }
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
        LOGGER.trace("Exited generate");
        return tradeMap;
    }

    /**
     * This method calls generate method recursively to generate  a list of trades in input format.
     *
     * @param tradeCount
     * @return List of tradeMap with key value pairs
     */
    public List<Map<String, String>> serve(int tradeCount) {
        LOGGER.trace("Entered serve");
        List<Map<String, String>> tradeList = new ArrayList<>();

        if (tradeCount <= Integer.MAX_VALUE) {
            IntStream.range(0, tradeCount).parallel().forEach( v ->
                    withTimer(timer, "generate", () -> { tradeList.add(this.generate(v)); return null;})
            );
        } else {
            throw new IllegalArgumentException("ERROR : Please provide tradeCount less than 2147483647");
        }
        LOGGER.trace("Exited serve");
        return tradeList;
    }
}