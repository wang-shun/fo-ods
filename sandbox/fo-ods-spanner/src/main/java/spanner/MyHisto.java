package spanner;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.ResultSet;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHisto {

  private static Logger log = LoggerFactory.getLogger(MyHisto.class);

  private int interval = 5;
  private int[] buckets = new int[1000];
  private int overflow = 0;
  private volatile int barrier;

  public void update(int value) {
    int idx = value / interval;
    if (idx < buckets.length) {
      buckets[idx]++;
    } else {
      overflow++;
    }
    barrier = value;
  }

  public void report() {
    report("[", "] = ", false);
  }

  public void reportCsv() {
    report("", ", ", true);
  }

  private void report(String prefix, String separator, boolean printZeros) {

    int bb = barrier;

    int lastNonZero = buckets.length - 1;
    while (lastNonZero >= 0 && buckets[lastNonZero] == 0) {
      lastNonZero--;
    }
    log.debug("Last non zero = {}, {}", lastNonZero, bb);

    int total = 0;
    StringBuilder buf = new StringBuilder();

    for (int i = 0; i <= lastNonZero; i++) {
      buf.append(prefix).append(i * interval).append(separator);
      if (printZeros || buckets[i] != 0) {
        buf.append(buckets[i]);
      }
      total += buckets[i];
      buf.append('\n');
    }
    buf.append("overflow = ").append(overflow).append('\n');
    total += overflow;
    buf.append("total = ").append(total);

    log.info("Report: \n{}", buf.toString());
  }

}