package foods.bigtable.loadtest;

import com.codahale.metrics.Slf4jReporter;
import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.*;
import foods.bigtable.repository.query.*;
import foods.bigtable.service.grpc.TradeRepositoryService;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static foods.bigtable.model.TradeId.tradeId;
import static foods.bigtable.repository.query.predicate.BookPredicate.book;
import static foods.bigtable.repository.query.predicate.BookPredicate.bookGreater;
import static foods.bigtable.repository.query.predicate.PrefixPredicate.prefix;

/**
 * Created by roman on 20/07/2017.
 */
public class LoadTester {

    private static Logger log = LoggerFactory.getLogger(LoadTester.class);

    private final String projectId;
    private final String instanceId;

    private Connection connection;
    private RealRepository repository;

    public LoadTester(String projectId, String instanceId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
    }

    public void connect() throws Exception {
        log.info("Connecting to {} / {}", projectId, instanceId);
        connection = Database.createConnection(projectId, instanceId);
        repository = new RealRepository(connection);
    }

    public void createTables() throws Exception {
        TradesTable.createTable(connection);
    }

    public void close() throws Exception {
        repository.report();
        connection.close();
    }

    public void save(Collection<EquityTrade> trades) throws Exception {
        log.debug("Creating {} equity trades..", trades.size());
        long start = System.currentTimeMillis();

        for (EquityTrade trade: trades) {
            repository.put(trade);
            log.debug("Saved {} / {}: {}", trade.getTradeId().getId(), trade.getTradeVersion(), trade);
        }

        long elapsed = System.currentTimeMillis() - start;
        double avg = elapsed / (double)trades.size();

        log.debug("Done. Elapsed: {} ms, avg: {}", elapsed, avg);
    }

    public void read(Collection<EquityTrade> trades) throws Exception {
        log.info("Reading " + trades.size() + " equity trades..");
        long start = System.currentTimeMillis();

        for (EquityTrade trade: trades) {
            read(trade.getTradeId(), trade.getTradeVersion());
        }

        long elapsed = System.currentTimeMillis() - start;
        double avg = elapsed / (double)trades.size();

        log.info("Done. Elapsed: " + elapsed + " ms, Avg: " + avg);
    }

    public void read(TradeId tradeId, int version) throws Exception {
        log.info("Reading " + tradeId.getId() + " / " + version);
        try {
            EquityTrade trade = repository.get(tradeId, version);
            log.info("Got: " + trade);
        } catch (EntityNotFoundException ex) {
            log.info("Not found: " + ex);
        }
    }

    public List<EquityTrade> query(Query query) throws Exception {
        return repository.find(query);
    }

    public EquityTrade getLatest(TradeId tradeId) throws Exception {
        return repository.getLatest(tradeId);
    }

    public List<EquityTrade> getAllVersions(TradeId tradeId) throws Exception {
        return repository.getAllVersions(tradeId);
    }

    public static void main(String[] args) throws Exception {

        String projectId = args[0];
        String instanceId = args[1];
        String tradeCount = args[2];
        LoadTester tester = new LoadTester(projectId, instanceId);
        tester.connect();
        tester.createTables();

        int tradeCountInt = Integer.valueOf(tradeCount);
        int versionCountInt = Integer.valueOf(args[3]);

        EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        while (tradeCountInt > 0)
        {
            int batchTradeCount = Math.min(100, tradeCountInt);
            Collection<EquityTrade> trades = tradeGenerator.createTrades(batchTradeCount, versionCountInt);
            tester.save(trades);
            log.info("Saved batch of {} equity trades..", trades.size());

            tradeCountInt -= batchTradeCount;
            //tester.read(trades);
        }

        //SimpleQuery query = SimpleQuery.forAll(allTradeVersions(tradeId("7189ddd353302475467474")), 1);
        //tester.getAllVersions(tradeId("7189353302475467474"));
        //tester.getLatest(tradeId("7189353302475467474"));

        log.info("Executing query");

        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(100, prefix("5")));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(versionGreater(50)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qty(210.2002807559431)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qtyGreater(5)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(PricePredicate.priceGreater(BigDecimal.valueOf(500))));
        //log.info("Got {} rows back ", trades.size());
        //for (EquityTrade trade: trades) {
//            log.info("{} = {}", trade.getIdVersion(), trade.getBook());
            //log.info(trade.getIdVersion() + " = " + trade.getQuantity());
            //log.info(trade.getIdVersion() + " = " + trade.getPrice());
            //log.info(trade.getIdVersion() + " = " + trade.getTradeVersion());
//        }

        //log.info();

        //trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qtyLess(5)));
        //trades = tester.query(SimpleQuery.forAll(PricePredicate.priceLess(BigDecimal.valueOf(500))));
        //trades = tester.query(SimpleQuery.forAll(bookLess("BOOK2")));
        //trades = tester.query(SimpleQuery.forAll(versionLess(50)));
        //for (EquityTrade trade: trades) {
            //log.info(trade.getIdVersion() + " = " + trade.getPrice());
            //log.info(trade.getIdVersion() + " = " + trade.getQuantity());
            //log.info(trade.getIdVersion() + " = " + trade.getBook());
            //log.info(trade.getIdVersion() + " = " + trade.getTradeVersion());
        //}
        tester.close();
    }

}
