package com.maplequad.fo.ods.tradecore.vs.app;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/***
 * TradeCoreViewServerAppTest -
 *
 * This class is used to test the GRPC Server App by using a wrapper class TradeCoreViewServerApp.
 *
 * @author Madhav Mindhe
 * @since :   15/08/2017
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TradeCoreViewServerAppTest {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreViewServerAppTest.class);
    private static long tradeId = 0;
    //private TradeCoreStoreService tradeCoreStoreService;
    private final int tradeCount = 1;

    public static void main(String[] args) {
        System.out.println(new Date(1504393200000L));
    }

    @Before
    public void setUp() throws Exception {

        //String serviceHost = "35.189.219.191";
        //String serviceHost = "localhost";
        //int servicePort = 5555;
        //this.tradeCoreStoreService = new TradeCoreStoreService(serviceHost, servicePort);
    }

    @After
    public void tearDown() throws Exception {
        //this.tradeCoreStoreService.delete();
        //this.tradeCoreStoreService = null;
    }

}