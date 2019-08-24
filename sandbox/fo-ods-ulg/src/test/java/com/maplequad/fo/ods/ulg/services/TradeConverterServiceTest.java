package com.maplequad.fo.ods.ulg.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradeConverterServiceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(TradeConverterServiceTest.class);

    private TradeGeneratorService genService;
    private TradeConverterService conService;

    @Before
    public void setUp() throws Exception {
        genService = new TradeGeneratorService("cash-eq","CREATE");
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