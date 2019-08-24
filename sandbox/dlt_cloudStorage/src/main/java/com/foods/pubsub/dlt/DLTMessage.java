package com.foods.pubsub.dlt;

import java.util.HashMap;
import java.util.Map;

import com.foods.lifeloader.lifeloadercreator.processor.LCTrackLog;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

public class DLTMessage {
    public LCTrackLog log;
    public Trade trade;
    
    
    public Map<String, String> LogtoMap(){
    	
    	Map<String,String> map = new HashMap<String,String>();
    	
    	map.put("serialNumber", String.valueOf(log.serialNumber));
    	map.put("numOfTrades", String.valueOf(log.numOfTrades));
    	map.put("subnumber", String.valueOf(log.subnumber));
    	map.put("ULT_requestTimestamp", String.valueOf(log.ULT_requestTimestamp));
    	map.put("LC_arrivalTime",String.valueOf(log.LC_arrivalTime));
    	map.put("LC_CRUDStartTime", String.valueOf(log.LC_CRUDStartTime));
    	map.put("DB_StartTime", String.valueOf(log.DB_StartTime));
    	map.put("DB_EndTime", String.valueOf(log.DB_EndTime));
    	map.put("LC_CRUDEndTime", String.valueOf(log.LC_CRUDEndTime));
    	map.put("tradeid", String.valueOf(log.tradeid));
    	map.put("osTradeid",String.valueOf(log.osTradeid));
    	map.put("action",log.action);
    	
    	
    	
    	return map;
    }
    
    
}
