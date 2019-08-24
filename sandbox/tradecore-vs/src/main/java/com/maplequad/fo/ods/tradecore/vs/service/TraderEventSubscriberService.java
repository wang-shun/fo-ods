package com.maplequad.fo.ods.tradecore.vs.service;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.processor.DLTMessage;
import com.maplequad.fo.ods.tradecore.lcm.processor.OldDLTMessage;
import com.maplequad.fo.ods.tradecore.vs.data.access.TradeEventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.maplequad.fo.ods.tradecore.vs.data.model.TradeEventView.createView;

/**
 * TraderEventSubscriberService -
 *
 * @author Madhav Mindhe
 * @since :   25/08/2017
 */
public class TraderEventSubscriberService {

    private static Logger LOGGER = LoggerFactory.getLogger(TraderEventSubscriberService.class);

    private final SubscriptionName subscriptionName;
    private final Meter requests;
    private final Meter errors;
    private volatile boolean stop;
    private volatile boolean ignoreErrors;
    private Subscriber subscriber;
    private ExecutorService errorExecutor = Executors.newSingleThreadExecutor();
    private PubSubMessageReceiver callback;
    private Trade tradeFilter;
    private TradeEventDAO tradeEventDAO;
    private BlockingQueue<MessageItem> queue = new ArrayBlockingQueue<>(10000);
    public TraderEventSubscriberService(String projectId, String subscriptionId, TradeEventDAO tradeEventDAO,
                                        MetricRegistry registry, boolean ignoreErrors, Trade tradeFilter) {
        subscriptionName = SubscriptionName.create(projectId, subscriptionId);
        this.tradeEventDAO = tradeEventDAO;
        this.callback = new PubSubMessageReceiver();
        this.ignoreErrors = ignoreErrors;
        this.tradeFilter = tradeFilter;
        this.errors = registry.meter("errors");
        this.requests = registry.meter("requests");
    }

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

            //TODO New Implementation Does Not Work!
            DLTMessage req = GsonWrapper.fromJson(item.data, DLTMessage.class);
            //DLTMessage req = OldDLTMessage.fromJson(item.data);
            LOGGER.info("Parsed object: {}", req);

            try {
                markRequest();
                this.processTradeEvent(req.trade);
            } catch (RuntimeException e) {
                markError();
                if (this.ignoreErrors) {
                    LOGGER.error("Error in processTradeEvent: {}", e.getMessage(), e);
                } else {
                    throw e;
                }
            }
        }
    }

    private void markError() {
        if (errors != null) {
            errors.mark();
        }
    }

    private void markRequest() {
        if (requests != null) {
            requests.mark();
        }
    }

    public void processTradeEvent(Trade trade) {
        LOGGER.info("processTradeEvent entered for tradeID {}", trade.getTradeId());
        if (tradeEventDAO != null) {
            if (trade.filter(tradeFilter)) {
                if (this.checkIfVsCacheAdditionEvent(trade)) {
                    LOGGER.info("processTradeEvent upsert called for tradeID {}", trade.getTradeId());
                    createView(trade).forEach( view -> tradeEventDAO.upsert(view));
                } else {
                    LOGGER.info("processTradeEvent delete called for tradeID {}", trade.getTradeId());
                    tradeEventDAO.delete(trade.getTradeId());
                }
            } else {
                LOGGER.info("processTradeEvent tradeFilter check failed for tradeID {}", trade.getTradeId());
            }
        }
        LOGGER.info("processTradeEvent exited for tradeID {}", trade.getTradeId());
    }

    private boolean checkIfVsCacheAdditionEvent(Trade trade) {
        return true;
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
                LOGGER.info("Message Id: {}", message.getMessageId());

                String data = message.getData().toStringUtf8();
                LOGGER.info("Message Payload Data: {}", data);
                MessageItem item = new MessageItem(data, start);

                queue.put(item);

            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted: ", e);

            } finally {
                consumer.ack();
                LOGGER.info("Message acked");
            }
        }
    }
}
