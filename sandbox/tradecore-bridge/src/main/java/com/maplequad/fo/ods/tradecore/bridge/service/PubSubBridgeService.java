package com.maplequad.fo.ods.tradecore.bridge.service;

import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.api.services.pubsub.Pubsub;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.bridge.pubsub.PubSubHelper;
import com.maplequad.fo.ods.tradecore.bridge.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * PubSubBridgeService -
 *
 * @author Madhav Mindhe
 * @since :   07/09/2017
 */
public class PubSubBridgeService {

    private static Logger LOGGER = LoggerFactory.getLogger(PubSubBridgeService.class);

    private ExecutorService errorExecutor = Executors.newSingleThreadExecutor();
    private PubSubMessageReceiver callback;
    private BlockingQueue<PubsubMessage> queue = new ArrayBlockingQueue<>(SysEnv.BLOCKING_QUEUE_SIZE);

    /**
     */
    public PubSubBridgeService() {
        this.callback = new PubSubMessageReceiver();
    }

    /**
     * @param topicName
     * @param subscriptionName
     */
    private void checkSourceAndSubscription(String topicName, String subscriptionName) {
        LOGGER.info("Subscribing to topic {} with subscription name as {}", topicName, subscriptionName);
        try {
            PubSubHelper helper = new PubSubHelper();
            Pubsub pubsubClient = helper.getClient();
            boolean isSubscriptionExists = helper.checkSubscriptionExists(pubsubClient, subscriptionName);
            if (!isSubscriptionExists) {
                boolean isTopicExists = helper.checkTopicExists(pubsubClient, topicName);
                if (!isTopicExists) {
                    LOGGER.info("Provided topic {} is unavailable. Trying to create one if possible for you", topicName);
                    helper.createTopic(pubsubClient, topicName);
                    //throw new IllegalArgumentException("Topic: " + topicName + " not found!");
                }
                helper.createSubscription(pubsubClient, topicName, subscriptionName);
            }
        } catch (IOException ioe){
            LOGGER.error("ERROR : Exception while subscribing to the source topic {} with subs name {}..",
                    topicName, subscriptionName, ioe.getMessage());
        }
    }

    /**
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        Publisher publisher = null;
        LOGGER.info("checkSourceAndSubscription started");
        this.checkSourceAndSubscription(SysEnv.SRC_FULL_TOPIC_NAME,
                SysEnv.getFullSubscriptionName(ServiceOptions.getDefaultProjectId(),SysEnv.SRC_TOPIC_SUBS_NAME));
        LOGGER.info("creating subscriber started");
        Subscriber subscriber = Subscriber.defaultBuilder
                (SubscriptionName.create(ServiceOptions.getDefaultProjectId(),SysEnv.SRC_TOPIC_SUBS_NAME), callback)
                //.setExecutorProvider(executor)
                .build();
        LOGGER.info("creating subscriber finished");
        subscriber.addListener(new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                LOGGER.error("Processing failed: from={}, ex={}", from, failure);
            }
        }, errorExecutor);
        subscriber.startAsync();

        try {
            LOGGER.info("creating publisher started");
            ExecutorProvider pubExecutor =
                    InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(SysEnv.NO_OF_EXEC_THREADS).build();
            publisher = Publisher.defaultBuilder(TopicName.create(SysEnv.DEST_PROJECT_ID, SysEnv.DEST_TOPIC_NAME))
                    .setExecutorProvider(pubExecutor).build();
            LOGGER.info("creating publisher finished.");
            LOGGER.info("Consumption has begun.. waiting for messages now..");
        }catch (IOException ioe){
            LOGGER.error("ERROR : Exception while subscribing to the destination topic {}..",
                    SysEnv.DEST_TOPIC_NAME,  ioe.getMessage());
        }
        while (true) {
            PubsubMessage item = queue.take();
            try {
                //send forward
                if(SysEnv.TXN_LOGGING_FLAG) {
                    LOGGER.info("Sending to Destination...");
                }
                publisher.publish(item);
                if(SysEnv.TXN_LOGGING_FLAG) {
                    LOGGER.info("Sent to Destination...{}", item);
                }


            } catch (Exception e) {
                LOGGER.error("Error in processing market data: {}", e.getMessage(), e);
            }
        }
    }



    /**
     * @return
     */
    public PubSubMessageReceiver getCallback() {
        return callback;
    }

    /**
     *
     */
    //public void stop() {
        //subscriber.stopAsync().awaitTerminated();
    //}


    /**
     *
     */
    public class PubSubMessageReceiver implements MessageReceiver {

        /**
         * @param message
         * @param consumer
         */
        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            try {
                if(SysEnv.TXN_LOGGING_FLAG) {
                    LOGGER.info("Message Id: {}", message.getMessageId());
                }
                String data = message.getData().toStringUtf8();
                if(SysEnv.TXN_LOGGING_FLAG) {
                    LOGGER.info("Message Payload: {}", data);
                }
                queue.put(message);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted: ", e);
            } finally {
                consumer.ack();
                if(SysEnv.TXN_LOGGING_FLAG) {
                    LOGGER.info("Message acked");
                }
            }
        }
    }
}
