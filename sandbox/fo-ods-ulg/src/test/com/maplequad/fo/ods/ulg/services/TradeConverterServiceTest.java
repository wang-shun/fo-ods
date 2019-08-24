package com.maplequad.fo.ods.ulg.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeConverterServiceTest {

    private final static Logger LOGGER = LogManager.getLogger(TradeConverterServiceTest.class);

    private TradeGeneratorService genService;
    private TradeConverterService conService;

    @Before
    public void setUp() throws Exception {
        genService = new TradeGeneratorService("cash-eq");
        conService = new TradeConverterService("cash-eq");
    }

    @After
    public void tearDown() throws Exception {
        genService = null;
        conService = null;
    }

    @Test
    public void serve() throws Exception {
        int tCount = 100;
        List cList = conService.serve(genService.serve(tCount));
        assertNotNull(cList);
        assertEquals(tCount, cList.size());
        LOGGER.info("Converted Trades -> {}", cList);
    }

}