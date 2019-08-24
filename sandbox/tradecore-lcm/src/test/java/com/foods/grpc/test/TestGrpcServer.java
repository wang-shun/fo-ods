package com.foods.grpc.test;

import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.lcm.grpc.TradeCoreLifeCycleService;
import com.maplequad.fo.ods.tradecore.lcm.grpc.TradeCoreLifeCycleServiceGrpcServer;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeSTDRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeSTDResponse;
import io.grpc.stub.StreamObserver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestGrpcServer {

	
	TradeCoreLifeCycleServiceGrpcServer server=null;
	
	
	ApplicationContext testContext = new ClassPathXmlApplicationContext("SpringBeanTest.xml");
	@Before
	public void prepare(){
		
		ApplicationContext context =  new ClassPathXmlApplicationContext("SpringBeanStorageGrpcService.xml");
		
		server = context.getBean("TradeCoreLifeCycleServiceGrpcServer",TradeCoreLifeCycleServiceGrpcServer.class);
	}
	
	@Test
	public void test() throws Exception{
		//fail("Not yet implemented");
		int a=0;
		server=server;
		
		TradeCoreLifeCycleService s=server.getService();
		
		Trade t = this.tradeCreation(this.testContext);
		
		CreateTradeSTDRequest request= CreateTradeSTDRequest.newBuilder()
				.setTrade(TradeTransformer.toProto(t))
				.build();
		TestStreamObserver<CreateTradeSTDResponse> responseObserver=new TestStreamObserver<CreateTradeSTDResponse>();
		s.createTrade(request, responseObserver);
		
		//Retrieve result
		CreateTradeSTDResponse res = responseObserver.myObject;
		System.out.println(res.getTrade().getTradeId());
		assert (res.getTrade().getTradeId()!=null);
	}

	private static Trade tradeCreation(ApplicationContext context) throws Exception {
		Trade trade = null;
//		TradeGeneratorServiceLegacy generater = context.getBean("TradeGeneratorServiceLegacy",
//				TradeGeneratorServiceLegacy.class);
//		// TradeConverterService2Pojo myConvertor =
//		// context.getBean("TradeConverterService2Pojo",
//		// TradeConverterService2Pojo.class);
//		TradeConverterService2Pojo convertor = context.getBean("TradeConverterService2Pojo",
//				TradeConverterService2Pojo.class);
//		List<Map<String, String>> oldtradeMapLst = generater.serve(1);
//		List<Trade> tradeMapLst2 = convertor.serve(oldtradeMapLst);
//		trade = tradeMapLst2.get(0);
//
//		return trade;
		return null;
	}
	private class TestStreamObserver<T> implements StreamObserver<T>{
		public T myObject;
		
		public TestStreamObserver(){}
		@Override
		public void onNext(T value) {
			// TODO Auto-generated method stub
			myObject = value;
		}

		@Override
		public void onError(Throwable t) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCompleted() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
