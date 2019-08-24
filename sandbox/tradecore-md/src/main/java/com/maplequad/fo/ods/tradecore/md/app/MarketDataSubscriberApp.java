package com.maplequad.fo.ods.tradecore.md.app;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.maplequad.fo.ods.tradecore.md.data.access.MarketEventDAO;
import com.maplequad.fo.ods.tradecore.md.service.MarketDataEventSubscriberService;
import com.maplequad.fo.ods.tradecore.utils.LogUtils;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;


/***
 * MarketDataSubscriberApp
 * This is the main class that connects to the Market Data Topic and
 * inserts/deletes data in FirebaseApp that populates FX Spot Rates Grid.
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public class MarketDataSubscriberApp {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MarketDataSubscriberApp.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        LOGGER.info("classpath: {}", System.getProperty("java.class.path"));

        String projectId = SysEnv.TC_PROJECT_ID;
        String subscriptionId = SysEnv.TC_MD_TOPIC_SUBS;
        LOGGER.info("Starting with project={}, subscription={}", projectId, subscriptionId);

        String serviceAccountPath = SysEnv.TC_FB_SRVC_ACCT_KEY;
        String databaseURL = SysEnv.TC_FB_DB_URL;
        String tradeGrid = SysEnv.TC_FB_BASE_GRID;
        boolean perfMetricsOnFlag = false;
        boolean ignoreErrors = true;
        boolean saveToFirebase = SysEnv.TC_FB_SAVE_FLAG;
        LOGGER.info("ignoreErrors={}, saveToFirebase={}", ignoreErrors, saveToFirebase);

        LogUtils.configureLog4jBridge();

        String authId = SysEnv.TC_FB_AUTH_ID;
        String authValue = SysEnv.TC_FB_AUTH_VALUE;


        MarketEventDAO marketEventDAO = null;
        if (saveToFirebase) {
            marketEventDAO = new MarketEventDAO(serviceAccountPath, databaseURL, tradeGrid, authId, authValue);
        }

        MetricRegistry registry = new MetricRegistry();
        MarketDataEventSubscriberService subscriber =
                new MarketDataEventSubscriberService(projectId, subscriptionId, marketEventDAO, registry, ignoreErrors);
        if (perfMetricsOnFlag) {
            Slf4jReporter reporter =
                    Slf4jReporter.forRegistry(registry).outputTo(LOGGER).convertRatesTo
                            (TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
            reporter.start(1L, TimeUnit.MINUTES);
        }

        subscriber.start();
    }
}
