package foods.bigtable.loadtest;

import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Database;
import foods.bigtable.repository.EntityNotFoundException;
import foods.bigtable.repository.RealRepository;
import foods.bigtable.repository.query.Query;
import foods.bigtable.repository.query.SimpleQuery;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static foods.bigtable.repository.query.predicate.PrefixPredicate.prefix;

/**
 * Created by roman on 20/07/2017.
 */
public class DumpTrades {

    private static Logger log = LoggerFactory.getLogger(DumpTrades.class);

    private final String projectId;
    private final String instanceId;

    private Connection connection;
    private RealRepository repository;

    public DumpTrades(String projectId, String instanceId) {
        this.projectId = projectId;
        this.instanceId = instanceId;
    }

    public void connect() throws Exception {
        log.info("Connecting to {} / {}", projectId, instanceId);
        connection = Database.createConnection(projectId, instanceId);
        repository = new RealRepository(connection);
    }

    public void close() throws Exception {
        repository.report();
        connection.close();
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

    public static void readTradeData(List<TradeId> tradeId, List<Integer> version) throws IOException {
        InputStream is = new FileInputStream("trades.txt");
        //InputStream is = QueryRowkeyTester.class.getResourceAsStream("/trades.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = r.readLine()) != null) {
            String[] values = line.split(",");
            tradeId.add(TradeId.tradeId(values[0]));
            version.add(Integer.valueOf(values[1]));
        }
        log.info("Read {} trades", tradeId.size());
        r.close();
    }

    public static void main(String[] args) throws Exception {

        log.info("Args: {}", Arrays.asList(args));
        String projectId = args[0];
        String instanceId = args[1];
        int tradeCount = Integer.valueOf(args[2]);

        DumpTrades tester = new DumpTrades(projectId, instanceId);
        tester.connect();

        int tradesPerDigit = tradeCount / 9;
        PrintWriter w = new PrintWriter(new FileWriter("trades.txt"));

        for (int i = 1; i < 10; i++) {
            log.info("Querying trades for {}", i);
            List<EquityTrade> trades = tester.query(SimpleQuery.forAll(tradesPerDigit, prefix(String.valueOf(i))));
            for (EquityTrade trade: trades) {
                w.print(trade.getTradeId().getId());
                w.print(",");
                w.println(trade.getTradeVersion());
            }

        }
        w.close();
        tester.close();
    }


}
