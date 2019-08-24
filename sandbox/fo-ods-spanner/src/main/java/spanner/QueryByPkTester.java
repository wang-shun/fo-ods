package spanner;

import com.codahale.metrics.Timer;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Struct;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryByPkTester extends QueryTester.BaseQueryTesterFunc {

    @Override
    public void test(DatabaseClient dbClient, TradeDetails trade) throws Exception {
        Struct ctx;
        Key key = Key.of(trade.id, trade.version);

        Timer.Context sw = timer.time();
        ctx = dbClient.singleUse().readRow("trades3", key, columns);
        sw.stop();

        if (ctx == null) {
            log.info("Got no result for {}", trade);
        } else {
            //log.info("Read trade {}", ctx.getString("tradeId"));
        }
    }
}
