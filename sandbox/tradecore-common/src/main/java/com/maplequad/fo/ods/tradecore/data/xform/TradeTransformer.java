package com.maplequad.fo.ods.tradecore.data.xform;

import com.google.cloud.Timestamp;
import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.exception.InvalidAssetClassException;
import com.maplequad.fo.ods.tradecore.proto.model.TradeEventOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.TradePartyOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.eq.EquityOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.fi.IrdOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.fx.FxdOuterClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * TradeTransformer -
 *
 * This class is used to convert the trade from proto to std and vice versa.
 *
 * @author Madhav Mindhe
 * @since :   12/08/2017
 */
public class TradeTransformer {

    //Added Private Constructor to hide the implicit public one
    private TradeTransformer() {

    }

    /**
     * This method is used to generate a trade from its Proto form.
     *
     * @param pTrade
     * @return std instance of Trade with all details from its Proto form
     */
    public static Trade fromProto(TradeOuterClass.Trade pTrade) {

        Trade trade = new Trade();

        //Primary Key
        trade.setTradeId(pTrade.getTradeId());

        //Attributes
        trade.setTradeDate(new Date(pTrade.getTradeDate()));
        trade.setAssetClass(pTrade.getAssetClass());
        trade.setProductType(pTrade.getProductType());
        trade.setTradeType(pTrade.getTradeType());
        trade.setOrigSystem(pTrade.getOrigSystem());
        trade.setOsTradeId(pTrade.getOsTradeId());

        //Collection
        List<TradeEvent> tradeEventList = new ArrayList();
        for (TradeEventOuterClass.TradeEvent iTradeEvent : pTrade.getTradeEventsList()) {
            tradeEventList.add(fromProto(iTradeEvent));
        }
        trade.setTradeEventList(tradeEventList);

        //Audit Trail
        trade.setCreatedBy(pTrade.getCreatedBy());
        trade.setCreatedTimeStamp(Timestamp.fromProto(pTrade.getCreatedTimeStamp()));

        return trade;
    }

    /**
     * This method is used to generate a list of Proto instances from the list of std trades
     *
     * @param tradeList
     * @return List of Proto instances of Trade
     */
    public static List<TradeOuterClass.Trade> toProto(List<Trade> tradeList) {
        List<TradeOuterClass.Trade> pTradesList = new ArrayList<>();
        for (Trade trade : tradeList) {
            pTradesList.add(toProto(trade));
        }
        return pTradesList;
    }

