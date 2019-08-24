package foods.dlt.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Level;
import java.util.logging.LogManager;

public class SimpleMain {

    private static Logger log = LoggerFactory.getLogger(SimpleMain.class);

    public static void main(String[] args) throws Exception {

        String projectId = args[0];
        String subscriptionId = args[1];
        int nThreads = Integer.parseInt(args[2]);
        log.info("Starting with project={}, subscription={}, nThreads={}",
            projectId, subscriptionId);

        // Optionally remove existing handlers attached to j.u.l root logger
        SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();
        LogManager.getLogManager().getLogger("").setLevel(Level.ALL);
        LogManager.getLogManager().getLogger("global").setLevel(Level.ALL);
        java.util.logging.Logger.getGlobal().setLevel(Level.ALL);



        SimpleSubscriber subscriber = new SimpleSubscriber(projectId, subscriptionId, nThreads);
        subscriber.start();

    }
}
