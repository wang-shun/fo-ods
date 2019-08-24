package com.maplequad.fo.ods.tradecore.lcm.processor;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery;

import java.util.concurrent.atomic.AtomicInteger;

public class MockTradeStoreService implements TradeStoreService {

	private final String prefix = String.valueOf(System.currentTimeMillis());
	private final AtomicInteger cnt = new AtomicInteger();

	@Override
	public TradeCoreStoreResponse getLatestTrades(Tradequery.TradeQuery query) {
		return null;
	}

	@Override
	public TradeCoreStoreResponse getLatestTrade(String tradeid) {
		return null;
	}

	@Override
	public TradeCoreStoreResponse createTrade(Trade tt) {
		TradeCoreStoreResponse response = new TradeCoreStoreResponse();
		String id = prefix + cnt.incrementAndGet();
		tt.setTradeId(id);
		response.setTrade(tt);
		Timestamp now = Timestamp.now();
		response.setDsStartTS(now);
		response.setDsFinishTS(now);
		return response;
	}

	@Override
	public TradeCoreStoreResponse updateTrade(Trade tt) {
		return null;
	}
}
