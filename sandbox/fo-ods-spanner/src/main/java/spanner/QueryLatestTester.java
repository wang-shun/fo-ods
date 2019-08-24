package spanner;

import com.codahale.metrics.Timer;
import com.google.cloud.spanner.*;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryLatestTester extends QueryTester.BaseQueryTesterFunc {

    @Override
    public void test(DatabaseClient dbClient, TradeDetails trade) throws Exception {
        ResultSet ctx;
        //Key key = Key.of(trade.id, trade.version);
        Key key = Key.of(trade.id);

        Timer.Context sw = timer.time();
        ctx = dbClient.singleUse().read("trades3", KeySet.prefixRange(key), columns, Options.limit(1));
        sw.stop();

        if (ctx == null) {
            log.info("Got no result for {}", trade);
        } else {
            while(ctx.next()) {
                //Struct struct = ctx.getCurrentRowAsStruct();
                log.info("Read trade {} / {}", ctx.getString("tradeId"), ctx.getLong("tradeVersion"));
            }
        }
    }
}
