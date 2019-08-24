package foods.dlt.stats;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StatsMain {

    private static Logger log = LoggerFactory.getLogger(StatsMain.class);

    public static void main(String[] args) throws Exception {


        ApplicationContext context =  new ClassPathXmlApplicationContext("SpringBean.xml");

        StatsSubscriber sub = context.getBean("StatsSubscriber",StatsSubscriber.class);

        sub.start();

        //MetricRegistry registry=null;
/*
        MetricRegistry registry = new MetricRegistry();
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
            .outputTo(log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();

        String projectId = args[0];
        String subscriptionId = args[1];
        log.info("Starting with project={}, subscription={}", projectId, subscriptionId);

        String spannerInstanceId = args[2];
        String spannerDbId = args[3];
        boolean ignoreErrors = args.length >= 5 && Boolean.parseBoolean(args[4]);
        log.info("Starting with spannerInstanceId={}, spannerDbId={}, ignoreErrors={}", spannerInstanceId, spannerDbId, ignoreErrors);
        StatsDao dao = new StatsDao(spannerInstanceId, spannerDbId);

        StatsSubscriber subscriber = new StatsSubscriber(projectId, subscriptionId, dao, registry, ignoreErrors);
        //reporter.start(1, TimeUnit.MINUTES);

        subscriber.start();

        /*
        StatsSubscriber.PubSubMessageReceiver callback = subscriber.getCallback();
        LCTrackLog req = new LCTrackLog();
        req.ULT_requestTimestamp = 100;
        req.LC_arrivalTime = 101;
        req.LC_CRUDStartTime = 103;
        req.DB_StartTime = 106;
        req.DB_EndTime = 110;
        req.LC_CRUDEndTime = 115;
        req.tradeid = "tradeId-" + System.currentTimeMillis();
        req.osTradeid = "osTradeId";
        req.action = "action";
        req.calculate();
        */

        //callback.processUpdate(120, req);
        //callback.processUpdate(120, req);
    }
}
