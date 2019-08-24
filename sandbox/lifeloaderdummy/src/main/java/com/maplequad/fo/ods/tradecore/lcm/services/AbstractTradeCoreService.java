package com.maplequad.fo.ods.tradecore.lcm.services;


public class AbstractTradeCoreService {

    protected static final String ENC = "UTF-8";
    protected static final String CONF = "src/main/resources/";

    //protected static Timer timer = null;

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

/*    public static Timer startMetric(String name, Logger logger){
        MetricRegistry registry = new MetricRegistry();
        timer = new Timer(new UniformReservoir());
        registry.register(name, timer);
        Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(30, TimeUnit.SECONDS);
        return timer;
    }*/

    

}
