package com.foods.statediagram;

import org.junit.Test;


public class TestTransition {

	//private static TradeGeneratorService createGenService;
//    private static TradeGeneratorService amendGenService;
//    private static TradeConverterService conService;
//  
//    private static TradeRetrievalService retService;
//    private static Timer totalTimer = new Timer(new UniformReservoir());
	@Test
	public void test() throws Exception{
		String assetClass = "cash-eq";
		
		//createGenService = new TradeGeneratorService(assetClass, "CREATE");
		
		
	/*	
		String assetClass = "cash-eq";
        String tableName = "tradecore";
        int tCount = 1000;
        int batchSize = 500;
        
      //Generating the instances of the services
        createGenService = new TradeGeneratorService(assetClass, "CREATE");
        amendGenService = new TradeGeneratorService(assetClass, "AMEND");
        conService = new TradeConverterService(assetClass);
        
        retService = new TradeRetrievalService(assetClass, tableName);
		ApplicationContext context =  StateTransitionGraphFactory.getContext();
		
		StateTransitionGraphFactory factory =context.getBean("StateTransitionGraphFactory",StateTransitionGraphFactory.class);
		
		
		StateTransitionGraph g = factory.getMyStateTransitionMap().get("swap");
		assert(g!=null);
		
		List< Map<String,String> > tradelst = conService.serve(createGenService.serve(1));
		
		for( Map<String, String> t : tradelst){
			//change the version and status
			g.transitionState(t,"swap", "NEW", "VER", "CREATE");
		}
		*/
		
	}

}
