package foods.ult.pubsub;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UltMain {

    private static Logger log = LoggerFactory.getLogger(UltMain.class);

    public static void main(String[] args) throws Exception {

      int tradeCount = Integer.parseInt(args[0]);
      String assetClass = args[1];
      int numLegs = Integer.parseInt(args[2]);
      int numParties = Integer.parseInt(args[3]);
      String topic = args[4];
      String serialNumber = args[5];
      int threads = Integer.parseInt(args[6]);
      int delay = Integer.parseInt(args[7]);
      boolean publishForReal = args.length < 9 || Boolean.parseBoolean(args[8]);
      int workerTradeCount = tradeCount / threads;
      log.info("Starting with tradeCount={}, assetClass={}, numLegs={}, numParties={}, topic={}, serial={}, threads={}, workerTradeCount={}, delay={}, publishForReal={}",
          tradeCount, assetClass, numLegs, numParties, topic, serialNumber, threads, workerTradeCount, delay, publishForReal);

      LogUtils.configureLog4jBridge();

      MetricRegistry registry = new MetricRegistry();
      LogUtils.startMetricsReporter(registry, log, Slf4jReporter.LoggingLevel.WARN);

      String action = "CREATE";
      String state = "NEW";

      TradeFactory factory = new TradeFactory(assetClass, numLegs, numParties);
      Publisher publisher = createTopic(topic, publishForReal);

      List<UltWorker> workers = new ArrayList<>();
      for (int i = 0; i < threads; i++) {
        UltWorker worker = new UltWorker(registry, action, state, serialNumber, factory, publisher, workerTradeCount, delay);
        workers.add(worker);
        log.info("Created worker {}", i);
      }

      ExecutorService executor = Executors.newFixedThreadPool(threads);
      long start = System.currentTimeMillis();
      log.info("Starting workers");
      executor.invokeAll(workers);

      long generateElapsed = (System.currentTimeMillis() - start) / 1000;
      log.info("Waiting for the publisher to stop...");

      if (publisher != null) {
        publisher.shutdown();
      }

      long publishElapsed = (System.currentTimeMillis() - start) / 1000 - generateElapsed;

      log.info("Done. Generate = {} s, publish = {} s, total = {} s", generateElapsed, publishElapsed,
          generateElapsed + publishElapsed);

      executor.shutdown();
    }

    private static Publisher createTopic(String topic, boolean publishForReal) throws IOException {
      Publisher publisher = null;

      if (publishForReal) {
        TopicName topicName = TopicName.create(ServiceOptions.getDefaultProjectId(), topic);
        publisher = Publisher.defaultBuilder(topicName).build();
      } else {
        log.warn("=============== MESSAGE PUBLISHING IS SWITCHED OFF ");
      }
      return publisher;
    }

}
