package foods.dlt.pubsub;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleSubscriber {

    private static Logger log = LoggerFactory.getLogger(SimpleSubscriber.class);

    private Subscriber subscriber;

    public SimpleSubscriber(String projectId, String subscriptionFrom, int nThreads) throws Exception {
        SubscriptionName subscriptionName = SubscriptionName.create(projectId, subscriptionFrom);

        ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder()
            .setExecutorThreadCount(nThreads).build();


        subscriber = Subscriber.defaultBuilder(subscriptionName, new PubSubMessageReceiver()).setExecutorProvider(executorProvider).build();
        subscriber.addListener(new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                log.error("Processing failed: from={}, ex={}", from, failure);
            }
        }, Executors.newSingleThreadExecutor());
    }

    public void start() throws Exception {
        log.info("Starting subscriber...");
        subscriber.startAsync();
    }

    class PubSubMessageReceiver implements MessageReceiver {

        private Map<String, Boolean> ids = new ConcurrentHashMap<>(10_000_000);
        private AtomicInteger dupeCount = new AtomicInteger();
        private AtomicInteger msgCount = new AtomicInteger();

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            try {
                //String data = message.getData().toStringUtf8();
                Boolean prev = ids.put(message.getMessageId(), Boolean.TRUE);
                int newMsgCount = msgCount.incrementAndGet();
                Timestamp msgTime = Timestamp.fromProto(message.getPublishTime());
                if (prev != null) {
                    int newDupeCount = dupeCount.incrementAndGet();
                    log.info("Message Id: {}, pubTime: {}, total: {}, dupes: {}, ==== DUPE ====", message.getMessageId(), msgTime, newMsgCount, newDupeCount);
                } else {
                    log.info("Message Id: {}, pubTime: {}, total: {}, dupes: {}", message.getMessageId(), msgTime, newMsgCount, dupeCount.get());
                }
                //log.info("Data: {}", data);

            } catch (Exception e) {
                log.error("Subscriber error", e);

            } finally {
                consumer.ack();
            }
        }
    }
}
