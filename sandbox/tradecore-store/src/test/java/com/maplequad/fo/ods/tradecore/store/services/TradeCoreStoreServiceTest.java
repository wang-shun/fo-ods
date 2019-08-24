package com.maplequad.fo.ods.tradecore.store.services;

import com.google.protobuf.util.JsonFormat;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.AssetClass;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.exception.InvalidAssetClassException;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.QueryByBookAndInstrumentId;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/***
 * TradeCoreStoreServiceTest -
 *
 * This class is used to test the GRPC Server App by using a wrapper class TradeCoreStoreService.
 *
 * @author Madhav Mindhe
 * @since :   15/08/2017
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TradeCoreStoreServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreStoreServiceTest.class);
    private static String tradeId = "";
    private static Trade testTrade;
    private static String assetClass;
    private final int tradeCount = 1;
    private TradeCoreStoreService tradeCoreStoreService;
    private TradeGenerator tradeGenerator;

    @Before
    public void setUp() throws Exception {

        //String serviceHost = "104.155.34.173";
        String serviceHost = "localhost";
        int servicePort = 3002;
        this.tradeCoreStoreService = new TradeCoreStoreService(serviceHost, servicePort);
        this.assetClass = AssetClass.IRD;
        switch (assetClass) {
            case AssetClass.EQT:
                this.tradeGenerator = TradeGenerator.getInstance("cash-eq");
                break;
            case AssetClass.IRD:
                this.tradeGenerator = TradeGenerator.getInstance("fi-irs");
                break;
            case AssetClass.FXD:
                this.tradeGenerator = TradeGenerator.getInstance("fx-fwd");
                break;
            default:
                throw new InvalidAssetClassException(assetClass);
        }

    }

    @After
    public void tearDown() throws Exception {
        this.tradeCoreStoreService = null;
    }

    @Test
    public void createTrade() throws Exception {
        // TestCase # 1 - Creating a New Trade
        long start = System.currentTimeMillis();
        for (int i = 0; i < tradeCount; i++) {
            Trade trade = tradeCoreStoreService.createTrade(tradeGenerator.createTrade(2, 3)).getTrade();
            testTrade = trade;
            tradeId = trade.getTradeId();
            assertNotEquals("", tradeId);
            assertEquals("1", trade.getTradeEventList().get(0).getOsVersion());
            LOGGER.info("Created Trade # : {} : {} = {}", i + 1, tradeId, trade);
        }
        long elapsed = System.currentTimeMillis() - start;
        double avg = elapsed / (double) tradeCount;
        LOGGER.info("{} trades took {} ms to save, avg={} ms/trade", tradeCount, elapsed, avg);
    }

    @Test
    public void getLatestTradeById() throws Exception {
//        createTrade();
        // TestCase # 2 - Reading a particular Trade with TradeID
        LOGGER.info("Getting latest version for tradeId {}", tradeId);
        Trade trade = tradeCoreStoreService.getLatestTrade("1755847496970160185").getTrade();
        LOGGER.info("Found trade {}", trade);
        assertEquals(tradeId, trade.getTradeId());
//        assertEquals(testTrade, trade);
    }

    @Test
    public void getLatestTradeByOsTradeId() throws Exception {
        assertTradeWithQuery(TradeQuery.newBuilder().setByOsTradeId(testTrade.getOsTradeId()).build());
    }

    @Test
    public void getLatestTradeByInstrumentBookIdTradeId() throws Exception {
        Trade trade = tradeCoreStoreService.getLatestTrades(TradeQuery.newBuilder().setByBookInstrumentId(
                QueryByBookAndInstrumentId.newBuilder().
                        setBookId("290457").
                        setInstrumentId("PQRS.T").
                        build()).build()).getTrade();
        System.out.println("Trade " + trade);
    }

    @Test
    @Ignore
    public void getLatestTradeByInstrumentId() throws Exception {
        assertTradeWithQuery(TradeQuery.newBuilder().setByOsTradeId(testTrade.getOsTradeId()).build());
    }

    @Test
    @Ignore
    public void getLatestTradeByInstrumentAndBookId() throws Exception {
        assertTradeWithQuery(TradeQuery.newBuilder().setByOsTradeId(testTrade.getOsTradeId()).build());
    }

    private void assertTradeWithQuery(TradeQuery query) {
        // TestCase # 2 - Reading a particular Trade with TradeID
        LOGGER.info("Getting latest version for odTradeId {}", testTrade.getOsTradeId());
        Trade trade = tradeCoreStoreService.getLatestTrades(query).getTrade();
        assertEquals(tradeId, trade.getTradeId());
        assertEquals(testTrade, trade);
    }

    @Test
    public void getLatestTrades() throws Exception {
        // TestCase # 3 - Reading all trades for the given criteria
        //Building the criteria to get all 2 leg trades for Equities Asset Class
        int asOnDaysBefore = 0;
        //Query query = new Query(assetClass);
        //query.and(Trade.ENTITY, Trade.OS_TRADE_ID, "4100216140480203200"); // IRS
        //query.and(Ird.ENTITY, Ird.BOOK, "290456");
        //query.and(Ird.ENTITY, Ird.MATURITY_DATE, "2017-10-07", Criteria.GT);
        TradeQuery query = TradeQuery.newBuilder().setByBookInstrumentId(
                QueryByBookAndInstrumentId.newBuilder().
                        setBookId("290457").
                        setInstrumentId("PQRS.T").
                        build()).
                build();
        TradeCoreStoreResponse res = tradeCoreStoreService.getLatestTrades(query);
        List<Trade> tradesList = res.getTrades();
        LOGGER.info("Found {} trades ", res.getTrades().size());
        LOGGER.info("Found first trade {}..", res.getTrade());
        assertNotEquals(0, tradesList.size());
        if (!tradesList.isEmpty()) {
            LOGGER.info("Found total {} trades..", tradesList.size());
            LOGGER.info("for matching the given criteria {}", query);
        } else {
            LOGGER.info("No trades are found matching the given criteria {}", query);
        }
    }

    @Test
    public void updateTrade() throws Exception {
        // TestCase # 4 - Updating a particular Trade
        testTrade.getTradeEventList().get(0).setOsVersion("2");
        Trade trade = tradeCoreStoreService.updateTrade(testTrade).getTrade();

        if (trade != null) {
            LOGGER.info("Updated trade {}", trade);
        } else {
            LOGGER.info("No trades are found matching the given tradeId");
        }
        assertEquals("2", trade.getTradeEventList().get(0).getOsVersion());
        // TestCase # 5 - Updating a particular Trade AGAIN!
        testTrade.getTradeEventList().get(0).setOsVersion("3");
        trade = tradeCoreStoreService.updateTrade(testTrade).getTrade();
        if (trade != null) {
            LOGGER.info("Updated trade {}", trade);
        } else {
            LOGGER.info("No trades are found matching the given tradeId");
        }
        assertEquals("3", trade.getTradeEventList().get(0).getOsVersion());
    }

    @Test
    public void insertBulkAmend() throws Exception {
        // TestCase # 5 - Creating a bulk amend
        BulkAmendOuterClass.BulkAmend amend = BulkAmendOuterClass.BulkAmend.newBuilder()
                .setCreatedBy("Madhav")
                .setAssetClass("ir-swap")
                .setDescription("First Trade Compression of IRS Trades")
                .setBulkAmendType("IRS_TC")
                .setType(BulkAmendOuterClass.BulkAmendDetails.newBuilder().
                        setSplit(BulkAmendOuterClass.Split.newBuilder().
                                setInstrumentId("ABC.D").setRatio(2.0).build()).
                        build()).
                build();

        LOGGER.info("Bulk Amend request {}",  JsonFormat.printer().print(amend));

        String id = tradeCoreStoreService.createBulkAmend(amend);

        BulkAmendOuterClass.BulkAmend amend2 = tradeCoreStoreService.getBulkAmend(id);

        LOGGER.info("Loaded Amend request {}",  JsonFormat.printer().print(amend2));

        assertEquals(amend.getType().getTypeCase(), amend2.getType().getTypeCase());
        assertEquals(amend.getType().getSplit().getRatio(), amend2.getType().getSplit().getRatio(), 0.01);
        assertEquals(amend.getType().getSplit().getRatio(), 2.0, 0.01);
    }
}