package com.maplequad.fo.ods.tradecore.lcm.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Madhav Mindhe
 * @since :   23/07/2017
 */
public class TradePersistenceServiceTest {

    private TradeGeneratorService genService;
    private TradeConverterService conService;
    private TradePersistenceService perService;

    @Before
    public void setUp() throws Exception {
        genService = new TradeGeneratorService("cash-eq","CREATE");
        conService = new TradeConverterService("cash-eq");
        perService = new TradePersistenceService( 10);
    }

    @After
    public void tearDown() throws Exception {
        genService = null;
        conService = null;
        perService = null;
    }

    @Test
    public void persist() throws Exception {
        int tCount = 35;
        perService.serve(conService.serve(genService.serve(tCount)));
        //TODO : Verify from database
        assertEquals(1, 1);
    }

}