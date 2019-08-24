package com.maplequad.fo.ods.tradecore.data.model;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

import java.util.List;

public class TradeCoreStoreResponse {

    private Trade trade;
    private List<Trade> trades;
    private Timestamp dsStartTS;
    private Timestamp dsFinishTS;

    public Trade getTrade() {
        if (trade == null && trades != null && !trades.isEmpty()) {
            trade = trades.get(0);
        }
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public Timestamp getDsStartTS() {
        return dsStartTS;
    }

    public void setDsStartTS(Timestamp dsStartTS) {
        this.dsStartTS = dsStartTS;
    }

    public Timestamp getDsFinishTS() {
        return dsFinishTS;
    }

    public void setDsFinishTS(Timestamp dsFinishTS) {
        this.dsFinishTS = dsFinishTS;
    }

}
