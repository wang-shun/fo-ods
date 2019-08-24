package spanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyMeter {

  private static Logger log = LoggerFactory.getLogger(MyMeter.class);

  private int interval = 1000;
  private int[] buckets = new int[5000];
  private long start;
  private long maxBucketValue;
  private int bucketIdx;
  private int overflow = 0;
  private volatile int barrier;

  public void update(int value) {
    // we have overflow already
    if (bucketIdx >= buckets.length) {
      overflow++;
      barrier = value;
      return;
    }

    long time = System.currentTimeMillis();

    if (start == 0) {
      start = System.currentTimeMillis();
      maxBucketValue = start + interval;
    }

    // move to the correct bucket
    while (bucketIdx <= buckets.length && time > maxBucketValue) {
      start = maxBucketValue;
      maxBucketValue = start + interval;
      bucketIdx++;
    }

    // update the bucket
    if (bucketIdx < buckets.length) {
      buckets[bucketIdx]++;
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
      buf.append(prefix).append(i).append(separator);
      if (printZeros || buckets[i] != 0) {
        buf.append(buckets[i]);
      }
      total += buckets[i];
      buf.append('\n');
    }
    buf.append("overflow = ").append(overflow).append('\n');
    total += overflow;
    buf.append("total = ").append(total);

    log.info("Report: one bucket is for {} ms\n{}", interval, buf.toString());
  }

}