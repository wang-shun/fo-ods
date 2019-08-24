package com.maplequad.fo.ods.tradecore.balcm.handlers;

import com.maplequad.fo.ods.tradecore.balcm.service.BulkAmendProcessor;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public enum StockSplitHandler implements BulkAmendProcessor {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockSplitHandler.class);

    private BulkAmendOuterClass.Split split=null;
    @Override
    public TradeQuery createQuery(BulkAmendOuterClass.BulkAmendDetails details) {
        this.split = details.getSplit();
        return TradeQuery.newBuilder().setByInstrumentId(split.getInstrumentId()).build();
    }

    @Override
    public List<Trade> processTrades(List<Trade> impactedTrades) {
        LOGGER.info("handleBulkAmend entered for {} trades", impactedTrades.size());
        List<Trade> splitTrades = new ArrayList<>();
        double ratio =split.getRatio();
        for (Trade t : impactedTrades) {
            TradeEvent e=t.getTradeEventList().get(0);
            for( ITradeLeg leg: e.getTradeLegList()) {
                if(! (leg instanceof Equity)) {
                    continue;
                }
                Equity eq = (Equity)leg;
                int q=eq.getQuantity();
                float gp=eq.getGrossPrice();
                float p=eq.getPrice();
                q= (int)Math.floor((double)q *ratio);
                p = (float) ((double) gp / q);
                //gp = p*q;
                eq.setQuantity(q);
                //eq.setGrossPrice(gp);
                eq.setPrice(p);
            }
            splitTrades.add(t);
            LOGGER.info("Tradeid: {} splitting to: {}",t.getTradeId(),ratio);
        }

        return splitTrades;
    }
}
