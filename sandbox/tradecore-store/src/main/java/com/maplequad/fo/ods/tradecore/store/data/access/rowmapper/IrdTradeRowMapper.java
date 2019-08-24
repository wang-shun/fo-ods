package com.maplequad.fo.ods.tradecore.store.data.access.rowmapper;

import com.google.cloud.spanner.ResultSet;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;

/***
 * IRDTradeRowMapper -
 *
 * This class is used to raed the complete trade from the resultset.
 *
 * @author Madhav Mindhe
 * @since :   13/08/2017
 */
public enum IrdTradeRowMapper implements TradeLegMapper {
    INSTANCE;

    /**
     * This method is used to read TradeLeg details from the provided resultSet.
     *
     * @param tradeLegRS
     * @return instance of TradeLeg containing the data from the resultSet
     */
    @Override
    public ITradeLeg readLeg(ResultSet tradeLegRS) {
        return readLeg(tradeLegRS, "");
    }

    @Override
    public String databaseEntity() {
        return Ird.ENTITY;
    }

    /**
     * This method is used to read TradeLeg details from the provided resultSet.
     *
     * @param tradeLegRS
     * @param prefix
     * @return instance of TradeLeg containing the data from the resultSet
     */
    public static Ird readLeg(ResultSet tradeLegRS, String prefix) {
        Ird tradeLeg = new Ird();
        tradeLeg.setTradeId(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.TRADE_ID)));
        tradeLeg.setValidTimeFrom(tradeLegRS.getTimestamp(StringUtils.concat(prefix, TradeLeg.VALID_TIME_FROM)));
        tradeLeg.setValidTimeTo(tradeLegRS.getTimestamp(StringUtils.concat(prefix, TradeLeg.VALID_TIME_TO)));
        tradeLeg.setTransactionTimeFrom(tradeLegRS.getTimestamp(StringUtils.concat(prefix, TradeLeg.TXN_TIME_FROM)));
        tradeLeg.setTransactionTimeTo(tradeLegRS.getTimestamp(StringUtils.concat(prefix, TradeLeg.TXN_TIME_TO)));

        tradeLeg.setLegNumber((int) tradeLegRS.getLong(StringUtils.concat(prefix, TradeLeg.LEG_NUMBER)));
        tradeLeg.setLegType(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.LEG_TYPE)));
        tradeLeg.setBook(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.BOOK)));
        tradeLeg.setInternalProductType(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.INT_PRD_TYPE)));
        tradeLeg.setInternalProductRef(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.INT_PRD_REF)));
        tradeLeg.setInstrumentId(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.INSTRUMENT_ID)));
        tradeLeg.setRic(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.RIC)));
        tradeLeg.setIsin(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.ISIN)));
        tradeLeg.setCurrency(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.CURRENCY)));
        tradeLeg.setExchange(tradeLegRS.getString(StringUtils.concat(prefix, TradeLeg.EXCHANGE)));

        tradeLeg.setIrdLegType(tradeLegRS.getString(StringUtils.concat(prefix, Ird.IRD_LEG_TYPE)));
        tradeLeg.setMaturityDate(StringUtils.getDate
                (tradeLegRS.getDate(StringUtils.concat(prefix, Ird.MATURITY_DATE))));
        tradeLeg.setNotional((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Ird.NOTIONAL)));
        tradeLeg.setIndex(tradeLegRS.getString(StringUtils.concat(prefix, Ird.INDEX)));
        tradeLeg.setNotionalExp(tradeLegRS.getString(StringUtils.concat(prefix, Ird.NOTIONAL_EXP)));
        tradeLeg.setTerm(tradeLegRS.getString(StringUtils.concat(prefix, Ird.TERM)));
        tradeLeg.setRate((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Ird.RATE)));
        tradeLeg.setBasis(tradeLegRS.getString(StringUtils.concat(prefix, Ird.BASIS)));
        tradeLeg.setSpread((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Ird.SPREAD)));
        tradeLeg.setSettlementCcy(tradeLegRS.getString(StringUtils.concat(prefix, Ird.SETTLEMENT_CCY)));
        tradeLeg.setSettlementAmount((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Ird.SETTLEMENT_AMNT)));
        tradeLeg.setSettlementDate(StringUtils.getDate
                (tradeLegRS.getDate(StringUtils.concat(prefix, Ird.SETTLEMENT_DATE))));
        tradeLeg.setBuySellInd(tradeLegRS.getString(StringUtils.concat(prefix, Ird.BUY_SELL_IND)));

        return tradeLeg;
    }

}
