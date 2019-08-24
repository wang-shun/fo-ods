package com.maplequad.fo.ods.tradecore.lcm.processor;

import java.util.List;
import java.util.Map;

import com.maplequad.fo.ods.tradecore.lcm.exception.NoTradeFoundException;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

public interface TradeProcessorInterface {
	//public List<Trade> processTradeFromMap(List< Map<String, String> > mapList) throws Exception;
	
	public Trade processTradeFromMapSingle(String orgJson) throws Exception,NoTradeFoundException;
	
	public List<Trade> queryTradeList(List<String> tradeId) throws Exception;
	
	public List<Trade> processTradeList(List< Trade > mapList) throws Exception;
}
