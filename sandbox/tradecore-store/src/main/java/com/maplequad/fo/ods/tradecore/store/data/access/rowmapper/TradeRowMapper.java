package com.maplequad.fo.ods.tradecore.store.data.access.rowmapper;

import com.google.cloud.spanner.ResultSet;
import com.maplequad.fo.ods.tradecore.data.model.trade.ProductType;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeParty;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass.TradeType;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;

/***
 * TradeRowMapper -
 *
 * This class is used to raed the complete trade from the resultset.
 *
 * @author Madhav Mindhe
 * @since :   13/08/2017
 */
public class TradeRowMapper {

    /**
     * This method is used to read Trade details from the provided resultSet.
     *
     * @param tradeRS
     * @return instance of Trade containing the data from the resultSet
     */
    public static Trade readTrade(ResultSet tradeRS) {
        return readTrade(tradeRS, "");
    }

    /**
     * This method is used to read TradeEvent details from the provided resultSet.
     *
     * @param tradeEventRS
     * @return instance of TradeEvent containing the data from the resultSet
     */
    public static TradeEvent readEvent(ResultSet tradeEventRS) {
        return readEvent(tradeEventRS, "");
    }

    /**
     * This method is used to read TradeParty details from the provided resultSet.
     *
     * @param tradePartyRS
     * @return instance of TradeParty containing the data from the resultSet
     */
    public static TradeParty readParty(ResultSet tradePartyRS) {
        return readParty(tradePartyRS, "");
    }

    /**
     * This method is used to read Trade details from the provided resultSet.
     *
     * @param tradeRS
     * @param prefix
     * @return instance of Trade containing the data from the resultSet
     */
    public static Trade readTrade(ResultSet tradeRS, String prefix) {
        Trade trade = new Trade();
        trade.setTradeId(tradeRS.getString(StringUtils.concat(prefix, Trade.TRADE_ID)));
        trade.setTradeDate(StringUtils.getDate(tradeRS.getDate(StringUtils.concat(prefix, Trade.TRADE_DATE))));
        trade.setAssetClass(tradeRS.getString(StringUtils.concat(prefix, Trade.ASSET_CLASS)));
        trade.setTradeType(TradeType.valueOf(tradeRS.getString(StringUtils.concat(prefix, Trade.TRADE_TYPE))));
        trade.setProductType(tradeRS.getString(StringUtils.concat(prefix, Trade.PRODUCT_TYPE)));
        trade.setOrigSystem(tradeRS.getString(StringUtils.concat(prefix, Trade.ORIG_SYS)));
        trade.setOsTradeId(tradeRS.getString(StringUtils.concat(prefix, Trade.OS_TRADE_ID)));
        trade.setCreatedTimeStamp(tradeRS.getTimestamp(StringUtils.concat(prefix, Trade.CREATED_TS)));
        return trade;
    }

    /**
     * This method is used to read TradeEvent details from the provided resultSet.
     *
     * @param tradeEventRS
     * @param prefix
     * @return instance of TradeEvent containing the data from the resultSet
     */
    public static TradeEvent readEvent(ResultSet tradeEventRS, String prefix) {
        TradeEvent tradeEvent = new TradeEvent();
        tradeEvent.setTradeId(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.TRADE_ID)));
        tradeEvent.setValidTimeFrom(tradeEventRS.getTimestamp(StringUtils.concat(prefix, TradeEvent.VALID_TIME_FROM)));
        tradeEvent.setValidTimeTo(tradeEventRS.getTimestamp(StringUtils.concat(prefix, TradeEvent.VALID_TIME_TO)));
        tradeEvent.setTransactionTimeFrom(tradeEventRS.getTimestamp(StringUtils.concat(prefix, TradeEvent.TXN_TIME_FROM)));
        tradeEvent.setTransactionTimeTo(tradeEventRS.getTimestamp(StringUtils.concat(prefix, TradeEvent.TXN_TIME_TO)));
        tradeEvent.setParentTradeId(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.PARENT_TRADE_ID)));
        tradeEvent.setEventType(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.EVENT_TYPE)));
        tradeEvent.setEventStatus(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.EVENT_STATUS)));
        tradeEvent.setEventReference(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.EVENT_REF)));
        tradeEvent.setEventRemarks(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.EVENT_REM)));
        tradeEvent.setNoOfLegs((int) tradeEventRS.getLong(StringUtils.concat(prefix, TradeEvent.NO_OF_LEGS)));
        tradeEvent.setOsVersion(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.OS_VERSION)));
        tradeEvent.setOsVersionStatus(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.OS_VERSION_STATUS)));
        tradeEvent.setOrderId(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.ORDER_ID)));
        tradeEvent.setExchangeExecutionId(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.EXCH_EXEC_ID)));
        tradeEvent.setTrader(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.TRADER_ID)));
        tradeEvent.setSalesman(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.SALESMAN_ID)));
        tradeEvent.setTraderCountry(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.TRADER_CNTRY)));
        tradeEvent.setSalesmanCountry(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.SALESMAN_CNTRY)));
        tradeEvent.setCreatedBy(tradeEventRS.getString(StringUtils.concat(prefix, TradeEvent.CREATED_BY)));
        return tradeEvent;
    }

    /**
     * This method is used to read TradeParty details from the provided resultSet.
     *
     * @param tradePartyRS
     * @param prefix
     * @return instance of TradeParty containing the data from the resultSet
     */
    public static TradeParty readParty(ResultSet tradePartyRS, String prefix) {
        TradeParty tradeParty = new TradeParty();
        tradeParty.setTradeId(tradePartyRS.getString(StringUtils.concat(prefix, TradeParty.TRADE_ID)));
        tradeParty.setValidTimeFrom(tradePartyRS.getTimestamp(StringUtils.concat(prefix, TradeParty.VALID_TIME_FROM)));
        tradeParty.setValidTimeTo(tradePartyRS.getTimestamp(StringUtils.concat(prefix, TradeParty.VALID_TIME_TO)));
        tradeParty.setTransactionTimeFrom(tradePartyRS.getTimestamp(StringUtils.concat(prefix, TradeParty.TXN_TIME_FROM)));
        tradeParty.setTransactionTimeTo(tradePartyRS.getTimestamp(StringUtils.concat(prefix, TradeParty.TXN_TIME_TO)));
        tradeParty.setPartyRef(tradePartyRS.getString(StringUtils.concat(prefix, TradeParty.PARTY_REF)));
        tradeParty.setPartyRole(tradePartyRS.getString(StringUtils.concat(prefix, TradeParty.PARTY_ROLE)));
        return tradeParty;
    }
}
