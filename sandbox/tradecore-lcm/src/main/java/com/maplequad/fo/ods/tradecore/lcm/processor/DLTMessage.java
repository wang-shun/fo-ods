package com.maplequad.fo.ods.tradecore.lcm.processor;

import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

public class DLTMessage {
	public com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog log;
	public Trade trade;

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("DLTMessage{");
		sb.append("log=").append(log);
		sb.append(", trade=").append(trade);
		sb.append('}');
		return sb.toString();
	}
}
