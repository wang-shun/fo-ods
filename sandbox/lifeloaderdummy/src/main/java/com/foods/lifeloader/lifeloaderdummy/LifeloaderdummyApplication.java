package com.foods.lifeloader.lifeloaderdummy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LifeloaderdummyApplication {

	public static void main(String[] args) throws Exception{
		SpringApplication.run(LifeloaderdummyApplication.class, args);

//		TradeGenerator gen= TradeGeneratorHelper.getInstance("cash-eq");
//
//		Trade t=gen.createTrade(1,1);
//
//		assert (t!=null);
//		assert(t.getTradeEventList()!=null);
//		System.out.println("Test ok");
	}
}
