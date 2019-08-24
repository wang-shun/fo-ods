import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static foods.bigtable.repository.TradesTable.*;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.EQUAL;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.GREATER;

/**
 * Created by roman on 20/07/2017.
 */
public class FullScanMain {

    private static Logger log = LoggerFactory.getLogger(FullScanMain.class);

    public static void main(String[] args) throws Exception {
      logParams(args);

      String projectId = System.getenv("PROJECT_ID");
      String bigtableInstanceId = System.getenv("BIGTABLE_INSTANCE_ID");

      log.info("project={}, bigtable instanceId={}", projectId, bigtableInstanceId);

      Configuration conf = BigtableConfiguration.configure(projectId, bigtableInstanceId);
      conf.setInt(BigtableOptionsFactory.MAX_SCAN_TIMEOUT_RETRIES, 1);
      conf.setInt(BigtableOptionsFactory.READ_PARTIAL_ROW_TIMEOUT_MS,
          (int)TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES));
      Connection conn = BigtableConfiguration.connect(conf);

      Table table = conn.getTable(TableName.valueOf(TRADES_TABLE_NAME));

      Scan scan = new Scan();
      FilterList filter = new FilterList();

      SingleColumnValueFilter instrFilter = new SingleColumnValueFilter(FAMILY_TRADE_EQUITY, COLUMN_instrument_id, GREATER, Bytes.toBytes(9223372036850000000L));
      instrFilter.setFilterIfMissing(true);
      filter.addFilter(instrFilter);

      SingleColumnValueFilter bookFilter = new SingleColumnValueFilter(FAMILY_TRADE_EQUITY, COLUMN_book, EQUAL, Bytes.toBytes("BOOK2"));
      bookFilter.setFilterIfMissing(true);
      filter.addFilter(bookFilter);

      SingleColumnValueFilter versionFilter = new SingleColumnValueFilter(FAMILY_META, COLUMN_tradeVersion, GREATER, Bytes.toBytes(98));
      versionFilter.setFilterIfMissing(true);
      filter.addFilter(versionFilter);

      filter.addFilter(new PageFilter(100));

      scan.setFilter(filter);
      log.info("Scan: {}", scan);

      try {
        doFind(table, scan);
      } finally {
        log.info("done!");
      }

    }


  private static void doFind(Table table, Scan scan) throws IOException {
    try (ResultScanner scanner = table.getScanner(scan)) {
      Iterator<Result> rowIt = scanner.iterator();
      int cnt = 0;
      long start = System.currentTimeMillis();
      while (rowIt.hasNext()) {
        Result row = rowIt.next();
        String tradeId = Bytes.toString(row.getValue(FAMILY_META, COLUMN_tradeId));
        log.debug("  read {}", tradeId);
        cnt++;
      }
      long elapsed = TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
      log.debug("Done. Read {} trades in {} ms", cnt, elapsed);
    }
  }

  private static void logParams(String[] args) {
    System.out.println("FullScanMain::main");
    log.info("Hello from FullScanMain");
    log.info("args: " + Arrays.asList(args));
    log.info("classpath: ");
    for (String str: System.getProperty("java.class.path").split(File.pathSeparator)) {
      log.info(str);
    }

    log.info("system properties: {}", System.getProperties());
    log.info("env: {}", System.getenv());
  }
}