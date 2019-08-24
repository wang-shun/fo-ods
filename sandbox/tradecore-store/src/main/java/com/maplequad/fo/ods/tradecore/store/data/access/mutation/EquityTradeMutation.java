package com.maplequad.fo.ods.tradecore.store.data.access.mutation;

import com.google.cloud.spanner.Mutation;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * EquityTradeMutation -
 *
 * This class creates all required mutations.
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class EquityTradeMutation {

    protected static final String UPSERT_IN = "upsert entered for table {} with {}";
    protected static final String UPSERT_OUT = "upsert exited for table {}";
    private static final String EQUITY_LEG = "EQUITY_LEG";
    private static final Logger LOGGER = LoggerFactory.getLogger(EquityTradeMutation.class);

    /**
     * This method generates the required mutation for TradeLeg table
     *
     * @param iTradeLeg
     * @return Mutation for TradeLeg
     */
    public static Mutation upsert(ITradeLeg iTradeLeg) {
        LOGGER.info(UPSERT_IN, EQUITY_LEG, iTradeLeg);
        Mutation.WriteBuilder builder = null;

        builder = Mutation.newInsertOrUpdateBuilder(EQUITY_LEG);
        Equity tradeLeg = (Equity) iTradeLeg;
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
        builder.set(Equity.CFI_CODE).to(tradeLeg.getCfiCode());
        builder.set(Equity.QUANTITY).to(tradeLeg.getQuantity());
        builder.set(Equity.PRICE).to(tradeLeg.getPrice());
        builder.set(Equity.GROSS_PRICE).to(tradeLeg.getGrossPrice());
        builder.set(Equity.BUY_SELL_IND).to(tradeLeg.getBuySellInd());
        LOGGER.info(UPSERT_OUT, EQUITY_LEG);
        return builder.build();
    }

}