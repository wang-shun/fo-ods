package com.maplequad.fo.ods.tradecore.lcm.services;

import com.jsoniter.JsonIterator;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.client.model.TradeInfo;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TradeGeneratorHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeGeneratorHelper.class);
    private static String tradeJson = "${ASSET_CLASS}/${ASSET_CLASS}-trade.json";

    private static Map<String,TradeGenerator> map =new HashMap<String,TradeGenerator>();
    public static TradeGenerator getInstance(String assetClass) throws IOException{
        TradeInfo tradeInfo;
        TradeGenerator tradeGenerator=null;

        if(map.get(assetClass)==null) {
            //Double check idiom
            synchronized  (TradeGeneratorHelper.class) {
                if(map.get(assetClass)==null) {
                    tradeGenerator = createTradeGenerator(assetClass);
                    map.put(assetClass, tradeGenerator);
                }else{
                    tradeGenerator=map.get(assetClass);
                }
            }
        }else{
            tradeGenerator=map.get(assetClass);
        }
        return tradeGenerator;
    }

    public static TradeGenerator createTradeGenerator(String assetClass) throws IOException {
        TradeInfo tradeInfo;
        TradeGenerator tradeGenerator;
        try {
            String newtradeJson = tradeJson.replace("${ASSET_CLASS}", assetClass.toLowerCase());
            Resource resource = new ClassPathResource(newtradeJson);

            InputStream in = resource.getInputStream();
            String result = IOUtils.toString(in, StandardCharsets.UTF_8);

            tradeInfo =
                    JsonIterator.deserialize(result, TradeInfo.class);
            assert (tradeInfo != null);
            LOGGER.info("Initialize tradeinfo successful");


            tradeGenerator = TradeGenerator.getInstance(assetClass,tradeInfo);
        } catch (IOException ioe2) {
            LOGGER.error("ERROR : Unable to find trade file at {}", tradeJson);
            throw ioe2;
        }
        return tradeGenerator;
    }

    public static Trade tradeCreation(TradeGenerateRequest request) throws Exception {

        TradeGenerator gen = TradeGeneratorHelper.getInstance(request.TradeType);

        Trade t = gen.createTrade(request.noOfLegs, request.noOfParties);

        assert (t != null);
        assert (t.getTradeEventList() != null);
        assert(t.getAssetClass()!=null);
        return t;
    }


}
