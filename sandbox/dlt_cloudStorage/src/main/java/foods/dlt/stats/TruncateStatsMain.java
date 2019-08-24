package foods.dlt.stats;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.foods.lifeloader.lifeloadercreator.processor.LCTrackLog;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

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
