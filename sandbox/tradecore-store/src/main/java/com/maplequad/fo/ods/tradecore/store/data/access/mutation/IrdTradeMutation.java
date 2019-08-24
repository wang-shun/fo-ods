package com.maplequad.fo.ods.tradecore.store.data.access.mutation;

import com.google.cloud.Date;
import com.google.cloud.spanner.Mutation;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * IrdTradeMutation -
 *
 * This class creates all required mutations.
 *
 * @author Madhav Mindhe
 * @since :   31/08/2017
 */
public class IrdTradeMutation {

    protected static final String UPSERT_IN = "upsert entered for table {} with {}";
    protected static final String UPSERT_OUT = "upsert exited for table {}";
    private static final String IRD_LEG = "IRD_LEG";
    private static final Logger LOGGER = LoggerFactory.getLogger(IrdTradeMutation.class);

    /**
     * This method generates the required mutation for TradeLeg table
     *
     * @param iTradeLeg
     * @return Mutation for TradeLeg
     */
    public static Mutation upsert(ITradeLeg iTradeLeg) {
        LOGGER.info(UPSERT_IN, IRD_LEG, iTradeLeg);
        Mutation.WriteBuilder builder = null;

        builder = Mutation.newInsertOrUpdateBuilder(IRD_LEG);
        Ird tradeLeg = (Ird) iTradeLeg;

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

        builder.set(Ird.IRD_LEG_TYPE).to(tradeLeg.getIrdLegType());
        int[] date1 = StringUtils.getYMD(tradeLeg.getMaturityDate());
        builder.set(Ird.MATURITY_DATE).to(Date.fromYearMonthDay(date1[0], date1[1], date1[2]));
        builder.set(Ird.NOTIONAL).to(tradeLeg.getNotional());
        builder.set(Ird.INDEX).to(tradeLeg.getIndex());
        builder.set(Ird.NOTIONAL_EXP).to(tradeLeg.getNotionalExp());
        builder.set(Ird.TERM).to(tradeLeg.getTerm());
        builder.set(Ird.RATE).to(tradeLeg.getRate());
        builder.set(Ird.BASIS).to(tradeLeg.getBasis());
        builder.set(Ird.SPREAD).to(tradeLeg.getSpread());
        builder.set(Ird.SETTLEMENT_CCY).to(tradeLeg.getSettlementCcy());
        builder.set(Ird.SETTLEMENT_AMNT).to(tradeLeg.getSettlementAmount());
        int[] date2 = StringUtils.getYMD(tradeLeg.getSettlementDate());
        builder.set(Ird.SETTLEMENT_DATE).to(Date.fromYearMonthDay(date2[0], date2[1], date2[2]));
        builder.set(Ird.BUY_SELL_IND).to(tradeLeg.getBuySellInd());

        LOGGER.info(UPSERT_OUT, IRD_LEG);
        return builder.build();
    }

}