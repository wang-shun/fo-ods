package foods.bigtable.loadtest;

import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Database;
import foods.bigtable.repository.RealRepository;
import foods.bigtable.repository.Repository;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static foods.bigtable.loadtest.DumpTrades.readTradeData;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryTester {

    private static Logger log = LoggerFactory.getLogger(foods.bigtable.loadtest.QueryTester.class);

    private final String projectId;
    private final String instanceId;

    private Connection connection;
    private RealRepository repository;

    public QueryTester(String projectId, String instanceId) {
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

    public static void execute(String projectId, String instanceId, int queryCount, QueryTesterFunc testFunc) throws Exception {

        foods.bigtable.loadtest.QueryTester tester = new foods.bigtable.loadtest.QueryTester(projectId, instanceId);
        tester.connect();

        List<TradeId> tradeId = new ArrayList<>(20000);
        List<Integer> version = new ArrayList<Integer>(20000);

        readTradeData(tradeId, version);

        log.info("Executing {} queries", queryCount);

        Random r = new Random();

        try {
            for (int i = 0; i < queryCount; i++) {

                int idx = r.nextInt(tradeId.size());
                testFunc.test(tester.repository, tradeId.get(idx), version.get(idx));
            }
        } finally {
            log.info("==== Finished!");
            tester.close();
        }
    }

    @FunctionalInterface
    public interface QueryTesterFunc {
        void test(Repository repository, TradeId tradeId, int version) throws Exception;
    }

    public static abstract class BaseQueryTesterFunc implements QueryTesterFunc {
        protected static Logger log = LoggerFactory.getLogger(BaseQueryTesterFunc.class);

        protected String[] args;

        protected void setArgs(String[] args) {
            this.args = args;
        }

        protected void init() {
        }
    }

    public static void main(String[] args) throws Exception {

        log.info("Args: {}", Arrays.asList(args));
        String projectId = args[0];
        String instanceId = args[1];
        String funcClass = args[2];
        int queryCount = Integer.valueOf(args[3]);

        Class cl = Class.forName(funcClass);
        Constructor constr = cl.getConstructors()[0];
        BaseQueryTesterFunc func = (BaseQueryTesterFunc)constr.newInstance();
        log.info("Found query func = {}", func);
        func.setArgs(args);
        func.init();


        try {
            QueryTester.execute(projectId, instanceId, queryCount, func);
        } catch (Exception e) {
            log.error("Terminated with exception: ", e);
        }
    }
}
