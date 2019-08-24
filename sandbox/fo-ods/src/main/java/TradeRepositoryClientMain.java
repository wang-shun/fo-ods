import com.google.gson.Gson;
import foods.bigtable.loadtest.EquityTradeGenerator;
import foods.bigtable.repository.query.SimpleQuery;
import foods.traderepository.proto.*;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static foods.bigtable.repository.query.predicate.VersionPredicate.versionLess;

/**
 * Created by roman on 20/07/2017.
 */
public class TradeRepositoryClientMain {

  private static final String SVC_NAME = "TRADE_REPO_SVC_NAME";
  private static Logger log = LoggerFactory.getLogger(TradeRepositoryClientMain.class);


  public static void main(String[] args) throws Exception {
    log.info("Hello from TradeRepositoryClientMain");
    log.info("args: {}", Arrays.asList(args));
    log.info("system properties: {}", System.getProperties());
    log.info("env: {}", System.getenv());

    String repo_svc_name = System.getenv(SVC_NAME);
    log.info("Discovered trade repo service name={}", repo_svc_name);
    if (repo_svc_name == null) {
      throw new RuntimeException("Please set the trade repository service name using env variable " + SVC_NAME);
    }

    String repo_hostname = System.getenv(repo_svc_name + "_SERVICE_HOST");
    log.info("Discovered trade repo service host={}", repo_hostname);

    String repo_port = System.getenv(repo_svc_name + "_SERVICE_PORT");
    log.info("Discovered trade repo service port={}", repo_port);

    int port = Integer.valueOf(repo_port);

    String tradeCountStr = System.getenv("TRADE_COUNT");
    log.info("Discovered trade count={}", tradeCountStr);
    int tradeCount = tradeCountStr == null ? 10 : Integer.valueOf(tradeCountStr);

    Channel channel = ManagedChannelBuilder.forAddress(repo_hostname, port).usePlaintext(true).build();
    TradeRepositoryGrpc.TradeRepositoryBlockingStub blockingStub = TradeRepositoryGrpc.newBlockingStub(channel);
    //asyncStub = RouteGuideGrpc.newStub(channel);

    log.info("client started");
    long start = System.currentTimeMillis();

    EquityTradeGenerator generator = new EquityTradeGenerator();
    Gson gson = new Gson();

    for (int i = 0; i < tradeCount; i++) {
      log.info("====================== " + i);
      foods.bigtable.model.EquityTrade trade = generator.createTrade();
      log.info("trade: {}", trade);

      String json = gson.toJson(trade);
      log.info("json: {}", json);

      EquityTrade grpcTrade = EquityTrade.newBuilder().setJson(json).build();
      SaveTradeRequest request = SaveTradeRequest.newBuilder().setTrade(grpcTrade).build();
      SaveTradeResponse response = blockingStub.saveTrade(request);
      log.info("response: {}", response);
    }
    long elapsed = System.currentTimeMillis() - start;
    double avg = elapsed / (double) tradeCount;
    log.info("{} trades took {} ms to save, avg={} ms/trade", tradeCount, elapsed, avg);


    int getCnt = 10;
    while (getCnt-- > 0) {
      /////////////////// get latest version //////////////////////////////////////////////////
      // 1003591809567632540
      GetLatestVersionRequest request = GetLatestVersionRequest.newBuilder().setTradeId("1003591809567632540").build();
      GetLatestVersionResponse result = blockingStub.getLatestVersion(request);
      if (result.hasTrade()) {
        log.info("Found trade {}", result.getTrade().getJson());
      } else {
        log.info("Not found");
      }
    }


    int queryCnt = 10;
    while (queryCnt-- > 0) {
      /////////////////// query trades //////////////////////////////////////////////////
      // 1003591809567632540
      SimpleQuery query = SimpleQuery.forAll(versionLess(2));
      String json = gson.toJson(query);

      QueryPredicate qp1 = QueryPredicate.newBuilder().setPredicateType(PredicateType.BOOK).setCompareOp(CompareOpType.EQUAL).setValue("BOOK2").build();
      QueryPredicate qp2 = QueryPredicate.newBuilder().setPredicateType(PredicateType.CCY).setCompareOp(CompareOpType.EQUAL).setValue("GBP").build();
      FindTradesRequest request = FindTradesRequest.newBuilder().addPredicate(qp1).addPredicate(qp2).setMaxRows(10).build();
      log.info("Request: {}", request);

      FindTradesResponse result = blockingStub.findTrades(request);
      log.info("Found {} trades", result.getTradeCount());
      for (EquityTrade trade: result.getTradeList()) {
        log.info("Trade: {}", trade);
      }
    }


  }
}
