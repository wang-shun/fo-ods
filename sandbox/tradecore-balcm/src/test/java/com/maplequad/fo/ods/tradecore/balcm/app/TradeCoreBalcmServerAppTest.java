package com.maplequad.fo.ods.tradecore.balcm.app;

import com.maplequad.fo.ods.tradecore.balcm.client.TradeCoreBulkAmendService;
import com.maplequad.fo.ods.tradecore.balcm.data.BulkAmendType;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.BulkAmend;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/***
 * TradeCoreBalcmServerAppTest -
 *
 * This class is used to test the GRPC Server App by using a wrapper class TradeCoreBalcmServerApp.
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TradeCoreBalcmServerAppTest {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreBalcmServerAppTest.class);
    private static long tradeId = 0;
    private final int tradeCount = 1;
    private TradeCoreBulkAmendService tradeCoreBulkAmendService;

    @Before
    public void setUp() throws Exception {

        String serviceHost = "localhost";
        //String serviceHost = "130.211.48.239";
        int servicePort = 3001;
        this.tradeCoreBulkAmendService = new TradeCoreBulkAmendService(serviceHost, servicePort);
    }

    @After
    public void tearDown() throws Exception {
        this.tradeCoreBulkAmendService = null;
    }

    @Test
    public void insertBulkAmend() throws Exception {
        BulkAmend insertBulkAmend = BulkAmend.newBuilder()
                .setAssetClass("ir-swap")
                .setType(BulkAmendOuterClass.BulkAmendDetails.newBuilder().
                        setCompression(BulkAmendOuterClass.Compression.newBuilder()
                        .setBookId("290123").setInstrumentId("PQRS.T").build()))
        .setCreatedBy("Madhav")
        .setAssetClass("IR_SWAP")
        .setDescription("First Trade Compression of IRS Trades")
        .setBulkAmendType(BulkAmendType.IRD_TRADE_COMPRESSION)
        .build();

        BulkAmendResponse response = tradeCoreBulkAmendService.initiateBulkAmend(insertBulkAmend);
        assertNotNull(response.getBulkAmendId());
        LOGGER.info("Created Bulk Amend Request with ID {}", response.getImpactedTradeIdsList());
    }

    @Test
    public void executeBulkAmend() throws Exception {
        // TestCase # 5 - Creating a bulk amend
        BulkAmend insertBulkAmend = BulkAmend.newBuilder()
                .setAssetClass("ir-swap")
                .setType(BulkAmendOuterClass.BulkAmendDetails.newBuilder().
                        setCompression(BulkAmendOuterClass.Compression.newBuilder()
                                .setBookId("290457").setInstrumentId("PQRS.T").build()))
                .setCreatedBy("Madhav")
                .setAssetClass("IR_SWAP")
                .setDescription("First Trade Compression of IRS Trades")
                .setBulkAmendType(BulkAmendType.IRD_TRADE_COMPRESSION)
                .build();

        BulkAmendResponse response = tradeCoreBulkAmendService.initiateBulkAmend(insertBulkAmend);
        assertNotNull(response.getBulkAmendId());
        LOGGER.info("Created Bulk Amend Request with ID {}", response.getBulkAmendId());
        assertNotNull(tradeCoreBulkAmendService.executeBulkAmend(response.getBulkAmendId()));
        LOGGER.info("Executed Execute Bulk Amend Request with ID {}", response.getBulkAmendId());
    }

    @Test
    public void cancelBulkAmend() throws Exception {
        // TestCase # 5 - Creating a bulk amend
        BulkAmend insertBulkAmend = BulkAmend.newBuilder()
                .setAssetClass("ir-swap")
                .setType(BulkAmendOuterClass.BulkAmendDetails.newBuilder().
                        setCompression(BulkAmendOuterClass.Compression.newBuilder()
                                .setBookId("290457").setInstrumentId("PQRS.T").build()))
                .setCreatedBy("Madhav")
                .setAssetClass("IR_SWAP")
                .setDescription("First Trade Compression of IRS Trades")
                .setBulkAmendType(BulkAmendType.IRD_TRADE_COMPRESSION)
                .build();

        BulkAmendResponse response = tradeCoreBulkAmendService.initiateBulkAmend(insertBulkAmend);
        assertNotNull(response.getBulkAmendId());
        LOGGER.info("Created Bulk Amend Request with ID {}", response.getBulkAmendId());
        assertNotNull(tradeCoreBulkAmendService.cancelBulkAmend(response.getBulkAmendId()));
        LOGGER.info("Executed Execute Bulk Amend Request with ID {}", response.getBulkAmendId());
    }
}