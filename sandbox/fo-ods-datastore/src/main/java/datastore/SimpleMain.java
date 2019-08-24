package datastore;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.google.cloud.datastore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Created by roman on 20/07/2017.
 */
public class SimpleMain {

    private static Logger log = LoggerFactory.getLogger(SimpleMain.class);

    final static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    final static KeyFactory keyFactory = datastore.newKeyFactory().setKind("Equity");

    public static void main(String[] args) {

        final MetricRegistry registry = new MetricRegistry();
        final Timer putTimer = new Timer();
        final Slf4jReporter reporter;

            registry.register("put", putTimer);

            reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
            reporter.start(1, TimeUnit.MINUTES);

        int tradeCount = Integer.parseInt(args[0]);
        int versionsCount = Integer.parseInt(args[1]);
        EquityTradeGenerator gen = new EquityTradeGenerator();


        Collection<EquityTrade> trades = gen.createTrades(tradeCount, versionsCount);
        log.info("Saving {} trades", trades.size());

        for (EquityTrade trade: trades) {
            Timer.Context ctx = putTimer.time();
            addTrade(trade);
            ctx.stop();
            log.info("Saved {} ", trade.getTradeId());
        }
        reporter.report();
    }

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
}
