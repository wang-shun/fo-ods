package com.gcppoc.upstreamloadtester;

import java.util.Collection;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.gcppoc.upstreamloadtester.loadtest.LoadTestInstance;

import foods.bigtable.loadtest.EquityTradeGenerator;
import foods.bigtable.model.EquityTrade;



public class TestLoadTestInstance {

	@Test
	public void test() throws Exception{
		ApplicationContext context = LoadTestInstance.getContext();
		
		assert(context!=null);
		
		{
		LoadTestInstance loader = context.getBean("LoadTestInstance",LoadTestInstance.class);
		assert(loader!=null);
		
		assert (loader.getHbaseconnection()!=null && loader.getHbaseconnection().isClosed()==false);
		
		EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        Collection<EquityTrade> trades = tradeGenerator.createTrades(100,1);
        loader.save(trades);
        loader.read(trades);
        //loader.close();
		}
        //tester.close();
		
		{
        LoadTestInstance loader = LoadTestInstance.getContext().getBean("LoadTestInstance",LoadTestInstance.class);
        assert(loader!=null);
		assert (loader.getHbaseconnection()!=null );
		assert(loader.getHbaseconnection().isClosed()==false);
		
		EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        Collection<EquityTrade> trades = tradeGenerator.createTrades(1,1);
        loader.save(trades);
		//loader.close();
		}
		
		assert(true);
	}

}
