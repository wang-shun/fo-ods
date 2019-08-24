package com.gcppoc.upstreamloadtester;

import java.util.Collection;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.gcppoc.upstreamloadtester.loadtest.LoadGrpcTestInstance;
import com.gcppoc.upstreamloadtester.loadtest.LoadTestInstance;

import foods.bigtable.loadtest.EquityTradeGenerator;
import foods.bigtable.model.EquityTrade;

public class TestGrpcTestInstance {

	@Test
	public void test() throws Exception{
		ApplicationContext context = LoadGrpcTestInstance.getContext();
		
		assert(context!=null);
		
		{
			LoadGrpcTestInstance loader = context.getBean("GrpcLoadTestInstance",LoadGrpcTestInstance.class);
			assert(loader!=null);
			
			
			EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
	        Collection<EquityTrade> trades = tradeGenerator.createTrades(1,1);
	        loader.save(trades);
	        
		}
		
		assert(true);
	}

}
