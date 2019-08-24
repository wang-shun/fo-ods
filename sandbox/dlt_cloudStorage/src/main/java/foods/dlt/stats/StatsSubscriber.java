package foods.dlt.stats;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.foods.lifeloader.lifeloadercreator.processor.LCTrackLog;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsSubscriber {

    private static Logger log = LoggerFactory.getLogger(StatsSubscriber.class);

    private final SubscriptionName subscriptionName;
    private final StatsDao dao;
    private final Histogram ultToLc;
    private final Histogram lcCrudTime;
    private final Histogram dbTime;
    private final Histogram lcToDbTime;
    private final Histogram totalTime;
    private final Histogram lcToDltTime;
    private final Histogram lcTime;
    private final boolean ignoreErrors;
    private final Meter requests;
    private final Meter errors;

    private volatile boolean stop;
    private Subscriber subscriber;
    private ExecutorService errorExecutor = Executors.newSingleThreadExecutor();
    private PubSubMessageReceiver callback;

    public StatsSubscriber(String projectId, String subscriptionId, StatsDao dao, MetricRegistry registry,
                           boolean ignoreErrors) {
        subscriptionName = SubscriptionName.create(projectId, subscriptionId);
        this.dao = dao;
        this.ultToLc = registry.histogram("ultToLc");
        this.lcCrudTime = registry.histogram("lcCrudTime");
        this.lcToDbTime = registry.histogram("lcToDbTime");
        this.dbTime = registry.histogram("dbTime");
        this.totalTime = registry.histogram("totalTime");
        this.lcTime = registry.histogram("lcTime");
        this.lcToDltTime = registry.histogram("lcToDltTime");
        this.errors = registry.meter("errors");
        this.requests = registry.meter("requests");
        this.ignoreErrors = ignoreErrors;
        callback = new PubSubMessageReceiver();
    }

    public void start() {
        // create a subscriber bound to the asynchronous message receiver
        subscriber = Subscriber.defaultBuilder(subscriptionName, callback).build();
        subscriber.addListener(new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                log.error("Processing failed: from={}, ex={}", from, failure);
            }
        }, errorExecutor);
        subscriber.startAsync();
    }

    public PubSubMessageReceiver getCallback() {
        return callback;
    }

    public void stop() {
        subscriber.stopAsync().awaitTerminated();
    }

    class PubSubMessageReceiver implements MessageReceiver {

        private Gson gson = new Gson();

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            long start = System.currentTimeMillis();
            log.info("Message Id: {}", message.getMessageId());

            String data = message.getData().toStringUtf8();
            log.info("Data: {}", data);

            DLTMessage req = gson.fromJson(data, DLTMessage.class);
            log.info("Parsed object: {}", req);

            try {
                requests.mark();
                processUpdate(start, req.log);
            } catch (RuntimeException e) {
                errors.mark();
                if (ignoreErrors) {
                    log.error("Error in processUpdate: {}", e.getMessage(), e);
                } else {
                    throw e;
                }
            }

            consumer.ack();
        }

        public void processUpdate(long start, LCTrackLog req) {
            dao.save(req, start);

            ultToLc.update(req.pubsubtravelTime);
            lcCrudTime.update(req.CRUDprocessTime);
            dbTime.update(req.storageTime);
            lcToDbTime.update(req.getLcToDbTime());
            lcTime.update(req.getLcTime());
            lcToDltTime.update(req.getLcToDlt(start));
            totalTime.update(req.getTotalTime(start));
        }

        public class DLTMessage {
            public LCTrackLog log;
            public Trade trade;

            @Override
            public String toString() {
                final StringBuffer sb = new StringBuffer("DLTMessage{");
                sb.append("log=").append(log);
                sb.append(", trade=").append(trade);
                sb.append('}');
                return sb.toString();
            }
        }
    }
}
