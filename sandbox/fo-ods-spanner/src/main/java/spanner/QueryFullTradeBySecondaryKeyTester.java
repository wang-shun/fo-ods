package spanner;

import ch.qos.logback.core.util.StringCollectionUtil;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.google.api.client.util.StringUtils;
import com.google.cloud.spanner.*;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryFullTradeBySecondaryKeyTester extends QueryTester.BaseQueryTesterFunc {

  private String select;

  @Override
  protected void init() {
    if (Boolean.parseBoolean(args[4]) ) {
      log.info("Excluding BLOB column");
      columns.remove("instrumentIsin");
    }

    String columnList = columns.stream().collect( Collectors.joining( ", " ) );
    select = "select " + columnList + " from trades3@{FORCE_INDEX=idxAccount} where account = @account";

    log.info("SQL: {}", select);
  }

  @Override
  public void test(DatabaseClient dbClient, TradeDetails trade) throws Exception {

    //log.info("Reading by account = {}", trade.account);

    startTimer();

    Statement statement = Statement.newBuilder(select).bind("account").to(trade.account).build();
    ResultSet ctx = dbClient.singleUse().executeQuery(statement);

    int cnt = 0;
    String tradeId = null;
    while (ctx.next()) {
      //Struct struct = ctx.getCurrentRowAsStruct();
      tradeId = ctx.getString("tradeId");
      tradeId = ctx.getString("book");
      //log.info("Read trade {} / {} = {}", ctx.getString("tradeId"), ctx.getLong("tradeVersion"), ctx.getLong("account"));
      //log.info("Read trade {} / {} = {}", ctx.getString("tradeId"), ctx.getLong("tradeVersion"), ctx.getString("book"));
      cnt++;
    }

    stopTimer();

    tradesPerQuery.update(cnt);

    //log.info("Got {} rows for account {}, tradeId={}", cnt, trade.account, tradeId);
  }

}