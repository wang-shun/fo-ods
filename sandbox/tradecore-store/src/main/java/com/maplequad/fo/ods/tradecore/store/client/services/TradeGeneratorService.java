package com.maplequad.fo.ods.tradecore.store.client.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.client.model.TradeInfo;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.maplequad.fo.ods.tradecore.utils.TimerMetricUtils.startMetric;
import static com.maplequad.fo.ods.tradecore.utils.TimerMetricUtils.withTimer;

/***
 * Generator - takes sample trade json and gives hashmap for each new trade
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeGeneratorService.class);
    private static TradeInfo tradeInfo;
    private static String assetClass;
    int noOfLegs = 1;
    int noOfParties = 2;
    private TradeCoreStoreService tradeCoreStoreService;
    private Timer timer;

    /***
     * Default constructor is made private
     */
    private TradeGeneratorService() {
        throw new IllegalArgumentException("ERROR : Please use constructor with assetClass as input");
    }

    /**
     * This constructor is used  to create an instance of TradeGeneratorService for given instance of newTradeInfo
     * This constructor is provided so that this class can be used as a static service outside of this application.
     *
     * @param newTradeInfo
     */
    public TradeGeneratorService(TradeInfo newTradeInfo) {
        tradeInfo = newTradeInfo;
    }

    /**
     * This constructor is used  to create an instance of TradeGeneratorService for a specific asset class
     *
     * @param assetClass
     */
    public TradeGeneratorService(String assetClass, String serviceHost, int servicePort) {

        this.assetClass = assetClass;
        timer = startMetric("generate", LOGGER);
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register("generate", timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);

        this.tradeCoreStoreService = new TradeCoreStoreService(serviceHost, servicePort);
    }

    /**
     * This method creates the required number of trades
     *
     * @param tradeCount
     * @param maxLeg
     * @param maxParties
     * @return
     */
    private List<String> createTrade(String assetClass, int tradeCount, int maxLeg, int maxParties) {
        // TestCase # 1 - Creating a New Trade
        List<String> tradeIdList = new ArrayList();
        long start = System.currentTimeMillis();
        noOfLegs = maxLeg;
        noOfParties = maxParties;
        for (int i = 0; i < tradeCount; i++) {
            if (maxLeg > 2) {
                noOfLegs = new Random().nextInt(maxLeg - 1) + 1;
            }
            if (maxParties > 2) {
                noOfParties = new Random().nextInt(maxParties - 1) + 1;
            }
            //LOGGER.info("Creating trade with {} leg/s and {} party/ies", noOfLegs, noOfParties);
            withTimer(timer, "generate", () -> {
                        TradeCoreStoreResponse response =
                                tradeCoreStoreService.createTrade(TradeGenerator.getInstance(assetClass).createTrade(noOfLegs, noOfParties));
                        String tradeId = response.getTrade().getTradeId();
                        tradeIdList.add(tradeId);
                        if(SysEnv.TG_LOG_GEN == null || !SysEnv.TG_LOG_GEN.equalsIgnoreCase("OFF")) {
                            LOGGER.info("Created Trade : {} = {}", tradeId, response.getTrade());
                        }
                        return null;
                    }
            );
        }
        long elapsed = System.currentTimeMillis() - start;
        double avg = elapsed / (double) tradeCount;
        LOGGER.info("{} trades took {} ms to save, avg={} ms/trade", tradeCount, elapsed, avg);
        return tradeIdList;
    }


    /**
     * This method calls generate method recursively to generate  a list of trades in input format.
     *
     * @param tradeCount
     */
    public void serve(int tradeCount, int maxLeg, int maxParties) {
        LOGGER.trace("Entered serve");
        List<String> tradeIdList = null;

        tradeIdList = this.createTrade(assetClass, tradeCount, maxLeg, maxParties);
        //this.updateTrade(tradeIdList);

        LOGGER.trace("Exited serve");
    }
}