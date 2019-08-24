package foods.dlt.pubsub;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;

public class TesterMain {

    private static Logger log = LoggerFactory.getLogger(TesterMain.class);

    public static void main(String[] args) throws Exception {

        MetricRegistry registry = new MetricRegistry();
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
            .outputTo(log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();

        String projectId = args[0];
        String subscriptionId = args[1];
        String topic = args[2];
        boolean isLeader = Boolean.parseBoolean(args[3]);
        int threads = Integer.parseInt(args[4]);
        int msgCount = Integer.parseInt(args[5]);
        int delay = Integer.parseInt(args[6]);
        int batchSize = Integer.parseInt(args[7]);
        log.info("Starting with project={}, subscription={}, topic={}. leader={}, threads={}, msgCount={}, delay={}, batchSize={}",
            projectId, subscriptionId, topic, isLeader, threads, msgCount, delay, batchSize);

        TesterSubscriber subscriber = new TesterSubscriber(projectId, subscriptionId, topic, registry, isLeader, threads, batchSize);
        reporter.start(1, TimeUnit.MINUTES);

        subscriber.start(msgCount, delay);

    }
}
