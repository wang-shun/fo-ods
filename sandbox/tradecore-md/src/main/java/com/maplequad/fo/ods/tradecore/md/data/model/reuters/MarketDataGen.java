package com.maplequad.fo.ods.tradecore.md.data.model.reuters;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/***
 * MarketDataGen
 *
 * This can be used to generate some dummy messages that are in perfect inline with the Reuters Market Data Model
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public class MarketDataGen {

    public static String[] FX_CURRENCY_SYMBOLS = new String[]{"USDGBP", "USDEUR", "USDAED", "USDJPY", "USDCAD", "USDCHF", "GBPUSD", "GBPEUR", "GBPAED", "GBPJPY", "GBPCAD", "GBPCHF", "EURUSD", "EURGBP", "EURAED", "EURJPY", "EURCAD", "EURCHF", "AEDUSD", "AEDGBP", "AEDEUR", "AEDJPY", "AEDCAD", "AEDCHF", "JPYUSD", "JPYGBP", "JPYEUR", "JPYAED", "JPYCAD", "JPYCHF", "CADUSD", "CADGBP", "CADEUR", "CADAED", "CADJPY", "CADCHF", "CHFUSD", "CHFGBP", "CHFEUR", "CHFAED", "CHFJPY", "CHFCAD"};

    public static List<String> fxSpotList = new ArrayList();

    static {

        fxSpotList.add("{\"RIC\":\"@RIC=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"@TS\",\"Fields\":[{\"BID\":[{\"Value\":\"@BID\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"09\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"BARCLAYS LON\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"PRIMACT_1\":[{\"Value\":\"12.9043\",\"Type\":\"Double\"}]},{\"GN_TXT16_2\":[{\"Value\":\"<ZARVOL>\",\"Type\":\"String\"}]},{\"QUOTIM_MS\":[{\"Value\":\"71911617\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"12.9193\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"@ASK\",\"Type\":\"Double\"}]},{\"VALUE_TS1\":[{\"Value\":\"19:58:31.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"BARCLAYS \",\"Type\":\"String\"}]},{\"GN_TXT24_1\":[{\"Value\":\"<0#ZARF=>\",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"12.8000\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]}]}");
        fxSpotList.add("{\"RIC\":\"@RIC=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"@TS\",\"Fields\":[{\"BID\":[{\"Value\":\"@BID\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"09\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"Commerzbank FFT\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"PRIMACT_1\":[{\"Value\":\"1.1369\",\"Type\":\"Double\"}]},{\"GN_TXT16_2\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"QUOTIM_MS\":[{\"Value\":\"73376752\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"1.1372\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"@ASK\",\"Type\":\"Double\"}]},{\"VALUE_TS1\":[{\"Value\":\"20:22:56.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"Commerzbank \",\"Type\":\"String\"}]},{\"GN_TXT24_1\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"1.1431\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]}]}");
        fxSpotList.add("{\"RIC\":\"@RIC=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"@TS\",\"Fields\":[{\"BID\":[{\"Value\":\"@BID\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"09\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"PIRAEUS BANK ATH\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"PRIMACT_1\":[{\"Value\":\"129.71\",\"Type\":\"Double\"}]},{\"GN_TXT16_2\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"QUOTIM_MS\":[{\"Value\":\"73646400\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"129.76\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"@ASK\",\"Type\":\"Double\"}]},{\"VALUE_TS1\":[{\"Value\":\"20:27:26.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"PIRAEUS BANK\",\"Type\":\"String\"}]},{\"GN_TXT24_1\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"130.43\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]}]}");
        fxSpotList.add("{\"RIC\":\"@RIC=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"@TS\",\"Fields\":[{\"BID\":[{\"Value\":\"@BID\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"09\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"DANSKE BANK COP\",\"Type\":\"String\"}]},{\"VALUE_TS1\":[{\"Value\":\"20:33:51.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"DANSKE BANK\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"1.0898\",\"Type\":\"Double\"}]},{\"PRIMACT_1\":[{\"Value\":\"1.0972\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]},{\"QUOTIM_MS\":[{\"Value\":\"74031461\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"1.0977\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"@ASK\",\"Type\":\"Double\"}]}]}");
        fxSpotList.add("{\"RIC\":\"@RIC=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"@TS\",\"Fields\":[{\"BID\":[{\"Value\":\"@BID\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"09\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"ALFA-BANK MOW\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"PRIMACT_1\":[{\"Value\":\"0.9111\",\"Type\":\"Double\"}]},{\"GN_TXT16_2\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"QUOTIM_MS\":[{\"Value\":\"73893455\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"0.9115\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"@ASK\",\"Type\":\"Double\"}]},{\"VALUE_TS1\":[{\"Value\":\"20:31:33.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"ALFA-BANK \",\"Type\":\"String\"}]},{\"GN_TXT24_1\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"0.9187\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]}]}");
        fxSpotList.add("{\"RIC\":\"@RIC=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"@TS\",\"Fields\":[{\"BID\":[{\"Value\":\"@BID\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"09\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"NORDEA COP\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"PRIMACT_1\":[{\"Value\":\"142.37\",\"Type\":\"Double\"}]},{\"GN_TXT16_2\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"QUOTIM_MS\":[{\"Value\":\"74032036\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"142.41\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"@ASK\",\"Type\":\"Double\"}]},{\"VALUE_TS1\":[{\"Value\":\"20:33:52.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"NORDEA \",\"Type\":\"String\"}]},{\"GN_TXT24_1\":[{\"Value\":\" \",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"142.14\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]}]}");

    }

    static String getCurr() {
        return FX_CURRENCY_SYMBOLS[new Random().nextInt(FX_CURRENCY_SYMBOLS.length)];
    }

    static String getSample() {

        return fxSpotList.get(new Random().nextInt(fxSpotList.size()));

    }

    static String getBidRate() {
        int minf = 1;
        int maxf = 3;
        StringBuilder floatBuilder = new StringBuilder();
        floatBuilder.append(String.valueOf(new Random().nextInt(maxf - minf) + minf));
        floatBuilder.append(".");
        floatBuilder.append(new Random().nextInt(99 - 10) + 10);
        return floatBuilder.toString();
    }

    static String getAskRate(String bidRate) {
        int minf = 1;
        int maxf = 3;
        StringBuilder floatBuilder = new StringBuilder();
        floatBuilder.append(String.valueOf(new Random().nextInt(maxf - minf) + minf));
        floatBuilder.append(".");
        floatBuilder.append(new Random().nextInt(99 - 10) + 10);
        float gap = Float.parseFloat(floatBuilder.toString());

        return String.valueOf(Float.parseFloat(bidRate) + gap / 100);

    }

    public static String get() {

        String sample = getSample();
        String bid = getBidRate();
        sample = sample.replace("@RIC", getCurr());
        sample = sample.replace("@BID", bid);
        sample = sample.replace("@ASK", getAskRate(bid));
        sample = sample.replace("@TS", String.valueOf(new Date().getTime()));
        return sample;

    }

    public static void main(String[] args) {

        for (int i = 0; i < 200; i++) {
            String msg = "{\"RIC\":\"ZAR=\",\"TOPIC\":\"213\",\"TIMESTAMP\":\"1504900711648\",\"Fields\":[{\"BID\":[{\"Value\":\"12.9043\",\"Type\":\"Double\"}]},{\"QUOTE_DATE\":[{\"Value\":\"08\\/09\\/2017\",\"Type\":\"Date\"}]},{\"DSPLY_NAME\":[{\"Value\":\"BARCLAYS LON\",\"Type\":\"String\"}]},{\"GV4_TEXT\":[{\"Value\":\"SPOT\",\"Type\":\"String\"}]},{\"PRIMACT_1\":[{\"Value\":\"12.9043\",\"Type\":\"Double\"}]},{\"GN_TXT16_2\":[{\"Value\":\"<ZARVOL>\",\"Type\":\"String\"}]},{\"QUOTIM_MS\":[{\"Value\":\"71911617\",\"Type\":\"Integer\"}]},{\"SEC_ACT_1\":[{\"Value\":\"12.9193\",\"Type\":\"Double\"}]},{\"ASK\":[{\"Value\":\"12.9193\",\"Type\":\"Double\"}]},{\"VALUE_TS1\":[{\"Value\":\"19:58:31.000\",\"Type\":\"Time\"}]},{\"CTBTR_1\":[{\"Value\":\"BARCLAYS \",\"Type\":\"String\"}]},{\"GN_TXT24_1\":[{\"Value\":\"<0#ZARF=>\",\"Type\":\"String\"}]},{\"GEN_VAL3\":[{\"Value\":\"12.8000\",\"Type\":\"Double\"}]},{\"PROD_PERM\":[{\"Value\":\"213\",\"Type\":\"Integer\"}]}]}";
            long start = new Date().getTime();
            System.out.println(new Gson().fromJson(JsonFixer.fix(get()), MarketData.class));
            long end = new Date().getTime();
            System.out.println(end - start);
            //System.out.println(new Gson().fromJson(JsonFixer.fix(msg), MarketData.class));
        }

    }

}
