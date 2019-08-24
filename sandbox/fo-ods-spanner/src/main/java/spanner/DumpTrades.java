package spanner;

import com.google.cloud.spanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by roman on 20/07/2017.
 */
public class DumpTrades {

    private static Logger log = LoggerFactory.getLogger(DumpTrades.class);


    public static List<TradeDetails> readTradeData() throws IOException {
        List<TradeDetails> result = new ArrayList<>(50000);
        InputStream is = new FileInputStream("trades.txt");
        BufferedReader r = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = r.readLine()) != null) {
            String[] values = line.split(",");
            TradeDetails v = new TradeDetails(values[0], Integer.valueOf(values[1]), Integer.valueOf(values[3]));
            result.add(v);
        }
        log.info("Read {} trades", result.size());
        r.close();
        return result;
    }

    public static void main(String[] args) throws Exception {

        log.info("Args: {}", Arrays.asList(args));
        String instanceId = args[0];
        String databaseId = args[1];
        String tableName = args[2];
        int account = Integer.valueOf(args[3]);

        EquityTradeGenerator gen = new EquityTradeGenerator();

        // Instantiates a client
        SpannerOptions options = SpannerOptions.newBuilder().build();
        Spanner spanner = options.getService();

        PrintWriter w = new PrintWriter(new FileWriter("trades.txt"));

        DatabaseClient dbClient = spanner.getDatabaseClient(DatabaseId.of(
            options.getProjectId(), instanceId, databaseId));


        String query = "select tradeId, tradeVersion, client, account, account1m, account10m FROM " + tableName + " WHERE account < " + account + "";
        log.info("Executing query: {}", query);
        ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(query));

        log.info("Got result set!");
        int count = 0;

        while (resultSet.next()) {
            w.print(resultSet.getString("tradeId"));
            w.print(",");

            w.print(resultSet.getLong("tradeVersion"));
            w.print(",");

            w.print(resultSet.getString("client"));
            w.print(",");

            w.print(resultSet.getLong("account"));
            w.print(",");

            w.print(resultSet.getLong("account1m"));
            w.print(",");

            w.print(resultSet.getLong("account10m"));
            count ++;
            w.println();

            if (count %1000 == 0) {
                log.info("Read {} trades", count);
                w.flush();
            }
        }
        w.close();
        resultSet.close();
        spanner.close();

        log.info("Wrote {} trades", count);
    }


}
