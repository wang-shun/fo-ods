package spanner;

import com.codahale.metrics.*;
import com.google.cloud.spanner.*;
import com.google.common.collect.Lists;
import com.sun.javafx.font.Metrics;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryBySecondaryKeyTester extends QueryTester.BaseQueryTesterFunc {

  protected Iterable<String> columns = Lists.newArrayList(
      "tradeId",
      "tradeVersion",
      "account");

  private Histogram tradesPerQuery;

  @Override
  protected void initMetrics(MetricRegistry metrics) {
    super.initMetrics(metrics);
    tradesPerQuery = metrics.histogram("tradesPerQuery");
  }

  @Override
  public void test(DatabaseClient dbClient, TradeDetails trade) throws Exception {
    ResultSet ctx;
    //Key key = Key.of(trade.id, trade.version);
    Key key = Key.of(trade.account);

    //log.info("Reading by account = {}", trade.account);

    startTimer();
    ctx = dbClient.singleUse().readUsingIndex("trades3", "idxAccount", KeySet.singleKey(key), columns);//, Options.limit(1));

    int cnt = 0;
    String tradeId = null;
    while (ctx.next()) {
      //Struct struct = ctx.getCurrentRowAsStruct();
      tradeId = ctx.getString("tradeId");
      //log.debug("Read trade {} / {} = {}", ctx.getString("tradeId"), ctx.getLong("tradeVersion"), ctx.getLong("account"));
      //log.info("Read trade {} / {} = {}", ctx.getString("book"));
      cnt++;
    }

    stopTimer();

    tradesPerQuery.update(cnt);

    //log.info("Got {} rows for account {}, tradeId={}", cnt, trade.account, tradeId);
  }

}