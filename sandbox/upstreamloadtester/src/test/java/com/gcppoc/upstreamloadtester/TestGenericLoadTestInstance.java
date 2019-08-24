package com.gcppoc.upstreamloadtester;

import static org.junit.Assert.*;

import org.junit.Test;

import com.maplequad.fo.ods.ulg.app.GenericLoadTestInstance;
import com.maplequad.fo.ods.ulg.services.TradeConverterService;
import com.maplequad.fo.ods.ulg.services.TradeGeneratorService;

public class TestGenericLoadTestInstance {

	@Test
	public void test() throws Exception{
		
		String assetClass="cash-eq";
		
		int numOfTrade = Integer.parseInt("1");
		
		GenericLoadTestInstance loader = GenericLoadTestInstance.getContext().getBean("GenericLoadTestInstance",GenericLoadTestInstance.class);
		TradeGeneratorService genService=null;
	    TradeConverterService conService=null;
	    
	    genService = new TradeGeneratorService(assetClass);
        conService = new TradeConverterService(assetClass);
        
        loader.save(conService.serve(genService.serve(numOfTrade)));
		loader.destroy();
		loader.destroy();
		assert(true);
	}

}
