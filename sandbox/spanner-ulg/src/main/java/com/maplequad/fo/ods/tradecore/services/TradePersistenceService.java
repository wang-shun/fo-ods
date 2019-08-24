package com.maplequad.fo.ods.tradecore.lcm.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.maplequad.fo.ods.tradecore.dao.Column;
import com.maplequad.fo.ods.tradecore.dao.SpannerHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/***
 * PersistenceService - takes bigtable ready hashmap and inserts database row in bigtable
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradePersistenceService extends AbstractTradeCoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradePersistenceService.class);
    private static int batchSize = 1;
    /***
     * Default constructor is made private
     */
    private TradePersistenceService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with tradeTableName and iBatchSize as input");
    }

    /**
     * This constructor is used  to create an instance of TradePersistenceService
     *
     * @param iBatchSize
     * @throws Exception
     */
    public TradePersistenceService(int iBatchSize) {
        this.batchSize = iBatchSize;
        //timer = this.startMetric("persist", LOGGER);
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register("persist", timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);
    }

    /**
     * This method calls persist method recursively to insert trades into the bigtable.
     *
     * @param tradeList
     */
    public List<String[]> serve(List<Map<String, List<List<Column>>>> tradeList) {
        LOGGER.info("Entered serve to persist {} records", tradeList.size());
        List rowKeys = new ArrayList();
        Iterator<Map<String, List<List<Column>>>> trdItr = tradeList.iterator();
        while (trdItr.hasNext()) {
            Map<String, List<List<Column>>> map = trdItr.next();
            withTimer(timer, "persist", () -> { SpannerHandle.getInstance().insert(map); return  null; });
        }
        LOGGER.info("Exited serve after inserting {} records",tradeList.size());
        return rowKeys;
    }


}
