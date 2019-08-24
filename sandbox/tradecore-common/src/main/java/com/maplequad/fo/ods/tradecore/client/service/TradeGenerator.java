package com.maplequad.fo.ods.tradecore.client.service;

import com.google.cloud.Timestamp;
import com.google.common.base.Strings;
import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.tradecore.client.model.TradeAttribute;
import com.maplequad.fo.ods.tradecore.client.model.TradeInfo;
import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * TradeGenerator class can be used to generate any number of trades with randomly generated values
 * as per specification provided in Json file format. The trades can be generated for any asset class with
 * any number of legs and parties in it.
 *
 * @author Madhav Mindhe
 * @since :   06/08/2017
 */
public class TradeGenerator {

    protected static final long DAY_IN_MILLIS = 86400000;
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeGenerator.class);
    private static final String COMMA = ",";
    private static final String SEPARATOR_START = "\\$\\{";
    private static final String SEPARATOR_END = "}";
    private static final String STRING_IND = "S";
    private static final String NUMEBR_IND = "N";
    private static final String REGEX_VALUES = "(\\$*\\{[^\\]]*\\})";
    private static final String ENC = "UTF-8";
    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    protected  TradeInfo tradeInfo;
    private  String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-trade.json";
    private static String CONF = "tradecore-common/src/main/resources/";
    private static Map<String, TradeGenerator> tradeGenMap = new HashMap();

    /**
     * This instance generator method checks if the instance for the given asset class is already created.
     * If not then it creates an instance and returns the same after populating with provided tradeInfo details.
     *
     * @param assetClass
     * @return instance of TradeGenerator
     */
    public static TradeGenerator getInstance(String assetClass, TradeInfo providedTradeInfo) {
        //if (tradeGenMap.get(assetClass) == null) {
            TradeGenerator tradeGenerator = new TradeGenerator();
            tradeGenerator.tradeInfo = providedTradeInfo;
            tradeGenMap.put(assetClass, tradeGenerator);
        //}
        return tradeGenerator;
    }

    /**
     * This instance generator method checks if the instance for the given asset class is already created.
     * If not then it creates an instance and returns the same after populating with all required details.
     *
     * @param assetClass
     * @return instance of TradeGenerator
     */
    public static TradeGenerator getInstance(String assetClass) {
        if (tradeGenMap.get(assetClass) == null) {
            TradeGenerator tradeGenerator = new TradeGenerator();
            String confLocation = System.getenv("CONF_LOCATION");
            if (confLocation != null) {
                CONF = confLocation;
            }
            tradeGenerator.tradeJson = tradeGenerator.tradeJson.replace("${ASSET_CLASS}", assetClass);
            try {
                tradeGenerator.tradeInfo =
                        JsonIterator.deserialize(FileUtils.readFileToString
                                (new File(CONF + tradeGenerator.tradeJson), ENC), TradeInfo.class);
            } catch (IOException ioe) {
                LOGGER.error("ERROR : Unable to find trade file at {}... Trying second path..", CONF + tradeGenerator.tradeJson);
            }
            tradeGenMap.put(assetClass, tradeGenerator);
        }
        return tradeGenMap.get(assetClass);
    }

    /**
     * This method actually generates a trade by randomising the values as per configuration json file.
     *
     * @return Map of key value pairs with randomised trade data
     */
    public  Trade createTrade(int noOfLegs, int noOfParties) {
        LOGGER.trace("Entered generate");
        Trade trade = new Trade();
        TradeEvent tradeEvent = new TradeEvent();
        List<TradeEvent> tradeEventList = new ArrayList<>();
        List<ITradeLeg> tradeLegList = new ArrayList<>();
        List<TradeParty> tradePartyList = new ArrayList<>();

        Timestamp currentTimestamp = Timestamp.now();

        //Trade & Event
        Map<String, String> tradeDetails = generate(tradeInfo.getTradeEventAttributes());
        trade.setCreatedBy(tradeDetails.get("username"));
        trade.setOrigSystem(tradeDetails.get("origSystem"));
        trade.setOsTradeId(tradeDetails.get("sequenceNumber"));
        trade.setAssetClass(tradeDetails.get("assetClass"));
        trade.setProductType(tradeDetails.get("productType"));
        trade.setTradeType(TradeOuterClass.TradeType.SINGLE);
        trade.setTradeDate(new Date());
        trade.setCreatedTimeStamp(currentTimestamp);

        tradeEvent.setNoOfLegs(noOfLegs);
        if (tradeDetails.get("parentTradeId") != null) {
            tradeEvent.setParentTradeId(tradeDetails.get("parentTradeId"));
        }
        tradeEvent.setValidTimeFrom(currentTimestamp);
        tradeEvent.setValidTimeTo(com.google.cloud.Timestamp.MAX_VALUE);
        tradeEvent.setOsVersion(tradeDetails.get("osVersion"));
        tradeEvent.setOsVersionStatus(tradeDetails.get("osVersionStatus"));
        if(SysEnv.LOCAL_RUN_FLAG != null){
            tradeEvent.setEventReference(tradeDetails.get("bookingId"));
        }
        else {
            tradeEvent.setEventReference(tradeDetails.get("bookingId")+SysEnv.RUN_NO);
        }
        tradeEvent.setEventStatus(tradeDetails.get("bookingStatus"));
        tradeEvent.setEventType(tradeDetails.get("eventType"));
        tradeEvent.setExchangeExecutionId(tradeDetails.get("exchangeExecutionId"));
        tradeEvent.setOrderId(tradeDetails.get("orderId"));
        tradeEvent.setTrader(tradeDetails.get("trader"));
        tradeEvent.setTraderCountry(tradeDetails.get("traderCountry"));
        tradeEvent.setSalesman(tradeDetails.get("salesman"));
        tradeEvent.setSalesmanCountry(tradeDetails.get("salesmanCountry"));
        //TradeLeg
        for (int lCount = 1; lCount <= noOfLegs; lCount++) {
            switch (trade.getAssetClass()) {
                case AssetClass.EQT:
                    tradeLegList.add(generateEquityLeg(generate(tradeInfo.getTradeLegAttributes()), lCount, currentTimestamp));
                    break;
                case AssetClass.IRD:
                    tradeLegList.add(generateIrdLeg(generate(tradeInfo.getTradeLegAttributes()), lCount, currentTimestamp));
                    break;
                case AssetClass.FXD:
                    tradeLegList.add(generateFxdLeg(generate(tradeInfo.getTradeLegAttributes()), lCount, currentTimestamp));
                    break;
            }
        }
        //TradeParty
        for (int pCount = 1; pCount <= noOfParties; pCount++) {
            Map<String, String> partyDetails = generate(tradeInfo.getTradePartyAttributes());
            TradeParty tradeParty = new TradeParty();
            tradeParty.setValidTimeFrom(currentTimestamp);
            tradeParty.setValidTimeTo(com.google.cloud.Timestamp.MAX_VALUE);
            tradeParty.setPartyRef(partyDetails.get("partyRef"));
            tradeParty.setPartyRole(partyDetails.get("partyRole"));
            tradePartyList.add(tradeParty);
        }
        tradeEvent.setTradeLegList(tradeLegList);
        tradeEvent.setTradePartyList(tradePartyList);
        tradeEventList.add(tradeEvent);
        trade.setTradeEventList(tradeEventList);
        LOGGER.trace("Exited generate");
        return trade;
    }

    /**
     * This method is used to generate a leg for an equity trade
     *
     * @param legDetails
     * @param lCount
     * @param currentTimestamp
     * @return an instance of an equity leg populated with the values for each attribute
     */
    private static Equity generateEquityLeg(Map<String, String> legDetails, int lCount, Timestamp currentTimestamp) {
        Equity tradeLeg = new Equity();
        tradeLeg.setLegNumber(lCount);
        tradeLeg.setValidTimeFrom(currentTimestamp);
        tradeLeg.setValidTimeTo(com.google.cloud.Timestamp.MAX_VALUE);

        tradeLeg.setLegType(legDetails.get("legType"));
        tradeLeg.setBook(legDetails.get("ptsFolioId"));
        tradeLeg.setInstrumentId(legDetails.get("symbol"));
        tradeLeg.setInternalProductRef(legDetails.get("intPrdRef"));
        tradeLeg.setInternalProductType(legDetails.get("intPrdType"));
        tradeLeg.setRic(legDetails.get("ric"));
        tradeLeg.setIsin(legDetails.get("isin"));
        tradeLeg.setCurrency(legDetails.get("currency"));
        tradeLeg.setExchange(legDetails.get("market"));

        tradeLeg.setCfiCode(legDetails.get("cfiCode"));
        tradeLeg.setQuantity(Integer.parseInt(legDetails.get("quantityFilled")));
        tradeLeg.setPrice((float) Double.parseDouble(legDetails.get("price")));
        tradeLeg.setGrossPrice(tradeLeg.getPrice() * tradeLeg.getQuantity());
        tradeLeg.setBuySellInd(legDetails.get("buySellInd"));

        return tradeLeg;
    }

    /**
     * This method is used to generate a leg for an IR Derivative
     *
     * @param legDetails
     * @param lCount
     * @param currentTimestamp
     * @return an instance of Ird leg populated with the values for each attribute
     */
    private static Ird generateIrdLeg(Map<String, String> legDetails, int lCount, Timestamp currentTimestamp) {
        Ird tradeLeg = new Ird();
        tradeLeg.setLegNumber(lCount);
        tradeLeg.setValidTimeFrom(currentTimestamp);
        tradeLeg.setValidTimeTo(com.google.cloud.Timestamp.MAX_VALUE);

        tradeLeg.setLegType(legDetails.get("legType"));
        tradeLeg.setBook(legDetails.get("ptsFolioId"));
        tradeLeg.setInstrumentId(legDetails.get("symbol"));
        tradeLeg.setInternalProductRef(legDetails.get("intPrdRef"));
        tradeLeg.setInternalProductType(legDetails.get("intPrdType"));
        tradeLeg.setRic(legDetails.get("ric"));
        tradeLeg.setIsin(legDetails.get("isin"));
        tradeLeg.setCurrency(legDetails.get("currency"));
        tradeLeg.setExchange(legDetails.get("market"));

        tradeLeg.setIrdLegType(legDetails.get("irdLegType"));
        tradeLeg.setMaturityDate(getDate(legDetails.get("maturityDate")));
        tradeLeg.setNotional((float) Double.parseDouble(legDetails.get("notional")));
        tradeLeg.setIndex(legDetails.get("index"));
        tradeLeg.setNotionalExp(legDetails.get("notionalExp"));
        tradeLeg.setTerm(legDetails.get("term"));
        tradeLeg.setRate((float) Double.parseDouble(legDetails.get("rate")));
        tradeLeg.setBasis(legDetails.get("basis"));
        tradeLeg.setSpread((float) Double.parseDouble(legDetails.get("spread")));
        tradeLeg.setSettlementCcy(legDetails.get("settlementCcy"));
        tradeLeg.setSettlementAmount((float) Double.parseDouble(legDetails.get("settlementAmount")));
        tradeLeg.setSettlementDate(getDate(legDetails.get("settlementDate")));

        return tradeLeg;
    }

    /**
     * This method is used to generate a leg for an FX Derivative
     *
     * @param legDetails
     * @param lCount
     * @param currentTimestamp
     * @return an instance of Fxd leg populated with the values for each attribute
     */
    private static Fxd generateFxdLeg(Map<String, String> legDetails, int lCount, Timestamp currentTimestamp) {
        Fxd tradeLeg = new Fxd();
        tradeLeg.setLegNumber(lCount);
        tradeLeg.setValidTimeFrom(currentTimestamp);
        tradeLeg.setValidTimeTo(com.google.cloud.Timestamp.MAX_VALUE);

        tradeLeg.setLegType(legDetails.get("legType"));
        tradeLeg.setBook(legDetails.get("ptsFolioId"));
        tradeLeg.setInstrumentId(legDetails.get("symbol"));
        tradeLeg.setInternalProductRef(legDetails.get("intPrdRef"));
        tradeLeg.setInternalProductType(legDetails.get("intPrdType"));
        tradeLeg.setRic(legDetails.get("ric"));
        tradeLeg.setIsin(legDetails.get("isin"));
        tradeLeg.setCurrency(legDetails.get("currency"));
        tradeLeg.setExchange(legDetails.get("market"));

        tradeLeg.setVolume((float) Double.parseDouble(legDetails.get("volume")));
        tradeLeg.setSpotRate((float) Double.parseDouble(legDetails.get("spotRate")));
        tradeLeg.setAllInRate((float) Double.parseDouble(legDetails.get("allInRate")));
        tradeLeg.setBaseCcy(legDetails.get("baseCcy"));
        tradeLeg.setCounterCcy(legDetails.get("counterCcy"));
        tradeLeg.setTenor(legDetails.get("tenor"));
        tradeLeg.setValueDate(LocalDate.parse(legDetails.get("valueDate")));


        tradeLeg.setBuySellInd( legDetails.get("buySellInd"));
        return tradeLeg;
    }

    /**
     * This method is used to generate random values of different type of data as per provided specificationß
     *
     * @param attributes
     * @return Map containing keys and values with randomly generated data for each key
     */
    private static Map<String, String> generate(List<TradeAttribute> attributes) {
        LOGGER.trace("Entered generate");
        Map<String, String> tradeMap = new HashMap<>();
        for (TradeAttribute attribute : attributes) {
            switch (attribute.getType()) {
                case "sequence":
                    long val = 0;
                    final UUID uid = UUID.randomUUID();
                    final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
                    buffer.putLong(uid.getLeastSignificantBits());
                    buffer.putLong(uid.getMostSignificantBits());
                    final BigInteger bi = new BigInteger(buffer.array());
                    val = bi.longValue() & Long.MAX_VALUE;
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
                    if(minf != maxf+1) {
                        StringBuilder floatBuilder = new StringBuilder();
                        floatBuilder.append(String.valueOf(new Random().nextInt(maxf - minf) + minf));
                        floatBuilder.append(".");
                        floatBuilder.append(new Random().nextInt(99 - 10) + 10);
                        tradeMap.put(attribute.getName(), floatBuilder.toString());
                    }else{
                        tradeMap.put(attribute.getName(), String.valueOf(minf));
                    }
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
                case "futdate":
                    int minDays = Integer.parseInt(attribute.getMinRange());
                    int maxDays = Integer.parseInt(attribute.getMaxRange());
                    int adjust = new Random().nextInt(maxDays - minDays) + minDays;
                    Instant futdate = new Date().toInstant();
                    final DateTimeFormatter formatterFutDate =
                            DateTimeFormatter.ofPattern(attribute.getFormat()).withZone(ZoneId.systemDefault());
                    futdate = futdate.plusMillis(adjust * DAY_IN_MILLIS);
                    tradeMap.put(attribute.getName(), formatterFutDate.format(futdate));
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
                        String[] vars;
                        String[] parts;
                        String value;
                        int count;
                        while (m.find()) {
                            value = m.group(0);
                            values = value.split(SEPARATOR_START);
                            for (String str : values) {
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
                                    if (parts.length > 1) {
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
     * This method is used to generate an instance of Date from the given string representation of Date.ß
     *
     * @param strDate
     * @return Date
     */
    private static Date getDate(String strDate) {
        Date uDate = null;
        if (!Strings.isNullOrEmpty(strDate)) {
            try {
                uDate = YYYY_MM_DD.parse(strDate);
            } catch (ParseException pe) {
                throw new RuntimeException("Invalid Date Specified as => " + strDate);
            }
        }
        return uDate;
    }
}