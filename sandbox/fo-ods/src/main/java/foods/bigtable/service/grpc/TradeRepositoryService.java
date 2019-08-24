package foods.bigtable.service.grpc;

import com.google.gson.Gson;
import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Database;
import foods.bigtable.repository.EntityNotFoundException;
import foods.bigtable.repository.RealRepository;
import foods.bigtable.repository.Repository;
import foods.bigtable.repository.query.SimpleQuery;
import foods.bigtable.repository.query.predicate.Predicate;
import foods.traderepository.proto.*;
import io.grpc.stub.StreamObserver;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static foods.bigtable.repository.TradesTable.*;

public class TradeRepositoryService extends TradeRepositoryGrpc.TradeRepositoryImplBase {

  private static Logger log = LoggerFactory.getLogger(TradeRepositoryService.class);

  private final Repository repository;
  private final Connection connection;

  private final Map<PredicateType, QueryPredicateHandler> predicateHandlers;

  public TradeRepositoryService(String projectId, String bigTableInstanceId) throws Exception {
    connection = Database.createConnection(projectId, bigTableInstanceId);
    repository = new RealRepository(connection);

    Map<PredicateType, QueryPredicateHandler> handlers = new HashMap<>();
    handlers.put(PredicateType.BOOK, new QueryPredicateHandler(FAMILY_TRADE_EQUITY, COLUMN_book, Bytes::toBytes));
    handlers.put(PredicateType.CCY, new QueryPredicateHandler(FAMILY_TRADE_EQUITY, COLUMN_ccy, Bytes::toBytes));
    handlers.put(PredicateType.INSTRUMENT_RIC, new QueryPredicateHandler(FAMILY_TRADE_EQUITY, COLUMN_instrument_ric, Bytes::toBytes));
    predicateHandlers = handlers;
  }

  @Override
  public void saveTrade(SaveTradeRequest request, StreamObserver<SaveTradeResponse> responseObserver) {
    responseObserver.onNext(doSaveTrade(request));
    responseObserver.onCompleted();
  }

  @Override
  public void getLatestVersion(GetLatestVersionRequest request, StreamObserver<GetLatestVersionResponse> responseObserver) {
    responseObserver.onNext(doGetLatestVersion(request));
    responseObserver.onCompleted();
  }

  @Override
  public void findTrades(FindTradesRequest request, StreamObserver<FindTradesResponse> responseObserver) {
    responseObserver.onNext(doFindTrades(request));
    responseObserver.onCompleted();
  }

  private FindTradesResponse doFindTrades(FindTradesRequest request) {

    log.info("FindTrades: {}", request);

    List<Predicate> predicates = new ArrayList<>(request.getPredicateCount());
    for (QueryPredicate predicate: request.getPredicateList()) {
      PredicateType type = predicate.getPredicateType();
      QueryPredicateHandler handler = predicateHandlers.get(type);
      Predicate p = handler.createPredicate(predicate);
      predicates.add(p);
      log.debug("Predicate for type {} is {}", type, p);
    }

    Optional<Long> maxRows = request.getMaxRows() == 0 ? Optional.empty() : Optional.of(request.getMaxRows());
    SimpleQuery query = new SimpleQuery(predicates, maxRows);
    log.debug("FindTrades: {}", query);

    FindTradesResponse.Builder builder = FindTradesResponse.newBuilder();
    Gson gson = new Gson();
    try {
      List<EquityTrade> trades = repository.find(query);
      log.info("Found {} trades", trades.size());
      addTradesToResponse(gson, builder, trades);

    } catch (Exception e) {
      throw new RuntimeException("Cannot execute the query: " + query, e);
    }
    FindTradesResponse response = builder.build();
    return response;

  }

  private SaveTradeResponse doSaveTrade(SaveTradeRequest request) {
    String tradeJson = request.getTrade().getJson();
    log.info("SaveTrade: {}", tradeJson);

    Gson gson = new Gson();
    EquityTrade trade = gson.fromJson(tradeJson, EquityTrade.class);
    log.debug("Parsed trade: {}", trade);

    try {
      repository.put(trade);
      log.info("Saved the trade");
    } catch (Exception e) {
      throw new RuntimeException("Cannot save the trade: " + trade, e);
    }

    SaveTradeResponse response = SaveTradeResponse.newBuilder().build();
    return response;
  }

  private GetLatestVersionResponse doGetLatestVersion(GetLatestVersionRequest request) {
    String tradeId = request.getTradeId();
    log.info("GetLatestVersion: {}", tradeId);

    Gson gson = new Gson();
    GetLatestVersionResponse.Builder responseBuilder = GetLatestVersionResponse.newBuilder();

    try {
      EquityTrade trade = repository.getLatest(TradeId.tradeId(tradeId));
      log.info("Trade found: {}", trade);
      addTradeToResponse(gson, responseBuilder, trade);

    } catch (EntityNotFoundException e) {
      log.info("Trade not found : {}", tradeId);

    } catch (Exception e) {
      throw new RuntimeException("Cannot query the trade: " + tradeId, e);
    }

    GetLatestVersionResponse response = responseBuilder.build();
    return response;
  }

  private void addTradeToResponse(Gson gson, GetLatestVersionResponse.Builder responseBuilder, EquityTrade trade) {
    foods.traderepository.proto.EquityTrade protoTrade = createProtoTrade(gson, trade);
    responseBuilder.setTrade(protoTrade).build();
  }

  private void addTradesToResponse(Gson gson, FindTradesResponse.Builder builder, List<EquityTrade> trades) {
    for (EquityTrade trade: trades) {
      foods.traderepository.proto.EquityTrade protoTrade = createProtoTrade(gson, trade);
      builder.addTrade(protoTrade).build();
    }
  }

  private foods.traderepository.proto.EquityTrade createProtoTrade(Gson gson, EquityTrade trade) {
    String tradeJson = gson.toJson(trade);
    return foods.traderepository.proto.EquityTrade.newBuilder().setJson(tradeJson).build();
  }
}
