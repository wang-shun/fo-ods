package com.maplequad.fo.ods.tradecore.balcm.app;

import com.maplequad.fo.ods.tradecore.balcm.service.TradeCoreBulkAmendGrpcService;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/***
 * TradeCoreBalcmServerApp
 * This is the main class that manages life cycle of Bulk Amends.
 *
 * @author Madhav Mindhe
 * @since :   06/09/2017
 */
public class TradeCoreBalcmServerApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeCoreBalcmServerApp.class);

    /**
     * This is the main method that wires up all the services together.
     *
     * @param args containing the portNumber
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LOGGER.info("Entered main");

        int threadsCnt = SysEnv.TC_BALCM_SRVC_THREADS;
        int port = SysEnv.TC_BALCM_SRVC_PORT;

        //Only used for local testing
        if(SysEnv.LOCAL_RUN_FLAG != null) {
            threadsCnt = 2;
            port = 3001;
        }
            //Reading the system environment variables and setting up the variables.
        if (port == 0){
            throw new IllegalArgumentException("ERROR : Please specify valid port for this process..");
        }
        else {

            // Optionally remove existing handlers attached to j.u.l root logger
            SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

            // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
            // the initialization phase of your application
            SLF4JBridgeHandler.install();
            java.util.logging.Logger.getGlobal().setLevel(Level.INFO);

            Executor exec = Executors.newFixedThreadPool(threadsCnt);
            Server server = ServerBuilder.forPort(port).addService(new TradeCoreBulkAmendGrpcService()).executor(exec).build();
            server.start();

            LOGGER.info("service started..");
            long weekLongRun = new Date().getTime() + 604800000;
            Object sync = new Object();
            while (new Date().getTime() < weekLongRun) {
                synchronized (sync) {
                    sync.wait();
                }
            }
        }
    }
}
