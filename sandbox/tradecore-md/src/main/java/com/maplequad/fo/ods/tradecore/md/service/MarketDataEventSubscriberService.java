package com.maplequad.fo.ods.tradecore.md.service;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.maplequad.fo.ods.tradecore.data.model.trade.AssetClass;
import com.maplequad.fo.ods.tradecore.md.data.access.MarketEventDAO;
import com.maplequad.fo.ods.tradecore.md.data.model.EqMarketEventView;
import com.maplequad.fo.ods.tradecore.md.data.model.FxMarketEventView;
import com.maplequad.fo.ods.tradecore.md.data.model.MarketEventView;
import com.maplequad.fo.ods.tradecore.md.data.model.reuters.JsonFixer;
import com.maplequad.fo.ods.tradecore.md.data.model.reuters.MarketData;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MarketDataEventSubscriberService -
 *
 * @author Madhav Mindhe
 * @since :   09/09/2017
 */
public class MarketDataEventSubscriberService {

    private static Logger LOGGER = LoggerFactory.getLogger(MarketDataEventSubscriberService.class);

    private final SubscriptionName subscriptionName;
    private final Meter requests;
    private final Meter errors;
    private volatile boolean stop;
    private volatile boolean ignoreErrors;
    private Subscriber subscriber;
    private ExecutorService errorExecutor = Executors.newSingleThreadExecutor();
    private PubSubMessageReceiver callback;
    private MarketEventDAO marketEventDAO;
    private BlockingQueue<MessageItem> queue = new ArrayBlockingQueue<>(10000000);
    private Map<String, String> prevAskMap = new HashMap<>();
    private Map<String, String> prevBidMap = new HashMap<>();
    private Map<String, String> prevLtpMap = new HashMap<>();
    private Map<String, MarketEventView> marketEventViewMap = new HashMap<>();
    private long LAST_EXEC_TIME = 0;
    private String assetClass = SysEnv.TC_ASSET_CLASS;

    /**
     * @param projectId
     * @param subscriptionId
     * @param marketEventDAO
     * @param registry
     * @param ignoreErrors
     */
    public MarketDataEventSubscriberService(String projectId, String subscriptionId, MarketEventDAO marketEventDAO,
                                            MetricRegistry registry, boolean ignoreErrors) {
        subscriptionName = SubscriptionName.create(projectId, subscriptionId);
        this.marketEventDAO = marketEventDAO;
        this.callback = new PubSubMessageReceiver();
        this.ignoreErrors = ignoreErrors;
        this.errors = registry.meter("errors");
        this.requests = registry.meter("requests");
    }

    /**
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        // create a subscriber bound to the asynchronous message receiver
        /*
        ExecutorProvider executor = InstantiatingExecutorProvider.newBuilder()
            .setExecutorThreadCount(1)
            .build();
*/
        subscriber = Subscriber.defaultBuilder(subscriptionName, callback)
                //.setExecutorProvider(executor)
                .build();
        subscriber.addListener(new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                LOGGER.error("Processing failed: from={}, ex={}", from, failure);
            }
        }, errorExecutor);
        subscriber.startAsync();

        while (true) {
            MessageItem item = queue.take();
            LOGGER.info("Got Market Event: {}", item.timestamp);
            try {
                markRequest();
                this.bufferMarketEvent(item.data);
                this.processMarketEvent();
                /*if (SysEnv.MARKET_SIMULATOR_FLAG) {
                    for (int iCount = 0; iCount < SysEnv.RUN_NO; iCount++) {
                        LOGGER.info("Got Addnl Market Event No : {} at {}", iCount, new Date().getTime());
                        markRequest();
                        this.bufferMarketEvent(MarketDataGen.get());
                        this.processMarketEvent();
                    }
                }*/
            } catch (RuntimeException e) {
                markError();
                if (this.ignoreErrors) {
                    LOGGER.error("Error in processMarketEvent: {}", e.getMessage(), e);
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     *
     */
    private void markError() {
        if (errors != null) {
            errors.mark();
        }
    }

    /**
     *
     */
    private void markRequest() {
        if (requests != null) {
            requests.mark();
        }
    }

    /**
     * This method is used to buffer the processing of Market Data Events
     *
     * @param marketEvent
     */
    private void bufferMarketEvent(String marketEvent) {
        LOGGER.debug("bufferMarketEvent entered");

        if (marketEvent != null) {
            MarketData marketData = new Gson().fromJson(JsonFixer.fix(marketEvent), MarketData.class);
            LOGGER.debug("marketData > {}", marketData);
            String prevAsk = prevAskMap.get(marketData.getRic());
            String prevBid = prevBidMap.get(marketData.getRic());
            String prevLtp = prevLtpMap.get(marketData.getRic());
            MarketEventView marketEventView = null;
            if(AssetClass.FXD.equalsIgnoreCase(assetClass)) {
                marketEventView = new FxMarketEventView(marketData, prevAsk, prevBid);
            } else if(AssetClass.EQT.equalsIgnoreCase(assetClass)) {
                marketEventView = new EqMarketEventView(marketData, prevAsk, prevBid, prevLtp);
            }

            if (marketData.getFields().getAsk() != null) {
                prevAskMap.put(marketData.getRic(), marketData.getFields().getAsk().getValue());
            }
            if (marketData.getFields().getBid() != null) {
                prevBidMap.put(marketData.getRic(), marketData.getFields().getBid().getValue());
            }
            if (marketData.getFields().getTrdPrc1() != null) {
                prevLtpMap.put(marketData.getRic(), marketData.getFields().getTrdPrc1().getValue());
            }
            marketEventViewMap.put(marketData.getRic(), marketEventView);
        }
        LOGGER.debug("bufferMarketEvent exited");
    }

    /**
     * This method is used to insert the buffered data of Market Data Events into DB
     */
    private void processMarketEvent() {
        if (new Date().getTime() > LAST_EXEC_TIME + SysEnv.MD_FB_UPDT_BFR_WINDOW_IN_MS) {
            LOGGER.info("processMarketEvent entered");
            int ricCounter = 0;
            for (String ric : marketEventViewMap.keySet()) {
                marketEventDAO.upsert(marketEventViewMap.get(ric));
                ricCounter++;
            }
            marketEventViewMap = new HashMap<>();
            LAST_EXEC_TIME = new Date().getTime();
            LOGGER.info("processMarketEvent exited with {} updates", ricCounter);
        }
    }

    public PubSubMessageReceiver getCallback() {
        return callback;
    }

    public void stop() {
        subscriber.stopAsync().awaitTerminated();
    }

    public static class MessageItem {
        String data;
        long timestamp;

        public MessageItem(String data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }

    public class PubSubMessageReceiver implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            try {
                long start = System.currentTimeMillis();
                LOGGER.debug("Message Id: {}", message.getMessageId());

                String data = message.getData().toStringUtf8();
                LOGGER.debug("Message Payload Data: {}", data);
                MessageItem item = new MessageItem(data, start);

                queue.put(item);

            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted: ", e);

            } finally {
                consumer.ack();
                LOGGER.debug("Message acked");
            }
        }
    }
}