    /**
     * This method is used to generate Proto instance of the trade
     *
     * @param trade
     * @return Proto instance of the trade
     */
    public static TradeOuterClass.Trade toProto(Trade trade) {

        TradeEventOuterClass.TradeEvent pTradeEvent = null;
        List<EquityOuterClass.Equity> pEquityTradeLegList = new ArrayList<>();
        List<IrdOuterClass.Ird> pIrdTradeLegList = new ArrayList<>();
        List<FxdOuterClass.Fxd> pFxdTradeLegList = new ArrayList<>();
        List<TradePartyOuterClass.TradeParty> pTradePartyList = new ArrayList<>();

        if (!trade.getTradeEventList().isEmpty()) {
            TradeEvent tradeEvent = trade.getTradeEventList().get(0);
            if (tradeEvent != null) {
                for (TradeParty tradeParty : tradeEvent.getTradePartyList()) {

                    TradePartyOuterClass.TradeParty.Builder pTradePartyBuilder =
                            TradePartyOuterClass.TradeParty.newBuilder();

                    if (!Strings.isNullOrEmpty(tradeParty.getTradeId())) {
                        pTradePartyBuilder.setTradeId(tradeParty.getTradeId());
                    }
                    if (tradeParty.getValidTimeFrom() != null &&
                            !Timestamp.MIN_VALUE.equals(tradeParty.getValidTimeFrom())) {
                        pTradePartyBuilder.setValidTimeFrom(tradeParty.getValidTimeFrom().toProto());
                    }
                    if (tradeParty.getValidTimeTo() != null &&
                            !Timestamp.MIN_VALUE.equals(tradeParty.getValidTimeTo())) {
                        pTradePartyBuilder.setValidTimeTo(tradeParty.getValidTimeTo().toProto());
                    }
                    if (tradeParty.getTransactionTimeFrom() != null &&
                            !Timestamp.MIN_VALUE.equals(tradeParty.getTransactionTimeFrom())) {
                        pTradePartyBuilder.setTransactionTimeFrom(tradeParty.getTransactionTimeFrom().toProto());
                    }
                    if (tradeParty.getTransactionTimeTo() != null &&
                            !Timestamp.MIN_VALUE.equals(tradeParty.getTransactionTimeTo())) {
                        pTradePartyBuilder.setTransactionTimeTo(tradeParty.getTransactionTimeTo().toProto());
                    }
                    if (!Strings.isNullOrEmpty(tradeParty.getPartyRef())) {
                        pTradePartyBuilder.setPartyRef(tradeParty.getPartyRef());
                    }
                    if (!Strings.isNullOrEmpty(tradeParty.getPartyRole())) {
                        pTradePartyBuilder.setPartyRole(tradeParty.getPartyRole());
                    }
                    pTradePartyList.add(pTradePartyBuilder.build());
                }

                for (ITradeLeg iTradeLeg : tradeEvent.getTradeLegList()) {
                    switch (((TradeLeg)iTradeLeg).getLegType()) {
                        case ProductType.CASH_EQUITY:
                            pEquityTradeLegList.add(EquityTradeTransformer.toProto(iTradeLeg));
                            break;
                        case ProductType.IR_SWAP:
                            pIrdTradeLegList.add(IrdTradeTransformer.toProto(iTradeLeg));
                            break;
                        case ProductType.FX_FORWARD:
                            pFxdTradeLegList.add(FxdTradeTransformer.toProto(iTradeLeg));
                            break;
                        default:
                            throw new InvalidAssetClassException(trade.getAssetClass());
                    }
                }

                TradeEventOuterClass.TradeEvent.Builder pTradeEventBuilder = TradeEventOuterClass.TradeEvent.newBuilder();
                if (!Strings.isNullOrEmpty(tradeEvent.getTradeId())) {
                    pTradeEventBuilder.setTradeId(tradeEvent.getTradeId());
                }
                if (tradeEvent.getValidTimeFrom() != null &&
                        !Timestamp.MIN_VALUE.equals(tradeEvent.getValidTimeFrom())) {
                    pTradeEventBuilder.setValidTimeFrom(tradeEvent.getValidTimeFrom().toProto());
                }
                if (tradeEvent.getValidTimeTo() != null &&
                        !Timestamp.MIN_VALUE.equals(tradeEvent.getValidTimeTo())) {
                    pTradeEventBuilder.setValidTimeTo(tradeEvent.getValidTimeTo().toProto());
                }
                if (tradeEvent.getTransactionTimeFrom() != null &&
                        !Timestamp.MIN_VALUE.equals(tradeEvent.getTransactionTimeFrom())) {
                    pTradeEventBuilder.setTransactionTimeFrom(tradeEvent.getTransactionTimeFrom().toProto());
                }
                if (tradeEvent.getTransactionTimeTo() != null &&
                        !Timestamp.MIN_VALUE.equals(tradeEvent.getTransactionTimeTo())) {
                    pTradeEventBuilder.setTransactionTimeTo(tradeEvent.getTransactionTimeTo().toProto());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getParentTradeId())) {
                    pTradeEventBuilder.setParentTradeId(tradeEvent.getParentTradeId());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getEventType())) {
                    pTradeEventBuilder.setEventType(tradeEvent.getEventType());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getEventStatus())) {
                    pTradeEventBuilder.setEventStatus(tradeEvent.getEventStatus());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getEventReference())) {
                    pTradeEventBuilder.setEventReference(tradeEvent.getEventReference());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getEventRemarks())) {
                    pTradeEventBuilder.setEventRemarks(tradeEvent.getEventRemarks());
                }
                if (tradeEvent.getNoOfLegs() != 0) {
                    pTradeEventBuilder.setNoOfLegs(tradeEvent.getNoOfLegs());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getOsVersion())) {
                    pTradeEventBuilder.setOsVersion(tradeEvent.getOsVersion());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getOsVersionStatus())) {
                    pTradeEventBuilder.setOsVersionStatus(tradeEvent.getOsVersionStatus());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getOrderId())) {
                    pTradeEventBuilder.setOrderId(tradeEvent.getOrderId());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getExchangeExecutionId())) {
                    pTradeEventBuilder.setExchangeExecutionId(tradeEvent.getExchangeExecutionId());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getTrader())) {
                    pTradeEventBuilder.setTrader(tradeEvent.getTrader());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getTraderCountry())) {
                    pTradeEventBuilder.setTraderCountry(tradeEvent.getTraderCountry());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getSalesman())) {
                    pTradeEventBuilder.setSalesman(tradeEvent.getSalesman());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getSalesmanCountry())) {
                    pTradeEventBuilder.setSalesmanCountry(tradeEvent.getSalesmanCountry());
                }
                if (!Strings.isNullOrEmpty(tradeEvent.getCreatedBy())) {
                    pTradeEventBuilder.setCreatedBy(tradeEvent.getCreatedBy());
                }

                //Faster Bi-Temporal Reads
                //pTradeEventBuilder.setActiveFlag(tradeEvent.isActiveFlag());
                //Used for Amends with no pending BulkAmends
                //pTradeEventBuilder.setLockedFlag(tradeEvent.isLockedFlag());

                if (!pEquityTradeLegList.isEmpty()) {
                    pTradeEventBuilder.addAllEquityLegs(pEquityTradeLegList);
                }
                if (!pIrdTradeLegList.isEmpty()) {
                    pTradeEventBuilder.addAllIrdLegs(pIrdTradeLegList);
                }
                if (!pFxdTradeLegList.isEmpty()) {
                    pTradeEventBuilder.addAllFxdLegs(pFxdTradeLegList);
                }
                if (!pTradePartyList.isEmpty()) {
                    pTradeEventBuilder.addAllTradeParties(pTradePartyList);
                }
                pTradeEvent = pTradeEventBuilder.build();
            }
        }
        TradeOuterClass.Trade.Builder pTradeBuilder = TradeOuterClass.Trade.newBuilder();
        if (!Strings.isNullOrEmpty(trade.getTradeId())) {
            pTradeBuilder.setTradeId(trade.getTradeId());
        }
        if (trade.getCreatedTimeStamp() != null &&
                !Timestamp.MIN_VALUE.equals(trade.getCreatedTimeStamp())) {
            pTradeBuilder.setCreatedTimeStamp(trade.getCreatedTimeStamp().toProto());
        }
        if (!Strings.isNullOrEmpty(trade.getCreatedBy())) {
            pTradeBuilder.setCreatedBy(trade.getCreatedBy());
        }
        if (!Strings.isNullOrEmpty(trade.getOrigSystem())) {
            pTradeBuilder.setOrigSystem(trade.getOrigSystem());
        }
        if (!Strings.isNullOrEmpty(trade.getOsTradeId())) {
            pTradeBuilder.setOsTradeId(trade.getOsTradeId());
        }
        if (!Strings.isNullOrEmpty(trade.getAssetClass())) {
            pTradeBuilder.setAssetClass(trade.getAssetClass());
        }
        if (!Strings.isNullOrEmpty(trade.getProductType())) {
            pTradeBuilder.setProductType(trade.getProductType());
        }
        if (trade.getTradeType() != null) {
            pTradeBuilder.setTradeType(trade.getTradeType());
        }
        if (trade.getTradeDate() != null) {
            pTradeBuilder.setTradeDate(trade.getTradeDate().getTime());
        }
        if (pTradeEvent != null) {
            pTradeBuilder.addTradeEvents(pTradeEvent);
        }
        return pTradeBuilder.build();
    }


    /**
     * This method is used to create an instance of TradeEvent from its Proto Form
     *
     * @param iTradeEvent
     * @return instance of TradeEvent with all details from its Proto form.
     */
    private static TradeEvent fromProto(TradeEventOuterClass.TradeEvent iTradeEvent) {

        TradeEvent tradeEvent = new TradeEvent();

        //Primary Key
        tradeEvent.setTradeId(iTradeEvent.getTradeId());

        tradeEvent.setValidTimeFrom(Timestamp.fromProto(iTradeEvent.getValidTimeFrom()));
        tradeEvent.setValidTimeTo(Timestamp.fromProto(iTradeEvent.getValidTimeTo()));
        tradeEvent.setTransactionTimeFrom(Timestamp.fromProto(iTradeEvent.getTransactionTimeFrom()));
        tradeEvent.setTransactionTimeTo(Timestamp.fromProto(iTradeEvent.getTransactionTimeTo()));

        //Attributes
        tradeEvent.setParentTradeId(iTradeEvent.getParentTradeId());
        tradeEvent.setEventType(iTradeEvent.getEventType());
        tradeEvent.setEventStatus(iTradeEvent.getEventStatus());
        tradeEvent.setEventReference(iTradeEvent.getEventReference());
        tradeEvent.setEventRemarks(iTradeEvent.getEventRemarks());
        tradeEvent.setNoOfLegs((int) iTradeEvent.getNoOfLegs());
        tradeEvent.setOsVersion(iTradeEvent.getOsVersion());
        tradeEvent.setOsVersionStatus(iTradeEvent.getOsVersionStatus());
        tradeEvent.setOrderId(iTradeEvent.getOrderId());
        tradeEvent.setExchangeExecutionId(iTradeEvent.getExchangeExecutionId());
        tradeEvent.setTrader(iTradeEvent.getTrader());
        tradeEvent.setSalesman(iTradeEvent.getSalesman());
        tradeEvent.setTraderCountry(iTradeEvent.getTraderCountry());
        tradeEvent.setSalesmanCountry(iTradeEvent.getSalesmanCountry());

        //Collections
        List<ITradeLeg> tradeLegList = new ArrayList();
        for (EquityOuterClass.Equity iTradeLeg : iTradeEvent.getEquityLegsList()) {
            tradeLegList.add(EquityTradeTransformer.fromProto(iTradeLeg));
        }
        for (IrdOuterClass.Ird iTradeLeg : iTradeEvent.getIrdLegsList()) {
            tradeLegList.add(IrdTradeTransformer.fromProto(iTradeLeg));
        }
        for (FxdOuterClass.Fxd iTradeLeg : iTradeEvent.getFxdLegsList()) {
            tradeLegList.add(FxdTradeTransformer.fromProto(iTradeLeg));
        }
        tradeEvent.setTradeLegList(tradeLegList);

        List<TradeParty> tradePartyList = new ArrayList<TradeParty>();
        for (TradePartyOuterClass.TradeParty iTradeParty : iTradeEvent.getTradePartiesList()) {
            tradePartyList.add(fromProto(iTradeParty));
        }
        tradeEvent.setTradePartyList(tradePartyList);

        //Audit Trail
        tradeEvent.setCreatedBy(iTradeEvent.getCreatedBy());

        //Faster Bi-Temporal Reads
        //tradeEvent.setActiveFlag(iTradeEvent.getActiveFlag());
        //Used for Amends with no pending BulkAmends
        //tradeEvent.setLockedFlag(iTradeEvent.getLockedFlag());

        return tradeEvent;
    }

    /**
     * This method is used to create an instance of TradeParty from its Proto Form
     *
     * @param iTradeParty
     * @return instance of TradeParty with all details from its Proto form.
     */
    private static TradeParty fromProto(TradePartyOuterClass.TradeParty iTradeParty) {

        TradeParty tradeParty = new TradeParty();

        //Primary Key
        tradeParty.setTradeId(iTradeParty.getTradeId());
        tradeParty.setPartyRef(iTradeParty.getPartyRef());

        tradeParty.setValidTimeFrom(Timestamp.fromProto(iTradeParty.getValidTimeFrom()));
        tradeParty.setValidTimeTo(Timestamp.fromProto(iTradeParty.getValidTimeTo()));
        tradeParty.setTransactionTimeFrom(Timestamp.fromProto(iTradeParty.getTransactionTimeFrom()));
        tradeParty.setTransactionTimeTo(Timestamp.fromProto(iTradeParty.getTransactionTimeTo()));

        //Attributes
        tradeParty.setPartyRole(iTradeParty.getPartyRole());
        return tradeParty;
    }

}
