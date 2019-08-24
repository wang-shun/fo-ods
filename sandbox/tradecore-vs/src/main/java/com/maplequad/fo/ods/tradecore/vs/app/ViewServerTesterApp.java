package com.maplequad.fo.ods.tradecore.vs.app;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;


public class ViewServerTesterApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewServerTesterApp.class);

    private static final String template = "{\"log\":{\"serialNumber\":\"1504606849251\",\"numOfTrades\":1,\"subnumber\":1," +
            "\"ULT_requestTimestamp\":1504606849251,\"LC_arrivalTime\":1504606849552,\"LC_CRUDStartTime\":1504606849552," +
            "\"DB_StartTime\":1504606849598,\"DB_EndTime\":1504606849753,\"LC_CRUDEndTime\":1504606849755," +
            "\"tradeid\":\"TRADE_ID\",\"osTradeid\":\"1504606849251\",\"action\":\"CREATE\"," +
            "\"DLT_arriveTime\":0,\"pubsubtravelTime\":301,\"CRUDprocessTime\":203,\"storageTime\":155}," +
            "\"trade\":{\"tradeEventList\":[{\"tradeId\":\"TRADE_ID\",\"parentTradeId\":\"\"," +
            "\"eventType\":\"CREATE\",\"eventStatus\":\"DONE\",\"eventReference\":\"LCM-123456\",\"eventRemarks\":\"\"," +
            "\"noOfLegs\":0,\"osVersion\":\"\",\"osVersionStatus\":\"\",\"orderId\":\"\"," +
            "\"exchangeExecutionId\":\"FE-MBCK-774\",\"trader\":\"Roman Ivashin\",\"salesman\":\"\"," +
            "\"traderCountry\":\"\",\"salesmanCountry\":\"\"," +
            "\"tradeLegList\":[{\"cfiCode\":\"ES\",\"quantity\":3857,\"price\":588.9,\"grossPrice\":2271387.2," +
            "\"buySellInd\":\"S\",\"tradeId\":\"TRADE_ID\",\"legNumber\":1,\"legType\":\"equities\"," +
            "\"book\":\"Book1\",\"internalProductType\":\"\",\"internalProductRef\":\"\",\"instrumentId\":\"KLR.L\"," +
            "\"ric\":\"KLR.L\",\"isin\":\"\",\"currency\":\"GBP\",\"exchange\":\"LSE\"," +
            "\"validTimeFrom\":{\"seconds\":1504606849,\"nanos\":552000000}," +
            "\"validTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999},\"transactionTimeFrom\":{\"seconds\":1504606849,\"nanos\":712000000},\"transactionTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999}}],\"tradePartyList\":[{\"tradeId\":\"TRADE_ID\",\"partyRef\":\"My Bank Plc.\",\"partyRole\":\"oboParty\",\"validTimeFrom\":{\"seconds\":1504606849,\"nanos\":552000000},\"validTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999},\"transactionTimeFrom\":{\"seconds\":1504606849,\"nanos\":712000000},\"transactionTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999}},{\"tradeId\":\"TRADE_ID\",\"partyRef\":\"Your Bank Plc.\",\"partyRole\":\"counterParty\",\"validTimeFrom\":{\"seconds\":1504606849,\"nanos\":552000000},\"validTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999},\"transactionTimeFrom\":{\"seconds\":1504606849,\"nanos\":712000000},\"transactionTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999}}],\"createdBy\":\"Roman Ivashin\",\"activeFlag\":true,\"validTimeFrom\":{\"seconds\":1504606849,\"nanos\":552000000},\"validTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999}," +
            "\"transactionTimeFrom\":{\"seconds\":TIMESTAMP,\"nanos\":712000000},\"transactionTimeTo\":{\"seconds\":253402300799,\"nanos\":999999999}}]," +
            "\"tradeId\":\"TRADE_ID\",\"tradeDate\":\"Sep 5, 2017 10:20:59 AM\"," +
            "\"primaryAssetClass\":\"equities\",\"productType\":\"cash-equities\",\"origSystem\":\"ViewServerTesterApp\"," +
            "\"osTradeId\":\"1504606849252\"," +
            "\"createdTimeStamp\":{\"seconds\":1504606859,\"nanos\":712000000},\"createdBy\":\"Roman Ivashin\"}}";

    public static void main(String[] args) throws Exception {
        initLogging();

        LOGGER.info("classpath: {}", System.getProperty("java.class.path"));

        String topicTo = args[0];
        Publisher publisher = getPublisher(topicTo);


        long id = 1111111110000000000L;
        long time = System.currentTimeMillis() / 1000;
        String trade = template.replaceAll("TRADE_ID", String.valueOf(id))
                .replaceAll("TIMESTAMP", String.valueOf(time));
        publishTrade(publisher, trade);

        LOGGER.info("Finished");
    }

    private static void publishTrade(Publisher publisher, String trade) throws InterruptedException, java.util.concurrent.ExecutionException {
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(trade)).build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        future.get();
    }

    private static Publisher getPublisher(String topicTo) throws IOException {
        ExecutorProvider pubExecutor = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(1).build();
        return Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topicTo))
                .setExecutorProvider(pubExecutor).build();
    }

    private static void initLogging() {
        // Optionally remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();
        LogManager.getLogManager().getLogger("").setLevel(Level.ALL);
        LogManager.getLogManager().getLogger("global").setLevel(Level.ALL);
        java.util.logging.Logger.getGlobal().setLevel(Level.ALL);
    }

}
