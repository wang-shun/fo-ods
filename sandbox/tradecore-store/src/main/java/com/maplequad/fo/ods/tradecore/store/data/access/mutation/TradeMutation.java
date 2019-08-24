package com.maplequad.fo.ods.tradecore.store.data.access.mutation;

import com.google.cloud.Date;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Mutation;
import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.exception.InvalidAssetClassException;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/***
 * EquityTradeMutation -
 *
 * This class creates all required mutations.
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class TradeMutation {

    protected static final String TRADE = "TRADE";
    protected static final String TRADE_EVENT = "TRADE_EVENT";
    protected static final String TRADE_PARTY = "TRADE_PARTY";
    protected static final String UPSERT_IN = "upsert entered for table {} with {}";
    protected static final String UPSERT_OUT = "upsert exited for table {}";
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeMutation.class);

    /**
     * This method generates the required mutations for Trade table
     *
     * @param trade
     * @param newTradeFlag
     * @return List of mutations
     */
    public static List<Mutation> upsert(Trade trade, boolean newTradeFlag) {
        LOGGER.info(UPSERT_IN, TRADE, trade);
        List<Mutation> tradeMutationList = new ArrayList<>();
        Timestamp createdTS = Timestamp.now();

        if (newTradeFlag) {
            Mutation.WriteBuilder builder = Mutation.newInsertBuilder(TRADE);
            String tradeId = String.valueOf(StringUtils.generateLong());
            trade.setTradeId(tradeId);
            builder.set(Trade.TRADE_ID).to(tradeId);
            trade.setCreatedTimeStamp(createdTS); // why do we need this?
            builder.set(Trade.CREATED_TS).to(createdTS);
            int[] date = StringUtils.getYMD(trade.getTradeDate());
            builder.set(Trade.TRADE_DATE).to(Date.fromYearMonthDay(date[0], date[1], date[2]));
            builder.set(Trade.ASSET_CLASS).to(trade.getAssetClass());
            builder.set(Trade.TRADE_TYPE).to(trade.getTradeType().name());
            builder.set(Trade.PRODUCT_TYPE).to(trade.getProductType());
            builder.set(Trade.ORIG_SYS).to(trade.getOrigSystem());
            builder.set(Trade.OS_TRADE_ID).to(trade.getOsTradeId());
            builder.set(Trade.CREATED_BY).to(trade.getCreatedBy());
            tradeMutationList.add(builder.build());
        }

        List<TradeEvent> eventList = trade.getTradeEventList();
        for (TradeEvent tradeEvent : eventList) {
            if (newTradeFlag) {
                tradeEvent.setTradeId(trade.getTradeId());
                //ValidTimeFrom must be received from the trade source
                //tradeEvent.setActiveFlag(true);
                //tradeEvent.setLockedFlag(false);
                tradeEvent.setValidTimeTo(Timestamp.MAX_VALUE);
                tradeEvent.setTransactionTimeFrom(createdTS);
                tradeEvent.setTransactionTimeTo(Timestamp.MAX_VALUE);
                tradeEvent.setCreatedBy(trade.getCreatedBy());
            }
            tradeMutationList.addAll(upsert(newTradeFlag, tradeEvent));
        }

        LOGGER.info(UPSERT_OUT, TRADE);
        return tradeMutationList;
    }

    public TradeMutation() {
    }

    /**
     * This method generates the required mutations for TradeEvent table
     *
     * @param newTradeFlag
     * @param tradeEvent
     * @return List of Mutations for TradeEvent Table
     */
    protected static List<Mutation> upsert(boolean newTradeFlag, TradeEvent tradeEvent) {
        LOGGER.info(UPSERT_IN, TRADE_EVENT, tradeEvent);
        List<Mutation> teMutationList = new ArrayList<>();
        Mutation.WriteBuilder builder = null;

        builder = Mutation.newInsertOrUpdateBuilder(TRADE_EVENT);

        //Primary Key
        builder.set(TradeEvent.TRADE_ID).to(tradeEvent.getTradeId());

        //Bi-Temporal
        builder.set(TradeEvent.VALID_TIME_FROM).to(tradeEvent.getValidTimeFrom());
        builder.set(TradeEvent.TXN_TIME_FROM).to(tradeEvent.getTransactionTimeFrom());
        builder.set(TradeEvent.VALID_TIME_TO).to(tradeEvent.getValidTimeTo());
        builder.set(TradeEvent.TXN_TIME_TO).to(tradeEvent.getTransactionTimeTo());

        //Attributes
        builder.set(TradeEvent.PARENT_TRADE_ID).to(tradeEvent.getParentTradeId());
        builder.set(TradeEvent.EVENT_TYPE).to(tradeEvent.getEventType());
        builder.set(TradeEvent.EVENT_STATUS).to(tradeEvent.getEventStatus());
        builder.set(TradeEvent.EVENT_REF).to(tradeEvent.getEventReference());
        builder.set(TradeEvent.EVENT_REM).to(tradeEvent.getEventRemarks());
        builder.set(TradeEvent.NO_OF_LEGS).to(tradeEvent.getNoOfLegs());
        builder.set(TradeEvent.OS_VERSION).to(tradeEvent.getOsVersion());
        builder.set(TradeEvent.OS_VERSION_STATUS).to(tradeEvent.getOsVersionStatus());
        builder.set(TradeEvent.ORDER_ID).to(tradeEvent.getOrderId());
        builder.set(TradeEvent.EXCH_EXEC_ID).to(tradeEvent.getExchangeExecutionId());
        builder.set(TradeEvent.TRADER_ID).to(tradeEvent.getTrader());
        builder.set(TradeEvent.SALESMAN_ID).to(tradeEvent.getSalesman());
        builder.set(TradeEvent.TRADER_CNTRY).to(tradeEvent.getTraderCountry());
        builder.set(TradeEvent.SALESMAN_CNTRY).to(tradeEvent.getSalesmanCountry());
        builder.set("activeFlag").to(false);
        //builder.set(TradeEvent.LOCKED_FLAG).to(tradeEvent.isLockedFlag());

        //Audit Trail
        builder.set(TradeEvent.CREATED_BY).to(tradeEvent.getCreatedBy());
        teMutationList.add(builder.build());

        //Collections
        List<ITradeLeg> tradeLegList = tradeEvent.getTradeLegList();
        for (ITradeLeg iTradeLeg : tradeLegList) {
            TradeLeg tradeLeg = (TradeLeg) iTradeLeg;
            tradeLeg.setTradeId(tradeEvent.getTradeId());//Required for New Trades
            tradeLeg.setValidTimeFrom(tradeEvent.getValidTimeFrom());
            tradeLeg.setValidTimeTo(tradeEvent.getValidTimeTo());
            tradeLeg.setTransactionTimeFrom(tradeEvent.getTransactionTimeFrom());
            tradeLeg.setTransactionTimeTo(tradeEvent.getTransactionTimeTo());
            switch (tradeLeg.getLegType()) {
                case (ProductType.CASH_EQUITY):
                    teMutationList.add(EquityTradeMutation.upsert(tradeLeg));
                    break;
                case (ProductType.IR_SWAP):
                    teMutationList.add(IrdTradeMutation.upsert(tradeLeg));
                    break;
                case (ProductType.FX_FORWARD):
                    teMutationList.add(FxdTradeMutation.upsert(tradeLeg));
                    break;
                default:
                    throw new InvalidAssetClassException(tradeLeg.getLegType());
            }
        }

        List<TradeParty> tradePartyList = tradeEvent.getTradePartyList();
        for (TradeParty tradeParty : tradePartyList) {
            tradeParty.setTradeId(tradeEvent.getTradeId());//Required for New Trades
            tradeParty.setValidTimeFrom(tradeEvent.getValidTimeFrom());
            tradeParty.setValidTimeTo(tradeEvent.getValidTimeTo());
            tradeParty.setTransactionTimeFrom(tradeEvent.getTransactionTimeFrom());
            tradeParty.setTransactionTimeTo(tradeEvent.getTransactionTimeTo());
            teMutationList.add(upsert(tradeParty));
        }
        LOGGER.info(UPSERT_OUT, TRADE_EVENT);
        return teMutationList;
    }

    /**
     * This method generates the required mutation for TradeParty table
     *
     * @param tradeParty
     * @return Mutation for TradeParty
     */
    protected static Mutation upsert(TradeParty tradeParty) {
        LOGGER.info(UPSERT_IN, TRADE_PARTY, tradeParty);
        Mutation.WriteBuilder builder = null;

        builder = Mutation.newInsertOrUpdateBuilder(TRADE_PARTY);

        //Primary Key
        builder.set(TradeParty.TRADE_ID).to(tradeParty.getTradeId());
        builder.set(TradeParty.PARTY_REF).to(tradeParty.getPartyRef());

        //Bi-Temporal
        builder.set(TradeParty.VALID_TIME_FROM).to(tradeParty.getValidTimeFrom());
        builder.set(TradeParty.TXN_TIME_FROM).to(tradeParty.getTransactionTimeFrom());
        builder.set(TradeParty.VALID_TIME_TO).to(tradeParty.getValidTimeTo());
        builder.set(TradeParty.TXN_TIME_TO).to(tradeParty.getTransactionTimeTo());

        //Attributes
        builder.set(TradeParty.PARTY_ROLE).to(tradeParty.getPartyRole());
        LOGGER.info(UPSERT_OUT, TRADE_PARTY);
        return builder.build();
    }
}