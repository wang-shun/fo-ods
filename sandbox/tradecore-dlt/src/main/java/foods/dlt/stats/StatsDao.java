package foods.dlt.stats;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.maplequad.fo.ods.tradecore.lcm.processor.DLTMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionRunner;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;

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

    public void save(LCTrackLog req) {
        dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
            @Nullable
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                Mutation mutation = createInsertMutation(req);
                //log.info("Mutation: {}", mutation);
                transaction.buffer(mutation);
                return null;
            }

        });
        log.info("Saved row");
    }

    public void save(List<DLTMessage> reqs) {
        dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
            @Nullable
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                for (DLTMessage req: reqs) {
                    Mutation mutation = createInsertMutation(req.log);
                    //log.info("Mutation: {}", mutation);
                    transaction.buffer(mutation);
                }
                return null;
            }

        });
        log.info("Saved {} rows", reqs.size());
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

    private Mutation createInsertMutation(LCTrackLog log) {
        Mutation tradeMut = Mutation.newInsertBuilder("stats")
            .set("tradeId").to(log.tradeid)
            .set("osTradeid").to(log.osTradeid)
            .set("action").to(log.action)
            .set("ult").to(asTs(log.ULT_requestTimestamp))
            .set("lcStart").to(asTs(log.LC_arrivalTime))
            .set("ultToLcTime").to(log.pubsubtravelTime)
            .set("lcCrudStart").to(asTs(log.LC_CRUDStartTime))
            .set("lcCrudEnd").to(asTs(log.LC_CRUDEndTime))
            .set("lcCrudTime").to(log.CRUDprocessTime)
            .set("dbStart").to(asTs(log.DB_StartTime))
            .set("dbEnd").to(asTs(log.DB_EndTime))
            .set("dbTime").to(log.storageTime)
            .set("lcToDbTime").to(log.getLcToDbTime())
            .set("lcTime").to(log.getLcTime())
            .set("lcToDltTime").to(log.getLcToDlt(log.DLT_arriveTime))
            .set("totalTime").to(log.getTotalTime(log.DLT_arriveTime))
            .set("totalLcmToDltTime").to(log.DLT_arriveTime - log.LC_arrivalTime)
            .set("dltArriveTime").to(asTs(log.DLT_arriveTime))
            .set("serialNumber").to(log.serialNumber)

                //.set("statsStart").to(asTs(log.statsStart))
            .build();
        return tradeMut;
    }

    private Timestamp asTs(long statsStart) {
        return Timestamp.ofTimeMicroseconds(TimeUnit.MILLISECONDS.toMicros(statsStart));
    }
}
