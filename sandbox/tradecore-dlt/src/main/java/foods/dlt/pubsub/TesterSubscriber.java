package foods.dlt.pubsub;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TesterSubscriber {

    private static Logger log = LoggerFactory.getLogger(TesterSubscriber.class);

    private final Timer total;
    private final Timer there;
    private final Timer back;
    private final Timer batch;
    private final int batchSize;

    private Subscriber subscriber;
    private Publisher publisher;

    private boolean leader;
    private AtomicInteger count = new AtomicInteger();
    private AtomicInteger currseq = new AtomicInteger();
    private AtomicInteger outoforder = new AtomicInteger();

    public TesterSubscriber(String projectId, String subscriptionFrom, String topicTo,
                            MetricRegistry registry, boolean leader, int threads, int batchSize) throws Exception {
        SubscriptionName subscriptionName = SubscriptionName.create(projectId, subscriptionFrom);
        ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(threads).build();

        subscriber = Subscriber.defaultBuilder(subscriptionName, new PubSubMessageReceiver()).setExecutorProvider(executorProvider).build();
        subscriber.addListener(new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                log.error("Processing failed: from={}, ex={}", from, failure);
            }
        }, Executors.newSingleThreadExecutor());
        this.leader = leader;

        ExecutorProvider pubExecutor = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(threads).build();
        publisher = Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topicTo)).setExecutorProvider(pubExecutor).build();

        this.total = registry.timer("total"); // latency for roundtrip
        this.there = registry.timer("there"); // latency for the outward leg
        this.back = registry.timer("back");   // latency for the return leg
        this.batch = registry.timer("batch"); // latency for publishing a batch of messages

        this.batchSize = batchSize;
    }

    public void start(int msgCount, int delay) throws Exception {
        // create a subscriber bound to the asynchronous message receiver
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            log.info("COUNTER={}", count.get());
            log.info("SEQ={}", currseq.get());
            log.info("OUTOFORDER={}", outoforder.get());
        },
        1, 1, TimeUnit.MINUTES);
        log.info("Starting subscriber...");
        subscriber.startAsync();

        if (leader) {
            log.info("Start publishing...");
            List<ApiFuture<String>> futures = new ArrayList<>(110);
            int pubCount = 0;
            int batchCount = 0;
            int logCount = 0;
            final int logCountMax = 1000;

            while (msgCount > 0) {
                batchCount++;

                Timer.Context timer = batch.time();
                for (int i = 0; i < batchSize && msgCount > 0; i++) {
                    pubCount++;
                    String data = pubCount + ";" + String.valueOf(System.currentTimeMillis());
                    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(data)).build();
                    ApiFuture<String> future =  publisher.publish(pubsubMessage);
                    futures.add(future);
                    if (delay > 0) {
                        Thread.sleep(delay);
                    }
                    msgCount--;
                    logCount++;
                }
                // wait for the batch to be published
                if (delay != -1) {
                    ApiFutures.allAsList(futures).get();
                }
                timer.stop();

                if (logCount >= logCountMax) {
                    log.info("published batch {} / {} msgs", batchCount, pubCount);
                    logCount = 0;
                }
                futures.clear();
            }
            log.info("Finished publishing {} messages", pubCount);
        }
    }

    class PubSubMessageReceiver implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            try {
                long now = System.currentTimeMillis();

                String data = message.getData().toStringUtf8();
                log.debug("Data: {}", data);

                String[] parts = data.split(";");

                // update metrics
                int seq = Integer.parseInt(parts[0]);
                int curr = currseq.get();
                if (seq != curr + 1) {
                    // this message is out of sequence
                    outoforder.incrementAndGet();
                }
                if (seq > curr) {
                    // assume this is the new current seq number
                    currseq.set(seq);
                }

                if (leader) {
                    // parse timestamps in the message and update metrics
                    long sent = Long.parseLong(parts[1]);
                    long received = Long.parseLong(parts[2]);
                    total.update(now - sent, TimeUnit.MILLISECONDS);
                    there.update(received - sent, TimeUnit.MILLISECONDS);
                    back.update(now - received, TimeUnit.MILLISECONDS);
                } else {
                    // follower: add timestamp to the message and send it back
                    String newData = data + ";" + now;
                    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(newData)).build();
                    total.update(1, TimeUnit.MILLISECONDS);
                    publisher.publish(pubsubMessage);
                }
            } catch (Exception e) {
                log.error("Subscriber error", e);
            } finally {
                consumer.ack();
                count.incrementAndGet();
            }
        }
    }
}
