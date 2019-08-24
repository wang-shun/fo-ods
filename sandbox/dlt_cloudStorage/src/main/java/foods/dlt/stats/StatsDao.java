package foods.dlt.stats;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.foods.lifeloader.lifeloadercreator.processor.LCTrackLog;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StatsDao {

    private static Logger log = LoggerFactory.getLogger(StatsDao.class);

    private DatabaseClient dbClient;

    public StatsDao(String instanceId, String databaseId) throws Exception {
        SpannerOptions options = SpannerOptions.newBuilder().build();
        Spanner spanner = options.getService();

        log.info("============= CONNECTING to spanner =============== ");

        dbClient = spanner.getDatabaseClient(DatabaseId.of(options.getProjectId(), instanceId, databaseId));
        log.info("============= CONNECTED! ========================== ");
    }

    public void save(LCTrackLog req, long statsStart) {
        dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
            @Nullable
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                Mutation mutation = createInsertMutation(req, statsStart);
                //log.info("Mutation: {}", mutation);
                transaction.buffer(mutation);
                return null;
            }

        });
        log.info("Saved row");
    }

    public void truncate() {
        dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
            @Nullable
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                Mutation mutation = Mutation.delete("stats", KeySet.all());
                //log.info("Mutation: {}", mutation);
                transaction.buffer(mutation);
                return null;
            }

        });
        log.info("Truncated table");
    }

    private Mutation createInsertMutation(LCTrackLog log, long statsStart) {
        Mutation tradeMut = Mutation.newInsertBuilder("stats")
            .set("tradeId").to(log.tradeid)
            .set("osTradeid").to(log.tradeid)
            .set("action").to(log.action)
            .set("ult").to(asTs(log.ULT_requestTimestamp))
            .set("ultRaw").to((log.ULT_requestTimestamp))
            .set("lcStart").to(asTs(log.LC_arrivalTime))
            .set("lcStartRaw").to(log.LC_arrivalTime)
            .set("ultToLcTime").to(log.pubsubtravelTime)
            .set("lcCrudStart").to(asTs(log.LC_CRUDStartTime))
            .set("lcCrudStartRaw").to((log.LC_CRUDStartTime))
            .set("lcCrudEnd").to(asTs(log.LC_CRUDEndTime))
            .set("lcCrudEndRaw").to((log.LC_CRUDEndTime))
            .set("lcCrudTime").to(log.CRUDprocessTime)
            .set("dbStart").to(asTs(log.DB_StartTime))
            .set("dbEnd").to(asTs(log.DB_EndTime))
            .set("dbTime").to(log.storageTime)
            .set("lcToDbTime").to(log.getLcToDbTime())
            .set("lcTime").to(log.getLcTime())
            .set("statsStart").to(asTs(statsStart))
            .set("lcToDltTime").to(log.getLcToDlt(statsStart))
            .set("totalTime").to(log.getTotalTime(statsStart))
            .build();
        return tradeMut;
    }

    private Timestamp asTs(long statsStart) {
        return Timestamp.ofTimeMicroseconds(TimeUnit.MILLISECONDS.toMicros(statsStart));
    }
}
