package foods.bigtable.repository;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roman on 20/07/2017.
 */
public class Database {

    private static Logger log = LoggerFactory.getLogger(Database.class);

    public static Connection createConnection(String projectId, String instanceId) {

        Configuration conf = BigtableConfiguration.configure(projectId, instanceId);

        configureRetries(conf);
        configureTimout(conf);

        Connection connection = BigtableConfiguration.connect(conf);
        return connection;
    }

    private static void configureRetries(Configuration conf) {
        String property = BigtableOptionsFactory.MAX_SCAN_TIMEOUT_RETRIES;
        String retriesStr = getEnvValue(property);
        if (retriesStr != null) {
            log.info("Setting BigTable retries to {}", retriesStr);
            conf.setInt(property, Integer.parseInt(retriesStr));
        }
    }

    private static void configureTimout(Configuration conf) {
        String property = BigtableOptionsFactory.READ_PARTIAL_ROW_TIMEOUT_MS;
        String timeoutStr = getEnvValue(property);
        if (timeoutStr != null) {
            log.info("Setting BigTable timeout to {}", timeoutStr);
            conf.setInt(property, Integer.parseInt(timeoutStr));
        }
    }

    private static String getEnvValue(String property) {
        return System.getenv(property.replace(".", "_"));
    }

}
