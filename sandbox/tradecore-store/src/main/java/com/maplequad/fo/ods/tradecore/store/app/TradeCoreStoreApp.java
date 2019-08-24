package com.maplequad.fo.ods.tradecore.store.app;

import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreGrpcService;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/***
 * TradeCoreStoreApp
 * This is the main class that wires up all the services and provide a way to use one or all at once.
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class TradeCoreStoreApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeCoreStoreApp.class);

    /**
     * This is the main method that wires up all the services together.
     *
     * @param args containing the portNumber
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LOGGER.info("Entered main");

        Executor exec = Executors.newFixedThreadPool(SysEnv.NO_OF_THREADS);
        Server server = ServerBuilder.forPort(SysEnv.TC_STORE_SRVC_PORT).
                        addService(new TradeCoreStoreGrpcService()).executor(exec).build();
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
