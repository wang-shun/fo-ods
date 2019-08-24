package foods.bigtable.bitemporal;

import foods.bigtable.loadtest.EquityTradeGenerator;
import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.*;
import foods.bigtable.repository.TradesTable;
import foods.bigtable.repository.query.Query;
import org.apache.hadoop.hbase.client.Connection;

import java.util.Collection;
import java.util.List;

/**
 * Created by roman on 20/07/2017.
 */
public class Main {

    private final String projectId;
    private final String instanceId;

    private Connection connection;
    private BitemporalRepository repository;

    public Main(String projectId, String instanceId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
    }

    public void connect() throws Exception {
        System.out.println("Connecting to " + projectId + " / " + instanceId);
        connection = Database.createConnection(projectId, instanceId);
        repository = new BitemporalRepository(connection);
    }

    public void createTables() throws Exception {
        TradesTable.createTable(connection);
    }

    public void close() throws Exception {
        connection.close();
    }

    public void save(Collection<EquityTrade> trades) throws Exception {
        System.out.println("Creating " + trades.size() + " equity trades..");
        long start = System.currentTimeMillis();

        for (EquityTrade trade: trades) {
            repository.put(trade);
            System.out.println("Saved " + trade.getTradeId().getId() + " / " + trade.getTradeVersion());
        }

        long elapsed = System.currentTimeMillis() - start;
        double avg = elapsed / (double)trades.size();

        System.out.println("Done. Elapsed: " + elapsed + " ms, Avg: " + avg);
    }

    public void read(Collection<EquityTrade> trades) throws Exception {
        System.out.println("Reading " + trades.size() + " equity trades..");
        long start = System.currentTimeMillis();

        for (EquityTrade trade: trades) {
            read(trade.getTradeId(), trade.getTradeVersion());
        }

        long elapsed = System.currentTimeMillis() - start;
        double avg = elapsed / (double)trades.size();

        System.out.println("Done. Elapsed: " + elapsed + " ms, Avg: " + avg);
    }

    public void read(TradeId tradeId, int version) throws Exception {
        System.out.println("Reading " + tradeId.getId() + " / " + version);
        try {
            EquityTrade trade = repository.get(tradeId, version);
            System.out.println("Got: " + trade);
        } catch (EntityNotFoundException ex) {
            System.out.println("Not found: " + ex);
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
        Main tester = new Main(projectId, instanceId);
        tester.connect();
        tester.createTables();

        EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        {
            Collection<EquityTrade> trades = tradeGenerator.createTrades(50, 4);
            tester.save(trades);
            tester.read(trades);
        }

        //SimpleQuery query = SimpleQuery.forAll(allTradeVersions(tradeId("7189ddd353302475467474")), 1);
        //tester.getAllVersions(tradeId("7189353302475467474"));
        //tester.getLatest(tradeId("7189353302475467474"));

        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(bookGreater("BOOK2")));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(versionGreater(50)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qty(210.2002807559431)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qtyGreater(5)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(PricePredicate.priceGreater(BigDecimal.valueOf(500))));
        //for (EquityTrade trade: trades) {
            //System.out.println(trade.getIdVersion() + " = " + trade.getQuantity());
            //System.out.println(trade.getIdVersion() + " = " + trade.getPrice());
            //System.out.println(trade.getIdVersion() + " = " + trade.getTradeVersion());
        //}

        //System.out.println();

        //trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qtyLess(5)));
        //trades = tester.query(SimpleQuery.forAll(PricePredicate.priceLess(BigDecimal.valueOf(500))));
        //trades = tester.query(SimpleQuery.forAll(bookLess("BOOK2")));
        //trades = tester.query(SimpleQuery.forAll(versionLess(50)));
        //for (EquityTrade trade: trades) {
            //System.out.println(trade.getIdVersion() + " = " + trade.getPrice());
            //System.out.println(trade.getIdVersion() + " = " + trade.getQuantity());
            //System.out.println(trade.getIdVersion() + " = " + trade.getBook());
            //System.out.println(trade.getIdVersion() + " = " + trade.getTradeVersion());
        //}
        tester.close();
    }

}
