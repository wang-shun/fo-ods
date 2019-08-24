package com.maplequad.fo.ods.tradecore.utils;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

/***
 * TimerMetricUtils -
 *
 * This abstract class has basic methods needed by all services .
 *
 * @author Madhav Mindhe
 * @since :   12/08/2017
 */
public class TimerMetricUtils {

    protected static final String ENC = "UTF-8";
    protected static final String CONF = "src/main/resources/";

    protected static Timer timer = null;

    public static Timer startMetric(String name, Logger logger) {
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
    }

    public static <T> T withTimer(Timer timer, String name, SupplierWithException<T> func) {

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

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

}
