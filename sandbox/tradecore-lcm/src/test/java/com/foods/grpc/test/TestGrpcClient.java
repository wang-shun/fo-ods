package com.foods.grpc.test;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import com.maplequad.fo.ods.tradecore.lcm.utils.CreateUUIDHelper;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeSTDRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeSTDResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreLifeCycleGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestGrpcClient {
	
	static ApplicationContext testContext = new ClassPathXmlApplicationContext("SpringBeanTestClient.xml");
	
	private static final Logger logger = LoggerFactory.getLogger(TestGrpcClient.class.getName());

	private final ManagedChannel channel;
	private final TradeCoreLifeCycleGrpc.TradeCoreLifeCycleBlockingStub blockingStub;
	
	public TestGrpcClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
	}

	/**
	 * Construct client for accessing RouteGuide server using the existing
	 * channel.
	 */
	public TestGrpcClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
		blockingStub = TradeCoreLifeCycleGrpc.newBlockingStub(channel);
		
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	
	public static void main(String[] args)throws Exception{
		TestGrpcClient client = new TestGrpcClient("104.155.10.143", 3000);
		//TestGrpcClient client = new TestGrpcClient("localhost", 3000);
	    try {
	      // Looking for a valid feature
	    	Trade t = tradeCreation(testContext);
	    	
	    	LCTrackLog l = new LCTrackLog();
	    	l.osTradeid=t.getOsTradeId();
	    	l.serialNumber=String.valueOf(CreateUUIDHelper.createUUID());
	    	l.ULT_requestTimestamp=new Date().getTime();
	    	l.numOfTrades=1;
	    	l.subnumber=0;
	    	l.action="CREATE";
	    	
	    	CreateTradeSTDRequest request= CreateTradeSTDRequest.newBuilder()
					.setTrade(TradeTransformer.toProto(t))
					.setTrackLog(l.copyToProto())
					.build();
	    	CreateTradeSTDResponse res=client.blockingStub.createTrade(request);
	      
	    	System.out.println("TradeId:"+res.getTrade().getTradeId());
	    	System.out.println("SerialNumber:"+l.serialNumber);
	    	
			System.out.println("Original ULT start date:"+ Timestamp.fromProto(res.getDsStartTimestamp()).toString());
			System.out.println("Original ULT end date:"+ Timestamp.fromProto(res.getDsFinishTimestamp()).toString());
	    	assert (res.getTrade().getTradeId()!=null && res.getTrade().getTradeId().length()>1);
			Date d = new Date();
			d.setTime(Timestamp.fromProto(res.getDsStartTimestamp()).toSqlTimestamp().getTime());
			assert (l.ULT_requestTimestamp==d.getTime());
			
			System.out.println("Testing clear!");
	      
	    } finally {
	      client.shutdown();
	    }
	}
	
	private static Trade tradeCreation(ApplicationContext context) throws Exception {

		TradeGenerator gen= TradeGeneratorHelper.getInstance("cash-eq");

		Trade trade=gen.createTrade(1,1);
/*
		Trade trade = null;
		TradeGeneratorServiceLegacy generater = context.getBean("TradeGeneratorServiceLegacy",
				TradeGeneratorServiceLegacy.class);
		// TradeConverterService2Pojo myConvertor =
		// context.getBean("TradeConverterService2Pojo",
		// TradeConverterService2Pojo.class);
		TradeConverterService2Pojo convertor = context.getBean("TradeConverterService2Pojo",
				TradeConverterService2Pojo.class);
		List<Map<String, String>> oldtradeMapLst = generater.serve(1);
		List<Trade> tradeMapLst2 = convertor.serve(oldtradeMapLst);
		trade = tradeMapLst2.get(0);*/
		
		return trade;
	}
}
