package foods.dlt.stats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TruncateStatsMain {

    private static Logger log = LoggerFactory.getLogger(TruncateStatsMain.class);

    public static void main(String[] args) throws Exception {
        String spannerInstanceId = args[0];
        String spannerDbId = args[1];
        log.info("Starting with spannerInstanceId={}, spannerDbId={}", spannerInstanceId, spannerDbId);
        StatsDao dao = new StatsDao(spannerInstanceId, spannerDbId);
        dao.truncate();
    }
}
