package com.maplequad.fo.ods.ulg.app;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import com.maplequad.fo.ods.ulg.dao.Query;
import com.maplequad.fo.ods.ulg.services.TradeConverterService;
import com.maplequad.fo.ods.ulg.services.TradeGeneratorService;
import com.maplequad.fo.ods.ulg.services.TradePersistenceService;
import com.maplequad.fo.ods.ulg.services.TradeRetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/***
 * UpstreamLoadGen
 * This is the main class that wires up all the services and provide a way to use one or all at once.
 *
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class UpstreamLoadGen {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpstreamLoadGen.class);

    //Instance of all Services
    private static TradeGeneratorService createGenService;
    private static TradeGeneratorService amendGenService;
    private static TradeConverterService conService;
    private static TradePersistenceService perService;
    private static TradeRetrievalService retService;
    private static Timer totalTimer = new Timer(new UniformReservoir());

    /**
     * This is the main method that wires up all the services together.
     *
     * @param args containing the assetClass, tableName, total trades to be generated and batch size
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LOGGER.info("Entered main");
        //default values
        String assetClass = "cash-eq";
        String tableName = "tradecore";
        int tCount = 100000;
        int batchSize = 5000;

        MetricRegistry registry = new MetricRegistry();
        registry.register("total", totalTimer);

        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.MINUTES);

        //Reading the arguments and setting up the variables.
        if (args != null && args.length == 4) {
            LOGGER.info("Running using program inputs..");
            assetClass = args[0];
            tableName = args[1];
            tCount = Integer.parseInt(args[2]);
            batchSize = Integer.parseInt(args[3]);
        }

        //Generating the instances of the services
        createGenService = new TradeGeneratorService(assetClass, "CREATE");
        amendGenService = new TradeGeneratorService(assetClass, "AMEND");
        conService = new TradeConverterService(assetClass);
        perService = new TradePersistenceService(tableName, batchSize);
        retService = new TradeRetrievalService(assetClass, tableName);

        //Invoking the services
        final int tradeCount = tCount;

        //Test Harness
        LOGGER.info("Running test harness now..");
        if(1 == 2)
        //creationToRetrieval(tradeCount);
        retService.serve(perService.serve(conService.serve(createGenService.serve(tradeCount))));


        if(1 == 1)
        creationToPersistence(tradeCount);

        if(1 == 2)
         amendToRetrieval(tradeCount);

        if(1 == 2)
         amendToPersistence(tradeCount);

        if(1 == 2)
        retService.getTradesForExchange("FEX");

        if(1==2)
        retService.getLatestAsOn("3916724595071011079", 1502206147463L,1502206147465L);


        //Let the stats get printed to console!
       Thread.sleep(30000);
        LOGGER.info("Exited main");
    }

    private static void creationToRetrieval(int tradeCount){
        withTimer(totalTimer, "creationToRetrieval", () -> retService.serve(perService.serve(conService.serve(createGenService.serve(tradeCount)))));
    }

    private static void creationToPersistence(int tradeCount){
        withTimer(totalTimer, "creationToPersistence", () -> perService.serve(conService.serve(createGenService.serve(tradeCount))));
    }

    private static void amendToRetrieval(int tradeCount){
        withTimer(totalTimer, "creationToRetrieval", () -> retService.serve(perService.serve(conService.serve(amendGenService.serve(tradeCount)))));
    }

    private static void amendToPersistence(int tradeCount){
        withTimer(totalTimer, "creationToPersistence", () -> perService.serve(conService.serve(amendGenService.serve(tradeCount))));
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    private static <T> T withTimer(Timer timer, String name, SupplierWithException<T> func) {

        Timer.Context ctx = timer.time();
        try {
            T result = func.get();
            return result;

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException("Wrapped exception - " + name + ": " + e.getMessage(), e);

        } finally {
            ctx.stop();
        }
    }

}
