package spanner;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spanner.DumpTrades.readTradeData;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryTester {

    private static Logger log = LoggerFactory.getLogger(QueryTester.class);

    private final String dbName;
    private final String instanceId;

    private Spanner spanner;
    private DatabaseClient dbClient;

    public QueryTester(String dbName, String instanceId) {
        this.dbName = dbName;
        this.instanceId = instanceId;
    }

    public void connect() throws Exception {
        log.info("Connecting to {} / {}", instanceId, dbName);
        SpannerOptions options = SpannerOptions.newBuilder().build();
        spanner = options.getService();

        dbClient = spanner.getDatabaseClient(DatabaseId.of(
            options.getProjectId(), instanceId, dbName));
    }

    public void close() throws Exception {
        spanner.close();
    }

    public static void execute(String dbName, String instanceId, int queryCount, BaseQueryTesterFunc testFunc) throws Exception {

        QueryTester tester = new QueryTester(dbName, instanceId);
        tester.connect();
        testFunc.init();

        List<TradeDetails> trades = readTradeData();

        executeWarmUp(20, testFunc, tester, trades);
        executeRealTest(queryCount, testFunc, tester, trades);
    }

    private static void executeRealTest(int queryCount, BaseQueryTesterFunc testFunc, QueryTester tester, List<TradeDetails> trades) throws Exception {
        log.info("Executing {} queries", queryCount);

        try {
            executeTestFunc(tester, testFunc, queryCount, trades, new MetricRegistry());
        } finally {
            log.info("==== Finished!");
            tester.close();
        }
    }

    private static void executeWarmUp(int queryCount, BaseQueryTesterFunc testFunc, QueryTester tester, List<TradeDetails> trades) throws Exception {
        log.info("WARMUP for {} queries", queryCount);

        try {
            executeTestFunc(tester, testFunc, queryCount, trades, new MetricRegistry());
        } finally {
            log.info("==== WARMUP Finished!");
        }
    }

    private static void executeTestFunc(QueryTester tester, BaseQueryTesterFunc testFunc, int queryCount, List<TradeDetails> trades, MetricRegistry registry) throws Exception {

        testFunc.initMetrics(registry);

        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
            .outputTo(log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();
        reporter.start(1, TimeUnit.MINUTES);

        Random r = new Random();
        try {
            for (int i = 0; i < queryCount; i++) {

                int idx = r.nextInt(trades.size());
                testFunc.test(tester.dbClient, trades.get(idx));
            }
        } finally {
            testFunc.stop();
            reporter.report();
            reporter.stop();
        }
    }

    @FunctionalInterface
    public interface QueryTesterFunc {
        void test(DatabaseClient dbClient, TradeDetails trade) throws Exception;
    }

    public static abstract class BaseQueryTesterFunc implements QueryTesterFunc {
        protected static Logger log = LoggerFactory.getLogger(BaseQueryTesterFunc.class);

        protected List<String> columns = Lists.newArrayList(
            "tradeId",
            "tradeVersion",
            "book",
            "buySell",
            "ccy",
            "consideration",
            "executionVenue",
            "instrumentId",
            "instrumentIsin",
            "instrumentRic",
            "price",
            "productType",
            "quantity",
            "settlementCcy",
            "client",
            "account",
            "book1000");

        protected List<String> legColumns = Lists.newArrayList(
            "tradeId",
            "tradeVersion",
            "legId",
            "book",
            "ccy",
            "quantity",
            "type",
            "client",
            "account",
            "account1m",
            "account10m",
            "book1000");


        protected ScheduledExecutorService executor;

        protected String[] args;

        protected String tableName;

        protected Timer timer;
        protected Histogram tradesPerQuery;
        protected Timer.Context sw;

        private MyHisto myHisto;
        private MyMeter myMeter;


        protected void setArgs(String[] args) {
            this.args = args;
        }

        protected void startTimer() {
          sw = timer.time();
        }

        protected void stopTimer() {
            long nanos = sw.stop();

            int millis = (int)(nanos / 1_000_000L);
            myHisto.update(millis);
            myMeter.update(millis);
        }

        protected void init() {
        }

        protected void stop() {
            executor.shutdownNow();
            reportMetrics();
            reportMetricsCsv();
        }

        protected void initMetrics(MetricRegistry metrics) {
            timer = metrics.timer("func");
            tradesPerQuery = metrics.histogram("tradesPerQuery");

            myHisto = new MyHisto();
            myMeter = new MyMeter();

            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleWithFixedDelay(() -> {
                reportMetrics();
                },1, 1, TimeUnit.MINUTES);
        }

        protected void reportMetrics() {
            myHisto.report();
            myMeter.report();
        }

        protected void reportMetricsCsv() {
            myHisto.reportCsv();
            myMeter.reportCsv();
        }
    }

    public static void main(String[] args) throws Exception {

        log.info("Args: {}", Arrays.asList(args));
        String instanceId = args[0];
        String dbName = args[1];
        String funcClass = args[2];
        int queryCount = Integer.valueOf(args[3]);

        Class cl = Class.forName(funcClass);
        Constructor constr = cl.getConstructors()[0];
        BaseQueryTesterFunc func = (BaseQueryTesterFunc)constr.newInstance();
        log.info("Found query func = {}", func);
        func.setArgs(args);

        try {
            QueryTester.execute(dbName, instanceId, queryCount, func);
        } catch (Exception e) {
            log.error("Terminated with exception: ", e);
        }
    }
}
