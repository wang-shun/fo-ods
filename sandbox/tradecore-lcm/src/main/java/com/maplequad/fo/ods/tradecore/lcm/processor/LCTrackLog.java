package com.maplequad.fo.ods.tradecore.lcm.processor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.LCTrackLogProto;

public class LCTrackLog {
	public String serialNumber;
	public int numOfTrades;
	public int subnumber;
	public long ULT_requestTimestamp;
	public long LC_arrivalTime;
	public long LC_CRUDStartTime;
	public long DB_StartTime;
	public long DB_EndTime;
	public long LC_CRUDEndTime;
	public String tradeid;
	public String osTradeid;
	public String action;
	public long DLT_arriveTime;
	public String errormsg="";

	//public long statsStart;
	
	public LCTrackLog(){}
	public LCTrackLog(LCTrackLog l){
		this.serialNumber=l.serialNumber;
		this.numOfTrades=l.numOfTrades;
		this.subnumber=l.subnumber;
		this.ULT_requestTimestamp=l.ULT_requestTimestamp;
		this.errormsg=l.errormsg;
		//this.statsStart=l.statsStart;
	}
	public long pubsubtravelTime;
	public long CRUDprocessTime;
	public long storageTime;
	
	public  void calculate(){
		pubsubtravelTime = this.LC_arrivalTime-this.ULT_requestTimestamp;
		CRUDprocessTime=this.LC_CRUDEndTime-this.LC_CRUDStartTime;
		this.storageTime=this.DB_EndTime-this.DB_StartTime;
	}

	public long getLcTime() { return LC_CRUDEndTime - LC_arrivalTime; }
	public long getLcToDbTime() { return DB_StartTime - LC_CRUDStartTime; }
	public long getTotalTime(long dltStartTime) { return dltStartTime - ULT_requestTimestamp; }
	public long getLcToDlt(long dltStartTime) { return dltStartTime - LC_CRUDEndTime; }
	
	public final static long convertTimeStampProto(com.google.protobuf.Timestamp p){
		java.sql.Timestamp jt=Timestamp.fromProto(p).toSqlTimestamp();
		return jt.getTime();
	}
	public final static com.google.protobuf.Timestamp convert2TimeStampProto(long millisecond){
		com.google.protobuf.Timestamp p;
		
		Date d=new Date();
		d.setTime(millisecond);
		Timestamp tt=Timestamp.of(d);
		p = tt.toProto();
		
		return p;
	}
	
	public final static long convertTimeStamp(Timestamp p){
		java.sql.Timestamp jt=p.toSqlTimestamp();
		return jt.getTime();
	}
	public final static Timestamp convert2TimeStamp(long millisecond){
		
		Date d=new Date();
		d.setTime(millisecond);
		Timestamp tt=Timestamp.of(d);
		
		
		return tt;
	}
	
	public void copyFromProto(LCTrackLogProto proto){
		this.serialNumber=String.valueOf(proto.getSerialNumber());
		this.numOfTrades=proto.getNumOfTrades();
		this.subnumber=proto.getSubnumber();
		this.ULT_requestTimestamp=convertTimeStampProto (proto.getULTRequestTimestamp());
		this.LC_arrivalTime=convertTimeStampProto (proto.getLCArrivalTime());
		this.LC_CRUDStartTime=convertTimeStampProto(proto.getLCCRUDStartTime());
		this.LC_CRUDEndTime=convertTimeStampProto(proto.getLCCRUDEndTime());
		this.DB_StartTime=convertTimeStampProto(proto.getDBStartTime());
		this.DB_EndTime=convertTimeStampProto(proto.getDBEndTime());
		
			this.tradeid=proto.getTradeid();
		
		this.osTradeid=proto.getOsTradeid();
		this.action=proto.getAction();
	}
	
	public LCTrackLogProto copyToProto(){
		LCTrackLogProto.Builder b = LCTrackLogProto.newBuilder();
		
		b.setSerialNumber(Long.parseLong(this.serialNumber));
		b.setNumOfTrades(this.numOfTrades);
		b.setSubnumber(this.subnumber);
		b.setULTRequestTimestamp(convert2TimeStampProto(this.ULT_requestTimestamp));
		b.setLCArrivalTime(convert2TimeStampProto(this.LC_arrivalTime));
		b.setLCCRUDStartTime(convert2TimeStampProto(this.LC_CRUDStartTime));
		b.setLCCRUDEndTime(convert2TimeStampProto(this.LC_CRUDEndTime));
		b.setDBStartTime(convert2TimeStampProto(this.DB_StartTime));
		b.setDBEndTime(convert2TimeStampProto(this.DB_EndTime));
		if(this.tradeid!=null)
			b.setTradeid(this.tradeid);
		if(this.osTradeid!=null)
			b.setOsTradeid(this.osTradeid);
		if(this.action!=null)
			b.setAction(this.action);
		return b.build();
	}
	
	public static Map<String, String> LogtoMap(LCTrackLog log){
    	
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
    	map.put("DLT_ArriveTime", String.valueOf(log.DLT_arriveTime));
    	map.put("errormsg",log.errormsg);
    	//map.put("statsStart",String.valueOf(log.statsStart));

    	return map;
    }
	
}
