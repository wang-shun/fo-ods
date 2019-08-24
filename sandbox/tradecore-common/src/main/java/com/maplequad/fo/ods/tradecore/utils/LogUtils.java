package com.maplequad.fo.ods.tradecore.utils;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class LogUtils {

  public static void configureLog4jBridge() {
    // Optionally remove existing handlers attached to j.u.l root logger
    SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

    // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
    // the initialization phase of your application
    SLF4JBridgeHandler.install();
    LogManager.getLogManager().getLogger("").setLevel(Level.ALL);
    LogManager.getLogManager().getLogger("global").setLevel(Level.ALL);
    java.util.logging.Logger.getGlobal().setLevel(Level.ALL);
  }

  public static void startMetricsReporter(MetricRegistry registry, Logger log, Slf4jReporter.LoggingLevel logLevel) {
    Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
        .outputTo(log)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .withLoggingLevel(logLevel)
        .build();
    reporter.start(1, TimeUnit.MINUTES);
    log.warn("Started metrics reporter");
  }

}
