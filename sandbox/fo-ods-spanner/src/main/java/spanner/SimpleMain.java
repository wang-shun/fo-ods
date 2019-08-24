package spanner;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.google.cloud.spanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by roman on 20/07/2017.
 */
public class SimpleMain {

    private static Logger log = LoggerFactory.getLogger(SimpleMain.class);

    private static String tradeTableName;
    private static String legTableName;

    private static MyHisto myHisto = new MyHisto();
    private static MyMeter myMeter = new MyMeter();

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {

        final MetricRegistry registry = new MetricRegistry();
        final Timer warmupTimer = new Timer();
        final Timer putTimer = new Timer();
        final Slf4jReporter reporter;

            registry.register("put", putTimer);
            registry.register("warmup", warmupTimer);

            reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
            reporter.start(1, TimeUnit.MINUTES);

        String instanceId = args[0];
        String databaseId = args[1];
        tradeTableName = args[2];
        legTableName = tradeTableName + "_legs";
        int tradeCount = Integer.parseInt(args[3]);
        int versionsCount = Integer.parseInt(args[4]);
        int legsCount = Integer.parseInt(args[5]);

        EquityTradeGenerator gen = new EquityTradeGenerator();


        // Instantiates a client
        SpannerOptions options = SpannerOptions.newBuilder().build();
        Spanner spanner = options.getService();

        log.info("Saving {} trades", tradeCount);

        log.info("============= CONNECTING =============== ");

        // Name of your instance & database.
        try {
            // Creates a database client
            DatabaseClient dbClient = spanner.getDatabaseClient(DatabaseId.of(
                options.getProjectId(), instanceId, databaseId));

            log.info("============= WARMING UP !!! =============== ");
            {
                Collection<EquityTrade> trades = gen.createTrades(50, versionsCount, legsCount);
                save(warmupTimer, dbClient, trades, false);
                log.info("Saved batch of {} equity trades..", trades.size());
            }
            log.info("============= STARTING !!! =============== ");

            executor.scheduleWithFixedDelay(() -> {
                reportMetrics();
            },1, 1, TimeUnit.MINUTES);

            while (tradeCount > 0)
            {
                int batchTradeCount = Math.min(100, tradeCount);
                Collection<EquityTrade> trades = gen.createTrades(batchTradeCount, versionsCount, legsCount);
                save(putTimer, dbClient, trades, true);
                log.info("Saved batch of {} equity trades..", trades.size());

                tradeCount -= batchTradeCount;
            }



            log.info("============= FINISHED =============== ");

        } finally {
            // Closes the client which will free up the resources used
            spanner.close();
        }

        executor.shutdownNow();
        reportMetrics();
        reportMetricsCsv();
        reporter.report();
    }

    protected static void reportMetrics() {
        myHisto.report();
        myMeter.report();
    }

    protected static void reportMetricsCsv() {
        myHisto.reportCsv();
        myMeter.reportCsv();
    }

    private static void save(Timer putTimer, DatabaseClient dbClient, Collection<EquityTrade> trades, boolean updateMyMetrics) {
        for (EquityTrade trade: trades) {
            //log.info("Inserting {}", trade);
            insertTrade(putTimer, dbClient, trade, updateMyMetrics);
        }
    }

    private static void insertTrade(Timer putTimer, DatabaseClient dbClient, EquityTrade trade, boolean updateMyMetrics) {
        Timer.Context ctx = putTimer.time();
        dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
            @Nullable
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                List<Mutation> mutation = createInsertMutation(trade);
                //log.info("Mutation: {}", mutation);
                transaction.buffer(mutation);
                return null;
            }
        });
        long elapsed = ctx.stop();

        if (updateMyMetrics) {
            int millis = (int) (elapsed / 1_000_000L);
            myHisto.update(millis);
            myMeter.update(millis);
        }

    }

    private static List<Mutation> createInsertMutation(EquityTrade trade) {
        List<Mutation> mutations = new ArrayList<>();
        Mutation tradeMut = Mutation.newInsertBuilder(tradeTableName)
            .set("tradeId").to(trade.getTradeId().getId())
            .set("tradeVersion").to(trade.getTradeVersion())
            .set("productType").to(trade.getTradeVersion())
            .set("instrumentId").to(trade.getInstrumentId())
            .set("instrumentRic").to(trade.getInstrumentRic())
            .set("instrumentIsin").to(trade.getInstrumentIsin())
            .set("buySell").to(trade.getBuySell().equals("B"))
            .set("quantity").to(trade.getQuantity())
            .set("price").to(trade.getPrice().doubleValue())
            .set("consideration").to(trade.getConsideration())
            .set("ccy").to(trade.getCcy())
            .set("settlementCcy").to(trade.getSettlementCcy())
            .set("executionVenue").to(trade.getExecutionVenue())
            .set("book").to(trade.getBook())
            .set("account").to(trade.getAccount())
            .set("account1m").to(trade.getAccount1m())
            .set("account10m").to(trade.getAccount10m())
            .set("client").to(trade.getClient())
            .set("book1000").to(trade.getBook1000())
            .build();
        mutations.add(tradeMut);

        for (EquityTradeLeg leg: trade.getLegs()) {
            Mutation legMut = Mutation.newInsertBuilder(legTableName)
                .set("tradeId").to(leg.getTradeId().getId())
                .set("tradeVersion").to(leg.getTradeVersion())
                .set("legId").to(leg.getLegId())
                .set("type").to(leg.getType())
                .set("quantity").to(leg.getQuantity())
                .set("ccy").to(leg.getCcy())
                .set("book").to(leg.getBook())
                .set("account").to(leg.getAccount())
                .set("account1m").to(leg.getAccount1m())
                .set("account10m").to(leg.getAccount10m())
                .set("client").to(leg.getClient())
                .set("book1000").to(leg.getBook1000())
                .build();
            mutations.add(legMut);
        }
        return mutations;
    }
/*
    private static Key addTrade(EquityTrade trade) {
        Key key = datastore.allocateId(keyFactory.newKey());
        Entity task = Entity.newBuilder(key)
            .set("tradeId", trade.getTradeId().getId())
            .set("version", trade.getTradeVersion())
            .set("productType", trade.getTradeVersion())
            .set("instrumentId", trade.getInstrumentId())
            .set("instrumentRic", trade.getInstrumentRic())
            //.set("instrumentIsin", trade.getInstrumentIsin())
            .set("buySell", trade.getBuySell())
            .set("quantity", trade.getQuantity())
            .set("price", trade.getPrice().doubleValue())
            .set("consideration", trade.getConsideration())
            .set("ccy", trade.getCcy())
            .set("settlementCcy", trade.getSettlementCcy())
            .set("executionVenue", trade.getExecutionVenue())
            .set("book", trade.getBook())
            .build();
        datastore.put(task);
        return key;
    }
    */
}
