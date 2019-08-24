package com.maplequad.fo.ods.tradecore.store.data.access.mutation;

import com.google.cloud.spanner.Mutation;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/***
 * FxdTradeMutation -
 *
 * This class creates all required mutations.
 *
 * @author Madhav Mindhe
 * @since :   31/08/2017
 */
public class FxdTradeMutation {

    protected static final String UPSERT_IN = "upsert entered for table {} with {}";
    protected static final String UPSERT_OUT = "upsert exited for table {}";
    private static final String FXD_LEG = "FXD_LEG";
    private static final Logger LOGGER = LoggerFactory.getLogger(FxdTradeMutation.class);

    /**
     * This method generates the required mutation for TradeLeg table
     *
     * @param iTradeLeg
     * @return Mutation for TradeLeg
     */
    public static Mutation upsert(ITradeLeg iTradeLeg) {
        LOGGER.info(UPSERT_IN, FXD_LEG, iTradeLeg);
        Mutation.WriteBuilder builder = null;

        builder = Mutation.newInsertOrUpdateBuilder(FXD_LEG);
        Fxd tradeLeg = (Fxd) iTradeLeg;

        //Primary Key
        builder.set(TradeLeg.TRADE_ID).to(tradeLeg.getTradeId());
        builder.set(TradeLeg.LEG_NUMBER).to(tradeLeg.getLegNumber());

        //Bi-Temporal
        builder.set(TradeLeg.VALID_TIME_FROM).to(tradeLeg.getValidTimeFrom());
        builder.set(TradeLeg.TXN_TIME_FROM).to(tradeLeg.getTransactionTimeFrom());
        builder.set(TradeLeg.VALID_TIME_TO).to(tradeLeg.getValidTimeTo());
        builder.set(TradeLeg.TXN_TIME_TO).to(tradeLeg.getTransactionTimeTo());

        //Attributes
        builder.set(TradeLeg.LEG_TYPE).to(tradeLeg.getLegType());
        builder.set(TradeLeg.BOOK).to(tradeLeg.getBook());
        builder.set(TradeLeg.INT_PRD_TYPE).to(tradeLeg.getInternalProductType());
        builder.set(TradeLeg.INT_PRD_REF).to(tradeLeg.getInternalProductRef());
        builder.set(TradeLeg.INSTRUMENT_ID).to(tradeLeg.getInstrumentId());
        builder.set(TradeLeg.RIC).to(tradeLeg.getRic());
        builder.set(TradeLeg.ISIN).to(tradeLeg.getIsin());
        builder.set(TradeLeg.CURRENCY).to(tradeLeg.getCurrency());
        builder.set(TradeLeg.EXCHANGE).to(tradeLeg.getExchange());

        builder.set(Fxd.SPOT_RATE).to(tradeLeg.getSpotRate());
        builder.set(Fxd.ALL_IN_RATE).to(tradeLeg.getAllInRate());
        builder.set(Fxd.BASE_CCY).to(tradeLeg.getBaseCcy());
        builder.set(Fxd.COUNTER_CCY).to(tradeLeg.getCounterCcy());
        builder.set(Fxd.TENOR).to(tradeLeg.getTenor());
        builder.set(Fxd.VALUE_DATE).to(localDateToGoogleDate(tradeLeg.getValueDate()));
        builder.set(Fxd.BUY_SELL_IND).to(tradeLeg.getBuySellInd());
        builder.set(Fxd.VOLUME).to(tradeLeg.getVolume());

        LOGGER.info(UPSERT_OUT, FXD_LEG);
        return builder.build();
    }

    private static com.google.cloud.Date localDateToGoogleDate(LocalDate valueDate) {
        return com.google.cloud.Date.fromYearMonthDay(
                valueDate.getYear(),
                valueDate.getMonth().getValue(),
                valueDate.getDayOfMonth());
    }

}