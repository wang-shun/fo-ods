package com.maplequad.fo.ods.tradecore.store.data.access.service;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionRunner;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeParty;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.store.data.access.mutation.BulkAmendMutation;
import com.maplequad.fo.ods.tradecore.store.data.access.mutation.TradeMutation;
import com.maplequad.fo.ods.tradecore.store.data.access.rowmapper.BulkAmendMapper;
import com.maplequad.fo.ods.tradecore.store.data.access.rowmapper.TradeLegMapper;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.maplequad.fo.ods.tradecore.store.data.access.query.QueryBuilder.*;
import static com.maplequad.fo.ods.tradecore.store.data.access.rowmapper.TradeRowMapper.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


/***
 * TradeDataAccessService -
 *
 * This class implements the interface to give the handle to database access operations.
 *
 * @author Madhav Mindhe
 * @since :   12/08/2017
 */
public class TradeDataAccessService implements DataAccessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessService.class);

    // dbClient is thread safe and should be reused by all threads
    private static final DatabaseClient dbClient = SpannerDatabaseService.getInstance().getDBClient();

    /**
     * This method is used to generate a new trade using the details
     *
     * @param trade
     * @return the details of the new trade
     */
    @Override
    public Trade insert(Trade trade) {
        LOGGER.info("Inserting trade {}", trade);
        dbClient.readWriteTransaction().run
                (new TransactionRunner.TransactionCallable<Void>() {
                    @Nullable
                    @Override
                    public Void run(TransactionContext txContext) throws Exception {
                        LOGGER.info("Inside transaction");
                        updateTransactionTime(trade, Timestamp.now(), Timestamp.MAX_VALUE);
                        txContext.buffer(TradeMutation.upsert(trade, true));
                        return null;
                    }
                });
        LOGGER.info("Generated new Trade with TradeId {}", trade.getTradeId());
        return trade;
    }

    /**
     * This method is used to get the latest version of trade by tradeId for given assetClass
     *
     * @param tradeId
     * @return latest version of trade for the given tradeId
     */
    @Override
    public Trade readLatest(String tradeId) {
        return this.readLatest(tradeId, Timestamp.now());
    }


    /**
     * This method is used to get the latest version of trade by tradeId as Of given Timestamp
     *
     * @param tradeId
     * @param vtf
     * @return latest version of trade for the given tradeId as of given timestamp
     */
    @Override
    public Trade readLatest(String tradeId, Timestamp vtf) {
        return readAsOf(tradeId, vtf);
    }

    /**
     * This method is used to get the latest version of trade by tradeId as Of given Timestamp
     *
     * @param tradeId
     * @param validFromTime
     * @return latest version of trade for the given tradeId as of given timestamp
     */
    private Trade readAsOf(String tradeId, Timestamp validFromTime) {
        List<Trade> trades = readTrades(tradeIdSubQuery(tradeId), validFromTime);
        if ( trades.size() != 1 ) {
            throw new RuntimeException("Found " + trades.size() + "for " + trades.size());
        }
        return trades.get(0);
    }

    @Override
    public List<Trade> readTrades(String subQuery, Timestamp asOf) {
        LOGGER.info("readAsOf entered");
        Timestamp ttf = Timestamp.now();
        try (ReadOnlyTransaction transaction = dbClient.readOnlyTransaction()) {
            Map<String,Trade> trades = readTradesAndEventAsOf(transaction, subQuery, asOf, ttf);
            Map<String,List<TradeParty>> partiesByTrade = readTradePartiesByTrade(transaction, subQuery, asOf, ttf);
            Map<String,List<ITradeLeg>> legsByTrade = readTradeLegsByTrade(transaction, subQuery, asOf, ttf);
            trades.forEach((tradeId, trade) -> {
                TradeEvent event = trade.getTradeEventList().get(0);
                event.setTradePartyList(partiesByTrade.getOrDefault(tradeId, emptyList()));
                event.setTradeLegList(legsByTrade.getOrDefault(tradeId, emptyList()));
            });
            LOGGER.info("readAsOf exited");
            return new ArrayList<>(trades.values());
        }
    }


    private Map<String,Trade> readTradesAndEventAsOf(ReadOnlyTransaction transaction, String subQuery, Timestamp validFrom, Timestamp ttf) {
        try ( ResultSet resultSet = transaction.executeQuery(queryTradesAndEventAsOf(subQuery, validFrom, ttf)) ) {
            Map<String,Trade> trades = new HashMap<>();
            while ( resultSet.next() ) {
                Trade trade = readTrade(resultSet, Trade.ENTITY);
                TradeEvent event = readEvent(resultSet, TradeEvent.ENTITY);
                trade.setTradeEventList(singletonList(event));
                trades.put(trade.getTradeId(), trade);
            }
            return trades;
        }
    }

    private Map<String,List<TradeParty>> readTradePartiesByTrade(ReadOnlyTransaction transaction, String subQuery, Timestamp validFrom, Timestamp ttf) {
        try ( ResultSet resultSet = transaction.executeQuery(queryTradePartiesAsOf(subQuery, validFrom, ttf)) ) {
            Map<String,List<TradeParty>> parties = new HashMap<>();
            while ( resultSet.next() ) {
                TradeParty tradeParty = readParty(resultSet);
                parties.computeIfAbsent(tradeParty.getTradeId(), x-> new LinkedList<>()).add( tradeParty );
            }
            return parties;
        }
    }

    private Map<String,List<ITradeLeg>> readTradeLegsByTrade(ReadOnlyTransaction transaction, String subQuery, Timestamp validFrom, Timestamp ttf) {
        Map<String,List<ITradeLeg>> legs = new HashMap<>();
        for (TradeLegMapper mapper : TradeLegMapper.ALL_LEG_MAPPERS ) {
            try ( ResultSet resultSet = transaction.executeQuery(queryTradeLegsAsOf(mapper.databaseEntity(), subQuery, validFrom, ttf)) ) {
                while ( resultSet.next() ) {
                    TradeLeg tradeLeg = (TradeLeg) mapper.readLeg(resultSet);
                    legs.computeIfAbsent(tradeLeg.getTradeId(), x-> new LinkedList<>()).add( tradeLeg );
                }
            }
        }
        return legs;
    }


    /**
     * * This method will -
     * 1. read the current (as on given timestamp) version of the trade
     * 2. check if the validTimeTo is infinity and if NOT then use that to store validTimeTo as trade being updated
     * 3. update the ValidTimeTo and TrasasctionTimeTo(??) given ValidTimeFrom and currentTimeStamp
     * in the previous version of the trade
     * 3. and then insert as on version of the trade with given validTimeFrom
     * and validTimeTo as the validTimeTo of the previous version of the trade (refer #2). TransactionTimeFrom and To should be
     * currentTimeStamp.
     * <p>
     * This method will -
     * 1. read the latest version of the trade
     * 2. and update the ValidTimeTo and TrasasctionTimeTo to
     * given ValidTimeFrom and currentTimeStamp in the currently latest version of the trade
     * 3. and then insert new version of the trade as std
     *
     * @param updatedTrade
     * @return updatedTrade
     */
    @Override
    public Trade update(Trade updatedTrade) {
        dbClient.readWriteTransaction().run
                (new TransactionRunner.TransactionCallable<Void>() {
                    @Nullable
                    @Override
                    public Void run(TransactionContext txContext) throws Exception {
                        List<Trade> tradesList = updateTrade(updatedTrade);
                        txContext.buffer(TradeMutation.upsert(tradesList.get(0), false));
                        txContext.buffer(TradeMutation.upsert(tradesList.get(1), false));
                        return null;
                    }
                });
        LOGGER.info("Updated Trade with TradeId {}", updatedTrade.getTradeId());
        return updatedTrade;
    }


    /**
     * @param updatedTrade
     * @return
     */
    private List<Trade> updateTrade(Trade updatedTrade) {
        //Getting the new ValidTimeFrom to be used as currValidTimeTo
        TradeEvent tradeEvent = updatedTrade.getTradeEventList().get(0);
        //These three statements are true in all cases
        Timestamp newValidTimeFrom = tradeEvent.getValidTimeFrom();
        Timestamp currValidTimeTo = newValidTimeFrom;
        Timestamp currTxTimeTo = Timestamp.now();
        //These three are true in std updates
        Timestamp newTxTimeFrom = currTxTimeTo;
        Timestamp newTxTimeTo = Timestamp.MAX_VALUE;
        Timestamp newValidTimeTo = Timestamp.MAX_VALUE;
        //Reading the current version
        //Passing currValidTimeTo is required if trade is being amended as on past date time...
        //Trade currTrade = readLatest(updatedTrade.getTradeId(), currValidTimeTo);
        //Removed currValidTimeTo to prevent from having two active versions
        Trade currTrade = readLatest(updatedTrade.getTradeId());
        //Get the ValidTimeTo for the current version
        Timestamp currValidTimeToCheck = currTrade.getTradeEventList().get(0).getValidTimeTo();
        //Checking if it is NOT Infinity
        if (!Timestamp.MAX_VALUE.equals(currValidTimeToCheck)) {
            //Set ValidTimeTo as ValidTimeTo of the Previous Version
            newValidTimeTo = currValidTimeToCheck;
            newTxTimeTo = currTxTimeTo;
        }
        //Setting TimeTo Fields in currentTrade
        Trade currTradeWithTT = updateCurrentVersion(currTrade, currValidTimeTo, currTxTimeTo);
        //set TrasactionTimes in updatedTrade
        Trade updatedTradeWithTT = updateNewVersion(updatedTrade, newTxTimeFrom, newTxTimeTo, newValidTimeTo);
        List tradeList = new ArrayList();
        tradeList.add(currTradeWithTT);
        tradeList.add(updatedTradeWithTT);
        return tradeList;
    }

    /**
     * This method is used to set the given validTimeTo and transactionTimeTo in the TradeEvent
     *
     * @param trade
     * @param currValidTimeTo
     * @param currTxTimeTo
     * @return updated Trade with provided validTimeTo and transactionTimeTo
     */
    private Trade updateCurrentVersion(Trade trade, Timestamp currValidTimeTo, Timestamp currTxTimeTo) {
        List<TradeEvent> tradeEventsList = trade.getTradeEventList();
        if (!tradeEventsList.isEmpty()) {
            tradeEventsList.get(0).setValidTimeTo(currValidTimeTo);
            tradeEventsList.get(0).setTransactionTimeTo(currTxTimeTo);
            //tradeEventsList.get(0).setActiveFlag(false);
        }
        return trade;
    }

    /**
     * This method is used to set the given trasactionTimeFrom and transactionTimeTo in the tradeEvent
     *
     * @param trade
     * @param txTimeFrom
     * @param txTimeTo
     * @return updated Trade with given trasactionTimeFrom and transactionTimeTo
     */
    private Trade updateTransactionTime(Trade trade, Timestamp txTimeFrom, Timestamp txTimeTo) {
        List<TradeEvent> uTradeEventsList = trade.getTradeEventList();
        if (!uTradeEventsList.isEmpty()) {
            uTradeEventsList.get(0).setTransactionTimeFrom(txTimeFrom);
            uTradeEventsList.get(0).setTransactionTimeTo(txTimeTo);
        }
        return trade;
    }


    /**
     * This method is used to set the given trasactionTimeFrom, transactionTimeTo
     * and the validTimeTo in the tradeEvent
     *
     * @param trade
     * @param txTimeFrom
     * @param txTimeTo
     * @param validTimeTo
     * @return updated Trade with given trasactionTimeFrom, transactionTimeTo and validTimeTo
     */
    private Trade updateNewVersion(Trade trade, Timestamp txTimeFrom, Timestamp txTimeTo, Timestamp validTimeTo) {
        List<TradeEvent> uTradeEventsList = trade.getTradeEventList();
        if (!uTradeEventsList.isEmpty()) {
            uTradeEventsList.get(0).setTransactionTimeFrom(txTimeFrom);
            uTradeEventsList.get(0).setTransactionTimeTo(txTimeTo);
            uTradeEventsList.get(0).setValidTimeTo(validTimeTo);
            //uTradeEventsList.get(0).setActiveFlag(true);
        }
        return trade;
    }

    @Override
    public String createBulkAmend(BulkAmendOuterClass.BulkAmend request) {
        LOGGER.info("Creating bulkAmend {}", request);
        String id = String.valueOf(StringUtils.generateLong());
        dbClient.readWriteTransaction().run(txContext-> {
                        txContext.buffer(BulkAmendMutation.insert(request, id));
                        return null;
                    });
        LOGGER.info("Stored new BulkAmend with Id {}", id);
        return id;
    }

    @Override
    public BulkAmendOuterClass.BulkAmend getBulkAmend(String bulkAmendId) {
        LOGGER.info("Loading bulkAmend {} ", bulkAmendId);
        try (ReadOnlyTransaction transaction = dbClient.readOnlyTransaction()) {
            ResultSet resultSet =
                    transaction.
                        executeQuery(
                            Statement.newBuilder("SELECT * FROM BULK_AMEND WHERE bulkAmendId = @bulkAmendId").
                               bind("bulkAmendId").to(bulkAmendId).build());
            if ( ! resultSet.next() ) {
                throw new IllegalArgumentException("Bulk amend not found " + bulkAmendId);
            }
            return BulkAmendMapper.readBulkAmend(resultSet);
        }
    }

    @Override
    public void executeBulkAmend(BulkAmendResponse bulkAmend) {
        LOGGER.info("Executing bulkAmend {}", bulkAmend);
        dbClient.readWriteTransaction().run
                (new TransactionRunner.TransactionCallable<Void>() {
                    @Nullable
                    @Override
                    public Void run(TransactionContext txContext) throws Exception {
                        //Insert in to BulkAmend
                        txContext.buffer(BulkAmendMutation.update(bulkAmend.getBulkAmendId(), "DONE"));
                        //Insert into BulkAmendTask
                        bulkAmend.getImpactedTradesList().stream().
                                map(TradeTransformer::fromProto).
                                forEach(t->txContext.buffer(TradeMutation.upsert(t, false)));
                        return null;
                    }
                });
        LOGGER.info("Executed BulkAmend with Id {}", bulkAmend.getBulkAmendId());
    }

    @Override
    public void cancelBulkAmend(String bulkAmendId) {
        LOGGER.info("Cancelling bulkAmend {}", bulkAmendId);
        dbClient.readWriteTransaction().run(txContext-> {
            txContext.buffer(BulkAmendMutation.update(bulkAmendId, "CANCELLED"));
            return null;
        });
    }
}
