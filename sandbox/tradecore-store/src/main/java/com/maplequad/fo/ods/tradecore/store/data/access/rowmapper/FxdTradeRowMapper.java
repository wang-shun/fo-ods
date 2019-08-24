package com.maplequad.fo.ods.tradecore.store.data.access.rowmapper;

import com.google.cloud.spanner.ResultSet;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;

import java.time.LocalDate;

/***
 * EquityTradeRowMapper -
 *
 * This class is used to raed the complete trade from the resultset.
 *
 * @author Madhav Mindhe
 * @since :   13/08/2017
 */
public enum FxdTradeRowMapper implements TradeLegMapper {
    INSTANCE;

    @Override
    public ITradeLeg readLeg(ResultSet resultSet) {
        return readLeg(resultSet, "");
    }

    @Override
    public String databaseEntity() {
        return Fxd.ENTITY;
    }


    /**
     * This method is used to read TradeLeg details from the provided resultSet.
     *
     * @param tradeLegRS
     * @param prefix
     * @return instance of TradeLeg containing the data from the resultSet
     */
    public static Fxd readLeg(ResultSet tradeLegRS, String prefix) {
        Fxd tradeLeg = new Fxd();
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

        tradeLeg.setSpotRate((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Fxd.SPOT_RATE)));
        tradeLeg.setVolume((float)tradeLegRS.getDouble(StringUtils.concat(prefix, Fxd.VOLUME)));
        tradeLeg.setAllInRate((float) tradeLegRS.getDouble(StringUtils.concat(prefix, Fxd.ALL_IN_RATE)));
        tradeLeg.setBaseCcy(tradeLegRS.getString(StringUtils.concat(prefix, Fxd.BASE_CCY)));
        tradeLeg.setCounterCcy(tradeLegRS.getString(StringUtils.concat(prefix, Fxd.COUNTER_CCY)));
        tradeLeg.setTenor(tradeLegRS.getString(StringUtils.concat(prefix, Fxd.TENOR)));
        tradeLeg.setValueDate(LocalDate.parse(tradeLegRS.getDate(StringUtils.concat(prefix, Fxd.VALUE_DATE)).toString()));
        tradeLeg.setBuySellInd(tradeLegRS.getString(StringUtils.concat(prefix, Fxd.BUY_SELL_IND)));

        return tradeLeg;
    }


}
