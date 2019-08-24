package foods.dlt.stats;

import com.codahale.metrics.*;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.processor.DLTMessage;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import foods.dlt.processor.BackOfficeProcesser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class StatsSubscriber implements ApplicationContextAware{

    private static Logger log = LoggerFactory.getLogger(StatsSubscriber.class);

    private  SubscriptionName subscriptionName;
    private  StatsDao dao;


    MetricRegistry registry;
    private  Meter requests;
    private  Meter errors;
    private  Histogram batchSize;
    private  Histogram ultToLc;
    private  Histogram lcCrudTime;
    private  Histogram dbTime;
    private  Histogram lcToDbTime;
    private  Histogram totalTime;
    private  Histogram lcToDltTime;
    private  Histogram lcTime;
    private  Histogram totalLcmToDltTime;
    private Counter queueDepth;

    private  boolean ignoreErrors;

    private volatile boolean stop;
    private Subscriber subscriber;
    private ExecutorService errorExecutor = Executors.newSingleThreadExecutor();
    private PubSubMessageReceiver callback;

    private BlockingQueue<MessageItem> queue = new ArrayBlockingQueue<>(100000);
    private String projectId;
    private String subscriptionId;

    private boolean simUpstream;

    private BackOfficeProcesser backOfficeProcesser;

    public StatsSubscriber(boolean simUpstream, boolean ignoreErrors, boolean disableMetric){
        if(!disableMetric) {
            registry = new MetricRegistry();
            Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                    .outputTo(log)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .withLoggingLevel(Slf4jReporter.LoggingLevel.INFO)
                    .build();
            reporter.start(1, TimeUnit.MINUTES);
            log.info("Started metric reporter");
        }

        projectId= ServiceOptions.getDefaultProjectId();
        this.ignoreErrors=ignoreErrors;
        this.simUpstream=simUpstream;
    }


    public void prepareSubsriber(String projectId, String subscriptionId, StatsDao dao, MetricRegistry registry,
                                 boolean ignoreErrors) {
        subscriptionName = SubscriptionName.create(projectId, subscriptionId);
        this.dao = dao;

        if (metricsEnabled()) {
            this.ultToLc = registry.histogram("ultToLc");
            this.lcCrudTime = registry.histogram("lcCrudTime");
            this.lcToDbTime = registry.histogram("lcToDbTime");
            this.dbTime = registry.histogram("dbTime");
            this.totalTime = registry.histogram("totalTime");
            this.totalLcmToDltTime = registry.histogram("totalLcmToDltTime");
            this.lcTime = registry.histogram("lcTime");
            this.lcToDltTime = registry.histogram("lcToDltTime");
            this.batchSize = registry.histogram("batchSize");
            this.errors = registry.meter("errors");
            this.requests = registry.meter("requests");
            this.queueDepth = registry.counter("queueDepth");
        }

        this.ignoreErrors = ignoreErrors;
        callback = new PubSubMessageReceiver();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        prepareSubsriber(projectId,subscriptionId,dao,registry,ignoreErrors);
    }

    public void start() throws InterruptedException {
        createSubscriber();

        int workersCount = 10;
        ExecutorService workers = Executors.newFixedThreadPool(workersCount);
        for (int i = 0; i < workersCount; i++) {
            workers.submit(() -> {
                try {
                    processQueue();
                } catch (Exception e) {
                    log.error("Worker terminated with {}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private void createSubscriber() {
        // create a subscriber bound to the asynchronous message receiver
        Long maxMemory = null;//Runtime.getRuntime().maxMemory() * 20 / 100L;
        Long maxElements = 10000L;
        FlowControlSettings flowSettings = FlowControlSettings.newBuilder()
            .setMaxOutstandingRequestBytes(maxMemory)
            .setMaxOutstandingElementCount(maxElements)
            .build();
        log.info("Flow control settings: maxMemory={}, maxElements={}, settings={}", maxMemory, maxElements, flowSettings);

        subscriber = Subscriber.defaultBuilder(subscriptionName, callback)
            .setFlowControlSettings(flowSettings)
            .build();

        subscriber.addListener(new Subscriber.Listener() {
            public void failed(Subscriber.State from, Throwable failure) {
                log.error("Processing failed: from={}, ex={}", from, failure);
            }
        }, errorExecutor);
        subscriber.startAsync();
    }

    private void processQueue() throws InterruptedException {
        int maxBatchSize = 20;
        List<MessageItem> batch = new ArrayList<>(maxBatchSize);
        List<DLTMessage> parsedBatch = new ArrayList<>(maxBatchSize);
        while (true) {
            batch.clear();
            parsedBatch.clear();
            drainBatch(maxBatchSize, batch);
            int thisBatchSize = batch.size();

            try {
                parseBatch(batch, parsedBatch);
                queueDepth.dec(thisBatchSize);
                batchSize.update(thisBatchSize);

                if(metricsEnabled()) {
                    requests.mark(thisBatchSize);
                }

                processUpdate(parsedBatch);

            } catch (RuntimeException e) {
                if(metricsEnabled()) {
                    errors.mark(thisBatchSize);
                }
                if (ignoreErrors) {
                    log.error("Error in processUpdate: {}", e.getMessage(), e);
                } else {
                    throw e;
                }
            }
        }
    }

    private void parseBatch(List<MessageItem> batch, List<DLTMessage> parsedBatch) {
        for (MessageItem item: batch) {
            DLTMessage req = GsonWrapper.fromJson(item.data, DLTMessage.class);
            log.info("Parsed trade: {} ostradeid: {}", req.trade.getTradeId(), req.trade.getOsTradeId());
            req.log.DLT_arriveTime = item.timestamp;
            parsedBatch.add(req);
        }
    }

    private void drainBatch(int maxBatchSize, List<MessageItem> batch) throws InterruptedException {
        int batchSize = queue.drainTo(batch, maxBatchSize);
        if (batchSize == 0) {
            // nothing in the queue, make a blocking call
            MessageItem item = queue.take();
            batch.add(item);
        }
    }


    private boolean metricsEnabled() {
        return registry != null;
    }

    public void processUpdate(List<DLTMessage> reqs) {
        dao.save(reqs);

        for (DLTMessage msg: reqs) {
            LCTrackLog req = msg.log;
            invokeBackOfficeProcessor(req, msg.trade);

            if (metricsEnabled()) {
                ultToLc.update(req.pubsubtravelTime);
                lcCrudTime.update(req.CRUDprocessTime);
                dbTime.update(req.storageTime);
                lcToDbTime.update(req.getLcToDbTime());
                lcTime.update(req.getLcTime());
                lcToDltTime.update(req.getLcToDlt(req.DLT_arriveTime));
                totalTime.update(req.getTotalTime(req.DLT_arriveTime));
                totalLcmToDltTime.update(req.DLT_arriveTime - req.LC_arrivalTime);
            }
        }
    }

    private void invokeBackOfficeProcessor(LCTrackLog req, Trade t) {
        if (this.simUpstream){
            boolean error=false;
            String errmsg=null;
            try {
                this.backOfficeProcesser.processTrade(t,req);

            } catch (NoTransitionFoundException | TradeValidationException | IncorrectTransitionforState | NoTradeEventFoundException ex){
                error=true;
                errmsg =ex.getMessage();
                log.error(errmsg);

            } finally {
                if(error)
                    this.backOfficeProcesser.publishSystemFailure(t,errmsg,req.serialNumber);
            }
        }
    }

    public PubSubMessageReceiver getCallback() {
        return callback;
    }

    public void stop() {
        subscriber.stopAsync().awaitTerminated();
    }

    class PubSubMessageReceiver implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            long start = System.currentTimeMillis();
            log.info("Message Id: {}, tradeId: {}", message.getMessageId(), message.getAttributesOrDefault("uuid", "unknown"));

            String data = message.getData().toStringUtf8();
            //log.info("Data: {}", data);
            MessageItem item = new MessageItem(data, start);

            try {
                queue.put(item);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                throw new RuntimeException("Interrupted: ", e);
            }
            consumer.ack();
            queueDepth.inc();
        }
    }

    public static class MessageItem {
        String data;
        long timestamp;

        public MessageItem(String data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
    }
/*
    public static class DLTMessage {
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
*/

    public StatsDao getDao() {
        return dao;
    }

    public void setDao(StatsDao dao) {
        this.dao = dao;
    }
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public BackOfficeProcesser getBackOfficeProcesser() {
        return backOfficeProcesser;
    }

    public void setBackOfficeProcesser(BackOfficeProcesser backOfficeProcesser) {
        this.backOfficeProcesser = backOfficeProcesser;
    }

}
