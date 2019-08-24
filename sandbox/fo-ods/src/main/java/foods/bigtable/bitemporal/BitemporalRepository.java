package foods.bigtable.bitemporal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.EntityNotFoundException;
import foods.bigtable.repository.Repository;
import foods.bigtable.repository.query.Query;
import foods.bigtable.repository.query.SimpleQuery;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static foods.bigtable.repository.TradesTable.*;
import static foods.bigtable.repository.query.predicate.AllTradeVersionsPredicate.allTradeVersions;

/**
 * Created by roman on 20/07/2017.
 */
public class BitemporalRepository {

  private static Logger log = LoggerFactory.getLogger(BitemporalRepository.class);

  private static final Long MAX_RESULTS = Long.MAX_VALUE;
  private final Table table;

  private final MetricRegistry registry = new MetricRegistry();
  private final Timer putTimer = new Timer(new UniformReservoir());
  private final Timer getTimer = new Timer(new UniformReservoir());
  private final Timer findTimer = new Timer(new UniformReservoir());

  public BitemporalRepository(Connection conn) throws Exception {
    table = conn.getTable(TableName.valueOf(TRADES_TABLE_NAME));

    registry.register("put", putTimer);
    registry.register("get", getTimer);
    registry.register("find", findTimer);

    final Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
        .outputTo(LoggerFactory.getLogger(BitemporalRepository.class.getName() + ".metrics"))
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    //reporter.start(1, TimeUnit.MINUTES);

  }

  public EquityTrade getLatest(TradeId id) throws Exception {

    Query query = SimpleQuery.forAll(1, allTradeVersions(id));
    List<EquityTrade> trades = find(query);
    if (trades.isEmpty()) {
      throw new EntityNotFoundException("Cannot find a latest trade by id = " + id);
    }
    if (trades.size() > 1) {
      throw new RuntimeException("Got more than one latest trade for trade by id = " + id);
    }

    EquityTrade res = trades.get(0);
    return res;
  }

  public List<EquityTrade> getAllVersions(TradeId id) throws Exception {
    return find(SimpleQuery.forAll(allTradeVersions(id)));
  }

  public EquityTrade get(TradeId id, int version) throws Exception {
    String rowKey = createRowKey(id, version);
    log.info("Get a single trade by row key={}", rowKey);

    Result getResult = withTimer(getTimer, "get", () -> table.get(new Get(Bytes.toBytes(rowKey))));

    if (getResult.isEmpty()) {
      throw new EntityNotFoundException("Cannot find entity by rowKey = " + rowKey);
    }

    EquityTrade trade = readEquityTrade(getResult);
    return trade;
  }

