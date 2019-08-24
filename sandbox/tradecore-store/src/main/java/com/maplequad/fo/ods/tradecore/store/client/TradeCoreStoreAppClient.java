package com.maplequad.fo.ods.tradecore.store.client;

import com.maplequad.fo.ods.tradecore.store.client.services.TradeGeneratorService;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.slf4j.LoggerFactory;

/**
 * TradeCoreStoreAppClient -
 * This class can be used to generate and update the trades by specifying number of trades and also the max legs and
 * max number of parties that need to be there with these trades
 */
public class TradeCoreStoreAppClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TradeCoreStoreAppClient.class);

    public static void main(String[] args) {
        LOGGER.info("Starting the TradeCoreStoreAppClient..");
        String ERR_MSG = "ERROR : Please provide inputs as Asset Class, Number of Trades, Max Number of Legs and Max Number of Parties";

        String assetClass = SysEnv.TC_TG_ASSET_CLASS;
        String serviceHost = SysEnv.TC_STORE_SRVC_HOST;
        int servicePort = SysEnv.TC_STORE_SRVC_PORT;
        int tradeCount = SysEnv.TEST_TRADE_COUNT;
        int maxNoOfLegs = SysEnv.TEST_MAX_TRADE_LEGS;
        int maxNoOfParties = SysEnv.TEST_MAX_TRADE_PARTIES;

        if(SysEnv.TC_TG_ASSET_CLASS == null){
             assetClass = "fi-irs-ba";
             serviceHost = "localhost";
             servicePort = 3002;
             tradeCount = 10;
             maxNoOfLegs = 2;
             maxNoOfParties = 2;

        }

        LOGGER.info("Received the Inputs as : {} {} {} {} {} {}", assetClass, serviceHost, servicePort, tradeCount, maxNoOfLegs, maxNoOfParties);
        TradeGeneratorService tradeGeneratorService = new TradeGeneratorService(assetClass, serviceHost, servicePort);
        LOGGER.info("Calling the Service now..");
        tradeGeneratorService.serve(tradeCount, maxNoOfLegs, maxNoOfParties);

        LOGGER.info("Closing the TradeCoreStoreAppClient..");
    }
}
