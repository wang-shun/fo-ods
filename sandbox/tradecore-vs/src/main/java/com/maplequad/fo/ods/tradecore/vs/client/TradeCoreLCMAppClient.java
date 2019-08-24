package com.maplequad.fo.ods.tradecore.vs.client;

import com.maplequad.fo.ods.tradecore.vs.client.services.TradeGeneratorService;
import org.slf4j.LoggerFactory;

/**
 * TradeCoreLCMAppClient -
 * This class can be used to generate and update the trades by specifying number of trades and also the max legs and
 * max number of parties that need to be there with these trades
 *
 * @author Madhav Mindhe
 * @since :   03/09/2017
 */
public class TradeCoreLCMAppClient {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(com.maplequad.fo.ods.tradecore.store.client.TradeCoreStoreAppClient.class);

    public static void main(String[] args) {
        LOGGER.info("Starting the TradeCoreLCMAppClient..");
        String ERR_MSG = "ERROR : Please provide inputs as Asset Class, Number of Trades, Max Number of Legs and Max Number of Parties";

        if (args != null && args.length == 6) {
            String assetClass = args[0];
            String serviceHost = args[1];//104.155.10.143;
            int servicePort = Integer.parseInt(args[2]);//3000;
            int tradeCount = Integer.parseInt(args[3]);
            int maxNoOfLegs = Integer.parseInt(args[4]);
            int maxNoOfParties = Integer.parseInt(args[5]);

            LOGGER.info("Received the Inputs as : {} {} {} {} {} {}", assetClass, serviceHost, servicePort, tradeCount, maxNoOfLegs, maxNoOfParties);
            TradeGeneratorService tradeGeneratorService = new TradeGeneratorService(assetClass, serviceHost, servicePort);
            LOGGER.info("Calling the Service now..");
            tradeGeneratorService.serve(tradeCount, maxNoOfLegs, maxNoOfParties);
        } else {
            throw new IllegalArgumentException(ERR_MSG);
        }
        LOGGER.info("Closing the TradeCoreLCMAppClient..");
    }
}