  private EquityTrade readEquityTrade(Result getResult) {
    EquityTrade trade = new EquityTrade();
    trade.setTradeId(new TradeId(Bytes.toString(getResult.getValue(FAMILY_META, COLUMN_tradeId))));
    trade.setTradeVersion(Bytes.toInt(getResult.getValue(FAMILY_META, COLUMN_tradeVersion)));
    //trade.setlastUpdated(Bytes.toInt(getResult.getValue(FAMILY_META, COLUMN_lastUpdated)));
    trade.setProductType(Bytes.toString(getResult.getValue(FAMILY_META, COLUMN_productType)));

    trade.setInstrumentId(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_instrument_id)));
    trade.setInstrumentRic(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_instrument_ric)));
    trade.setInstrumentIsin(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_instrument_isin)));
    trade.setBuySell(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_buySell)));
    trade.setQuantity(Bytes.toDouble(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_quantity)));
    trade.setPrice(Bytes.toBigDecimal(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_price)));
    trade.setConsideration(Bytes.toDouble(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_consideration)));
    trade.setCcy(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_ccy)));
    trade.setSettlementCcy(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_settlementCcy)));
    trade.setExecutionVenue(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_executionVenue)));
    trade.setBook(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_book)));
    //trade.settradeDatetime(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_tradeDatetime)));
    //trade.setvalueDate(Bytes.toString(getResult.getValue(FAMILY_TRADE_EQUITY, COLUMN_valueDate)));

    //trade.settype(Bytes.toString(getResult.getValue(FAMILY_TRADE_LINKAGES, COLUMN_type)));
    //trade.setsystem(Bytes.toString(getResult.getValue(FAMILY_TRADE_LINKAGES, COLUMN_system)));
    //trade.setid(Bytes.toString(getResult.getValue(FAMILY_TRADE_LINKAGES, COLUMN_id)));

    //trade.settype(Bytes.toString(getResult.getValue(FAMILY_TRADE_EVENTS, COLUMN_type)));
    //trade.settimestamp(Bytes.toString(getResult.getValue(FAMILY_TRADE_EVENTS, COLUMN_timestamp)));
    //trade.setuserId(Bytes.toString(getResult.getValue(FAMILY_TRADE_EVENTS, COLUMN_userId)));
    //trade.setfirstTrade(Bytes.toString(getResult.getValue(FAMILY_TRADE_EVENTS, COLUMN_firstTrade)));
    return trade;
  }

  public List<EquityTrade> find(Query query) throws Exception {
    log.info("Executing query {}", query);

    Scan scan = new Scan();
    scan.setFilter(query.getFilter());

    // this doesn't work - another unsupported HBase method?
    //query.getMaxResultSize().ifPresent(scan::setMaxResultSize);

    long maxResultSize = query.getMaxResultSize().orElse(MAX_RESULTS);

    List<EquityTrade> trades = withTimer(findTimer, "find", () -> doFind(scan, maxResultSize));

    return trades;

  }

  private List<EquityTrade> doFind(Scan scan, long maxResultSize) throws IOException {
    List<EquityTrade> result = new ArrayList<>();
    try (ResultScanner scanner = table.getScanner(scan)) {
      Iterator<Result> rowIt = scanner.iterator();
      while (result.size() < maxResultSize && rowIt.hasNext()) {
        EquityTrade trade = readEquityTrade(rowIt.next());
        result.add(trade);
        log.debug("  read {}", trade.getIdVersion());
      }
      log.info("Done. Read {} trades", result.size());
      return result;
    }
  }

  public void put(EquityTrade trade) throws Exception {
    String rowKey = createRowKey(trade);

    Put put = new Put(Bytes.toBytes(rowKey));

    put.addColumn(FAMILY_META, COLUMN_tradeId, Bytes.toBytes(trade.getTradeId().getId()));
    put.addColumn(FAMILY_META, COLUMN_tradeVersion, Bytes.toBytes(trade.getTradeVersion()));
    //put.addColumn(FAMILY_META, COLUMN_lastUpdated, Bytes.toBytes(trade.getLastUpdated()));
    put.addColumn(FAMILY_META, COLUMN_productType, Bytes.toBytes(trade.getProductType()));

    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_instrument_id, Bytes.toBytes(trade.getInstrumentId()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_instrument_ric, Bytes.toBytes(trade.getInstrumentRic()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_instrument_isin, Bytes.toBytes(trade.getInstrumentIsin()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_buySell, Bytes.toBytes(trade.getBuySell()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_quantity, Bytes.toBytes(trade.getQuantity()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_price, Bytes.toBytes(trade.getPrice()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_consideration, Bytes.toBytes(trade.getConsideration()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_ccy, Bytes.toBytes(trade.getCcy()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_settlementCcy, Bytes.toBytes(trade.getSettlementCcy()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_executionVenue, Bytes.toBytes(trade.getExecutionVenue()));
    put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_book, Bytes.toBytes(trade.getBook()));
    //put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_tradeDatetime, Bytes.toBytes(trade.getTradeDatetime()));
    //put.addColumn(FAMILY_TRADE_EQUITY, COLUMN_valueDate, Bytes.toBytes(trade.getValueDate()));

    //put.addColumn(FAMILY_LINKAGES, COLUMN_type, Bytes.toBytes(trade.gettype()));
    //put.addColumn(FAMILY_LINKAGES, COLUMN_system, Bytes.toBytes(trade.getsystem()));
    //put.addColumn(FAMILY_LINKAGES, COLUMN_id, Bytes.toBytes(trade.getid()));

    //put.addColumn(FAMILY_EVENTS, COLUMN_type, Bytes.toBytes(trade.gettype()));
    //put.addColumn(FAMILY_EVENTS, COLUMN_timestamp, Bytes.toBytes(trade.gettimestamp()));
    //put.addColumn(FAMILY_EVENTS, COLUMN_userId, Bytes.toBytes(trade.getuserId()));
    //put.addColumn(FAMILY_EVENTS, COLUMN_firstTrade, Bytes.toBytes(trade.getfirstTrade()));

    log.info("Saving with rowkey={}", rowKey);


    withTimer(putTimer, "put", () -> {
      table.put(put);
      return null;
    });
  }


  @FunctionalInterface
  public interface SupplierWithException<T> {
    T get() throws Exception;
  }

  private <T> T withTimer(Timer timer, String name, SupplierWithException<T> func) {

    Timer.Context ctx = timer.time();
    try {
      T result = func.get();
      return result;

    } catch (RuntimeException e) {
      throw e;

    } catch (Exception e) {
      throw new RuntimeException("Wrapped exception - " + name + ": " + e.getMessage(), e);

    } finally {
      ctx.stop();
    }
  }

  private String createRowKey(EquityTrade trade) {
    TradeId tradeId = trade.getTradeId();
    int tradeVersion = trade.getTradeVersion();
    return createRowKey(tradeId, tradeVersion);
  }

  private String createRowKey(TradeId tradeId, int tradeVersion) {
    String versionString = createPaddedVersionString(tradeVersion);
    return tradeId.getId() + "_" + versionString;
  }

  private String createPaddedVersionString(int tradeVersion) {
    int rowVersion = MAX_VERSION - tradeVersion;
    String rowVersionStr = String.valueOf(rowVersion);

    int padCnt = MAX_VERSION_DIGIT_COUNT - rowVersionStr.length();

    StringBuilder sb = new StringBuilder();
    while (padCnt > 0) {
      sb.append('0');
      padCnt--;
    }
    sb.append(rowVersionStr);
    return sb.toString();
  }

}
