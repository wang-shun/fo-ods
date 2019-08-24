package com.maplequad.fo.ods.tradecore.balcm.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.maplequad.fo.ods.tradecore.balcm.data.BulkAmendType;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.store.client.services.TradeGeneratorService;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.slf4j.LoggerFactory;

public class TradeCoreBalcmEqClient {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(com.maplequad.fo.ods.tradecore.store.client.TradeCoreStoreAppClient.class);

    public static void main(String[] args) throws InvalidProtocolBufferException {
        LOGGER.info("Starting the TradeCoreBalcmEqClient..");
        long startTime = System.currentTimeMillis();

        String assetClass = SysEnv.TC_ASSET_CLASS;
        String storeServiceHost = SysEnv.TC_STORE_SRVC_HOST;
        int storeServicePort = SysEnv.TC_STORE_SRVC_PORT;
        String balcmServiceHost = SysEnv.TC_BALCM_SRVC_HOST;
        int balcmServicePort = SysEnv.TC_BALCM_SRVC_PORT;
        String tgAssetClass = SysEnv.TC_TG_ASSET_CLASS;
        int runNumber = SysEnv.RUN_NO;

        if(SysEnv.LOCAL_RUN_FLAG != null){
            assetClass = "ird";
            tgAssetClass = "fi-irs-ba";
            storeServiceHost = "104.155.34.173";
            storeServicePort = 3002;
            balcmServiceHost = "130.211.48.239";
            balcmServicePort = 3001;
            runNumber = 8;
        }

        int tradeCount = 20;
        int maxNoOfLegs = 2;
        int maxNoOfParties = 2;

        if (args != null && args.length == 3) {
            tradeCount = Integer.parseInt(args[0]);
            maxNoOfLegs = Integer.parseInt(args[1]);
            maxNoOfParties = Integer.parseInt(args[2]);
        }


        LOGGER.info("Received the Inputs as : {} {} {} {} {} {}", tgAssetClass, storeServiceHost, storeServicePort, tradeCount, maxNoOfLegs, maxNoOfParties);
        TradeGeneratorService tradeGeneratorService = new TradeGeneratorService(tgAssetClass, storeServiceHost, storeServicePort);
        LOGGER.info("Setup finished for run # {} in {} millis", runNumber, System.currentTimeMillis() - startTime);

        long tcStartTime = System.currentTimeMillis();
        LOGGER.info("Calling the Service now for run # {} ", runNumber);
        tradeGeneratorService.serve(tradeCount, maxNoOfLegs, maxNoOfParties);
        LOGGER.info("Created {} Trades (L={}/P={}) for No # {} in {} milliseconds", tradeCount, maxNoOfLegs, maxNoOfParties,
                runNumber, System.currentTimeMillis() - tcStartTime);

        LOGGER.info("Received the Inputs as : {} {} {}", balcmServiceHost, balcmServicePort, runNumber);

        TradeCoreBulkAmendService tradeCoreBulkAmendService = new TradeCoreBulkAmendService(balcmServiceHost, balcmServicePort);

        Tradequery.TradeQuery query = Tradequery.TradeQuery.newBuilder().setByBookInstrumentId(
                Tradequery.QueryByBookAndInstrumentId.newBuilder().
                        //setBookId("290457").
                        setInstrumentId("PQRS.T").
                        build()).
                build();

        double splitratio=0.25;
        BulkAmendOuterClass.Split s =  com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.Split.newBuilder()
                .setInstrumentId("PQRS.T")
                .setRatio(splitratio).build();

        BulkAmendOuterClass.BulkAmend executeBulkAmend = BulkAmendOuterClass.BulkAmend.newBuilder()
                .setType(BulkAmendOuterClass.BulkAmendDetails.newBuilder()
                        .setSplit(s).build())
                .setCreatedBy("XXX")
                .setAssetClass(assetClass)
                .setDescription("Stock split of equity Trades # "+runNumber)
                .setBulkAmendType(BulkAmendType.EQT_STOCK_SPLIT)
                .build();

        BulkAmendResponse init = tradeCoreBulkAmendService.initiateBulkAmend(executeBulkAmend);
        LOGGER.info("Bulk amend response {}", JsonFormat.printer().print(init));

        long bastartTime = System.currentTimeMillis();
        BulkAmendResponse response = tradeCoreBulkAmendService.executeBulkAmend(init.getBulkAmendId());

        LOGGER.info("Created Execute Bulk Amend Request with ID {}", response.getBulkAmendId());
        LOGGER.info("Completed Bulk Amendment of {} Trades (L={}/P={}) in {} milliseconds..",
                response.getImpactedTradesCount(), maxNoOfLegs, maxNoOfParties, System.currentTimeMillis() - bastartTime);
        LOGGER.info("Completed E2E testing TradeCoreBalcmApp for {} Trades (L={}/P={}) in {} milliseconds",
                response.getImpactedTradesCount(), maxNoOfLegs, maxNoOfParties, System.currentTimeMillis() - startTime);
    }

}
