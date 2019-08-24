package com.foods.lifeloader.lifeloaderdummy.Publisher;

import com.maplequad.fo.ods.tradecore.lcm.services.TradeGenerateRequest;

import java.util.HashMap;
import java.util.Map;

public class TrackLog {
	public String serialNumber;
	public int numOfTrades;
	public int subnumber;
	public long requestTime;
	public long beginQTime;
	public long endQTime;
	public String operation;
	public String tradetype;
	public String osTradeId;
	public String action;
	public String tradeJson;
	
	public TrackLog(){}
	public TrackLog(TrackLog l){
		this.serialNumber=l.serialNumber;
		this.numOfTrades=l.numOfTrades;
		this.subnumber=l.subnumber;
		this.requestTime=l.requestTime;
		this.action=l.action;
		this.tradetype=l.tradetype;
		this.osTradeId=l.osTradeId;
		this.beginQTime=l.beginQTime;
		this.endQTime=l.endQTime;
		this.operation=l.operation;
		this.tradeJson=l.tradeJson;
		
	}
	
	
	public Map<String, String> LogtoMap(){
    	
    	Map<String,String> map = new HashMap<String,String>();
    	
    	map.put("serialNumber", String.valueOf(this.serialNumber));
    	map.put("numOfTrades", String.valueOf(this.numOfTrades));
    	map.put("subnumber", String.valueOf(this.subnumber));
    	map.put("ULT_requestTimestamp", String.valueOf(this.requestTime));
    	map.put("beginQTime",String.valueOf(this.beginQTime));
    	map.put("endQTime", String.valueOf(this.endQTime));
    	map.put("osTradeid",String.valueOf(this.osTradeId));
    	map.put("action",this.action);
    	map.put("operation", this.operation);
    	map.put("tradetype",this.tradetype);
    	map.put("tradeJson",this.tradeJson);
    	
    	return map;
    }

    public TradeGenerateRequest tradeSpec;
}
