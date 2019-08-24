package com.maplequad.fo.ods.tradecore.vs.app;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.utils.LogUtils;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import com.maplequad.fo.ods.tradecore.vs.data.access.TradeEventDAO;
import com.maplequad.fo.ods.tradecore.vs.service.TraderEventSubscriberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;


/***
 * TradeCoreViewServerApp
 * This is the main class that connects to the DLT Topic and inserts/deletes data in FirebaseApp that populates UI Grid.
 *
 * TODO
 * 1. Completion Listener - ACK?
 * 2. Filter - Provision is there
 * 3. EventType - Insert or Delete from Cache? - Both methods are there but only insert enabled now
 * 4. Multiple Tables - One for Each User or One for Each Filter
 * 5. How multiple pods in cluster will affect consumption from topic?? one copy each or one msg one pod
 * 6. Performance for 20000 trades
 * 7. Throughput of 2000 TradeEvents Per Sec
 *
 * @author Madhav Mindhe
 * @since :   26/08/2017
 */
public class TradeCoreViewServerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeCoreViewServerApp.class);

    public static void main(String[] args) throws Exception {

        TraderEventSubscriberService subscriber;

        LOGGER.info("classpath: {}", System.getProperty("java.class.path"));

        String projectId = SysEnv.TC_PROJECT_ID;
        String assetClass = SysEnv.TC_ASSET_CLASS;
        String subscriptionId = SysEnv.TC_VS_DLT_TOPIC_SUBS;
        LOGGER.info("Starting with project={}, subscription={}", projectId, subscriptionId);

        String serviceAccountPath = SysEnv.TC_FB_SRVC_ACCT_KEY;
        String databaseURL = SysEnv.TC_FB_DB_URL;
        String tradeGrid = SysEnv.TC_FB_BASE_GRID;
        boolean perfMetricsOnFlag = false;
        boolean ignoreErrors = false;
        boolean saveToFirebase = SysEnv.TC_FB_SAVE_FLAG;
        LOGGER.info("ignoreErrors={}, saveToFirebase={}", ignoreErrors, saveToFirebase);

        LogUtils.configureLog4jBridge();

        String authId = SysEnv.TC_FB_AUTH_ID;
        String authValue = SysEnv.TC_FB_AUTH_VALUE;

        Trade tradeFilter = new Trade();
        tradeFilter.setAssetClass(assetClass);

        TradeEventDAO tradeEventDAO = null;
        if (saveToFirebase) {
            tradeEventDAO = new TradeEventDAO(serviceAccountPath, databaseURL, tradeGrid, authId, authValue);
        }

        MetricRegistry registry = new MetricRegistry();
        subscriber = new TraderEventSubscriberService
                (projectId, subscriptionId, tradeEventDAO, registry, ignoreErrors, tradeFilter);
        if (perfMetricsOnFlag) {
            Slf4jReporter reporter = Slf4jReporter.forRegistry(registry)
                    .outputTo(LOGGER)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .build();
            reporter.start(1, TimeUnit.MINUTES);
        }

        subscriber.start();

    }

}
