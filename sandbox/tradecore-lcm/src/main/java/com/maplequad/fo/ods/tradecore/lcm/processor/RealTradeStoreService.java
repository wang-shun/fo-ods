package com.maplequad.fo.ods.tradecore.lcm.processor;

import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;

public class RealTradeStoreService implements TradeStoreService {

	private TradeCoreStoreService impl;

	@Override
	public TradeCoreStoreResponse getLatestTrades(TradeQuery query) {
		return impl.getLatestTrades(query);
	}

	@Override
	public TradeCoreStoreResponse getLatestTrade(String tradeid) {
		return impl.getLatestTrade(tradeid);
	}

	@Override
	public TradeCoreStoreResponse createTrade(Trade tt) {
		return impl.createTrade(tt);
	}

	@Override
	public TradeCoreStoreResponse updateTrade(Trade tt) {
		return impl.updateTrade(tt);
	}

	public void setImpl(TradeCoreStoreService impl) {
		this.impl = impl;
	}
}
