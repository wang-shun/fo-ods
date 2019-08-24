package com.maplequad.fo.ods.tradecore.lcm.grpc;

import java.util.Date;

import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreLifeCycleGrpc;
import com.maplequad.fo.ods.tradecore.lcm.helper.TradeStoreGRPCHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.maplequad.fo.ods.tradecore.proto.service.grpc.*;

import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;


import io.grpc.stub.StreamObserver;

public class TradeCoreLifeCycleService extends TradeCoreLifeCycleGrpc.TradeCoreLifeCycleImplBase
		implements ApplicationContextAware {
	ApplicationContext context;

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeCoreLifeCycleService.class);

	public TradeCoreLifeCycleService(){}
	TradeStoreGRPCHelper grpcHelper=null;
	private final static String SUCCESS="SUCCESS";
	private final static String FAIL="FAIL";
	
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		context = arg0;
	}
	
	

	@Override
	public void createTrade(CreateTradeSTDRequest request, StreamObserver<CreateTradeSTDResponse> responseObserver) {
		// TODO Auto-generated method stub
		CreateTradeSTDResponse res;
		Timestamp dsStartTS = Timestamp.now();
		try{
			res=this.insertTrade(request);
			
		}catch (Exception e){
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			res = CreateTradeSTDResponse.newBuilder()
					.setTrade(request.getTrade())
					.setDsStartTimestamp(dsStartTS.toProto())
	                .setDsFinishTimestamp(Timestamp.now().toProto())
	                .setStatus(this.FAIL)
					.build();
		}
		responseObserver.onNext(res);
        responseObserver.onCompleted();
	}

	@Override
	public void readTrade(ReadTradeSTDRequest request, StreamObserver<ReadTradeSTDResponse> responseObserver) {
		// TODO Auto-generated method stub
		ReadTradeSTDResponse res=null;
		TradeOuterClass.Trade trade=null;
		Timestamp dsStartTS = Timestamp.now();
		try{
			res=this.readTrade(request);
			
		}catch (Exception e){
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			res = ReadTradeSTDResponse.newBuilder()
					.setTrades(0, trade)
					.setDsStartTimestamp(dsStartTS.toProto())
	                .setDsFinishTimestamp(Timestamp.now().toProto())
					.build();
		}
		
		responseObserver.onNext(res);
        responseObserver.onCompleted();
	}

	@Override
	public void updateTrade(UpdateTradeSTDRequest request, StreamObserver<UpdateTradeSTDResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.updateTrade(request, responseObserver);
	}

	
	protected CreateTradeSTDResponse insertTrade(CreateTradeSTDRequest request) throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,TradeValidationException {
		TradeOuterClass.Trade pt = request.getTrade();
		LOGGER.info("Received Trade: {}",pt);
		Trade t = TradeTransformer.fromProto(pt);
		
		Trade newTrade = null;
		
		//insert trade here
		LCTrackLog tLog=new LCTrackLog();
		tLog.copyFromProto(request.getTrackLog());
		
		tLog.ULT_requestTimestamp=tLog.ULT_requestTimestamp==0?new Date().getTime():tLog.ULT_requestTimestamp;
		
		LOGGER.info("SerialNumber:"+request.getTrackLog().getSerialNumber());
		LOGGER.info("Translated SerialNumber:"+tLog.serialNumber);
		
		
		newTrade=this.grpcHelper.insertTrade(t,tLog);
		
		Date d = new Date();
		d.setTime(tLog.ULT_requestTimestamp);
		Timestamp dsStartTS = Timestamp.of(d);
		
		LOGGER.info("Outgoing Trade {}",TradeTransformer.toProto(newTrade));
		
		CreateTradeSTDResponse res = CreateTradeSTDResponse.newBuilder()
				.setTrade(TradeTransformer.toProto(newTrade))
				.setDsStartTimestamp(dsStartTS.toProto())
                .setDsFinishTimestamp(Timestamp.now().toProto())
                .setStatus(this.SUCCESS)
				.build();
		
		return res;
	}
	
	protected ReadTradeSTDResponse readTrade(ReadTradeSTDRequest request){
		ReadTradeSTDResponse res=null;
		
		TradeOuterClass.Trade t=request.getTrade();
		
		
		
		return res;
	}
	
//	@Override
//	public void createTrade(CreateTradeRequest request, StreamObserver<CreateTradeResponse> responseObserver) {
//		// TODO Auto-generated method stub
//		
//		CreateTradeResponse res = CreateTradeResponse.newBuilder().build();
//
//		TradeOuterClass.Trade pt = request.getTrade();
//		Trade t = TradeTransformer.fromProto(pt);
//		
//		Trade newTrade = null;
//		Timestamp dsStartTS = Timestamp.now();
//		//insert trade here
//		
//		res.setTrade(TradeTransformer.toProto(newTrade));
//		
//		//Finish serving request
//		responseObserver.onNext(res);
//		//super.createTrade(request, responseObserver);
//		responseObserver.onCompleted();
//	}
//
//	@Override
//	public void readTrade(ReadTradeRequest request, StreamObserver<ReadTradeResponse> responseObserver) {
//		// TODO Auto-generated method stub
//		super.readTrade(request, responseObserver);
//	}
//
//	@Override
//	public void updateTrade(UpdateTradeRequest request, StreamObserver<UpdateTradeResponse> responseObserver) {
//		// TODO Auto-generated method stub
//		super.updateTrade(request, responseObserver);
//	}


	
	public TradeStoreGRPCHelper getGrpcHelper() {
		return grpcHelper;
	}



	public void setGrpcHelper(TradeStoreGRPCHelper grpcHelper) {
		this.grpcHelper = grpcHelper;
	}


	
}
