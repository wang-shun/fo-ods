package com.foods.statediagram;

import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import junit.framework.TestCase;

public class TestTransition2 extends TestCase {

	@Test
	public void testPrintMessage() throws Exception{
		ApplicationContext context =  StateTransitionGraphFactory.getContext();
		
		StateTransitionGraphFactory factory =context.getBean("StateTransitionGraphFactory",StateTransitionGraphFactory.class);

		//TradeGeneratorHelper helper = TradeGeneratorHelper.

		
		//assert(g!=null);
		
		
		//g.transitionState(null, "NEW", "VER", "CREATE");
		
	      //assertEquals(message,messageUtil.printMessage());
	}
}
