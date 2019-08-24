package com.maplequad.fo.ods.tradecore.store.data.access.rowmapper;

import com.google.cloud.spanner.ResultSet;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;

/***
 * EquityTradeRowMapper -
 *
 * This class is used to raed the complete trade from the resultset.
 *
 * @author Madhav Mindhe
 * @since :   13/08/2017
 */
public enum EquityTradeRowMapper implements TradeLegMapper {
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
        return Equity.ENTITY;
    }

    /**
     * This method is used to read TradeLeg details from the provided resultSet.
     *
     * @param tradeLegRS
     * @param prefix
     * @return instance of TradeLeg containing the data from the resultSet
     */
    public static Equity readLeg(ResultSet tradeLegRS, String prefix) {
        Equity tradeLeg = new Equity();
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
        tradeLeg.setCfiCode(tradeLegRS.getString(StringUtils.concat(prefix, Equity.CFI_CODE)));
        tradeLeg.setQuantity((int) tradeLegRS.getLong(StringUtils.concat(prefix, Equity.QUANTITY)));
        tradeLeg.setPrice((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Equity.PRICE)));
        tradeLeg.setGrossPrice((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Equity.GROSS_PRICE)));
        tradeLeg.setBuySellInd(tradeLegRS.getString(StringUtils.concat(prefix, Equity.BUY_SELL_IND)));

        return tradeLeg;
    }

}
