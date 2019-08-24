package com.maplequad.fo.ods.tradecore.lcm.processor;

import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;

public interface TradeStoreService {
	TradeCoreStoreResponse getLatestTrades(TradeQuery query);
	TradeCoreStoreResponse getLatestTrade(String tradeid);
	TradeCoreStoreResponse createTrade(Trade tt);
	TradeCoreStoreResponse updateTrade(Trade tt);
}
