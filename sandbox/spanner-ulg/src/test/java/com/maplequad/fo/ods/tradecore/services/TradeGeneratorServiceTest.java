package com.maplequad.fo.ods.tradecore.lcm.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeGeneratorServiceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(TradeGeneratorServiceTest.class);

    private TradeGeneratorService genService;

    @org.junit.Before
    public void setUp() throws Exception {
        genService = new TradeGeneratorService("cash-eq","CREATE");
    }

    @org.junit.After
    public void tearDown() throws Exception {
        genService = null;
    }

    @org.junit.Test
    public void serve() throws Exception {
        int tCount = 100;
        List gList = genService.serve(tCount);
        assertNotNull(gList);
        assertEquals(tCount, gList.size());
        LOGGER.info("Generated Trades -> {}", gList);
    }

}