package spanner;

import com.codahale.metrics.Timer;
import com.google.cloud.spanner.*;

import java.util.stream.Collectors;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryWithLegsByPkTester extends QueryTester.BaseQueryTesterFunc {

    private String select;

    @Override
    protected void init() {
        if (Boolean.parseBoolean(args[4]) ) {
            log.info("Excluding BLOB column");
            columns.remove("instrumentIsin");
        }

        // select t.tradeId as t_tradeId, t.tradeVersion as t_tradeVersion, t.book as t_book, t.buySell as t_buySell, t.ccy as t_ccy, t.consideration as t_consideration, t.executionVenue as t_executionVenue, t.instrumentId as t_instrumentId, t.instrumentIsin as t_instrumentIsin, t.instrumentRic as t_instrumentRic, t.price as t_price, t.productType as t_productType, t.quantity as t_quantity, t.settlementCcy as t_settlementCcy, t.client as t_client, t.account as t_account, t.book1000 as t_book1000,
        //        l.tradeId as l_tradeId, l.tradeVersion as l_tradeVersion, l.legId as l_legId, l.book as l_book, l.ccy as l_ccy, l.quantity as l_quantity, l.type as l_type, l.price as l_price, l.client as l_client, l.account as l_account, l.account1m as l_account1m, l.account10m as l_account10m, l.book1000 as l_book1000
        // from trades4 t, trades4_legs l
        // where t.tradeId = @tradeId and t.versionId = @version and l.tradeId = l.tradeId and t.versionId = l.versionId

        String tradeColumnList = columns.stream().map((a) -> "t." + a + " as t_" + a).collect( Collectors.joining( ", " ) );
        String legsColumnList = legColumns.stream().map((a) -> "l." + a + " as l_" + a).collect( Collectors.joining( ", " ) );
        select = "select " + tradeColumnList + ", " + legsColumnList +
            " from trades4 t, trades4_legs l " +
            " where t.tradeId = @tradeId " +
                "and t.tradeVersion = @version " +
                "and t.tradeId = l.tradeId " +
                "and t.tradeVersion = l.tradeVersion";

        log.info("SQL: {}", select);
    }

    @Override
    public void test(DatabaseClient dbClient, TradeDetails trade) throws Exception {
        startTimer();

        Statement statement = Statement.newBuilder(select)
            .bind("tradeId").to(trade.id)
            .bind("version").to(trade.version)
            .build();
        ResultSet ctx = dbClient.singleUse().executeQuery(statement);

        int cnt = 0;
        String tradeId = null;
        while (ctx.next()) {
            //Struct struct = ctx.getCurrentRowAsStruct();
            tradeId = ctx.getString("t_tradeId");
            //String legTradeId = ctx.getString("l_tradeId");
            //long legId = ctx.getLong("l_legId");
            //log.info("Read trade {} / {} = {}", tradeId, ctx.getLong("t_tradeVersion"), legTradeId, legId);
            //log.info("Read trade {} / {} = {}", ctx.getString("tradeId"), ctx.getLong("tradeVersion"), ctx.getString("book"));
            cnt++;
        }

        stopTimer();

        tradesPerQuery.update(cnt);
    }
}
