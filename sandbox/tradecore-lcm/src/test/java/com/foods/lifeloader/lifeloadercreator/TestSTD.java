package com.foods.lifeloader.lifeloadercreator;

import com.foods.statediagram.STATE;
import com.foods.statediagram.StateTransitionGraphFactory;
import com.foods.statediagram.StateTransitionGraphInterface;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSTD {

static ApplicationContext testContext = new ClassPathXmlApplicationContext("SpringBeanTestClient.xml");
	
	private static final Logger logger = LoggerFactory.getLogger(TestSTD.class.getName());
	@Test
	public void test() throws Exception{
		Trade t = tradeCreation(testContext);
		
		StateTransitionGraphFactory transitionFactory =testContext.getBean("StateTransitionGraphFactory",StateTransitionGraphFactory.class);
		
		StateTransitionGraphInterface graphInterface = transitionFactory
				.getMyTransitionDiagram(t.getProductType());

		Trade tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.CREATE);
		
		TradeEvent et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.DONE ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.SAVE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.DONE ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		
		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.APPROVE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.VER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		
		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.SAVE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.PENDVER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		
		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.SAVE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.PENDVER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		
		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.APPROVE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.VER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.MATURE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.MAT ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");
		
		
		assert(true);
	}


	@Test
	public void test2() throws Exception{

		Trade t = tradeCreation(testContext);

		StateTransitionGraphFactory transitionFactory =testContext.getBean("StateTransitionGraphFactory",StateTransitionGraphFactory.class);

		StateTransitionGraphInterface graphInterface = transitionFactory
				.getMyTransitionDiagram(t.getProductType());

		Trade tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.CREATE);

		TradeEvent et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.DONE ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");


		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.APPROVE);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.VER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");


		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.PTERM);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.PENDVER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");

		for(ITradeLeg leg: et.getTradeLegList()){
			Ird ird = (Ird)leg;
			System.out.println(ird.getMaturityDate());
		}

		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.REJECT);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.PENDVER ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");

		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.CANCEL);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.PENDCANC ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");


		t=tt;
		tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.REJECT);
		et =tt.getTradeEventList().get(0);
		assert(et.getEventStatus().equals(  STATE.PENDCANC ));
		System.out.println(et.getEventStatus());
		System.out.println(et.getEventType()+"\n");

	}

	
	private static Trade tradeCreation(ApplicationContext context) throws Exception {
		Trade trade = null;
		/*TradeGeneratorServiceLegacy generater = context.getBean("TradeGeneratorServiceLegacy",
				TradeGeneratorServiceLegacy.class);
		// TradeConverterService2Pojo myConvertor =
		// context.getBean("TradeConverterService2Pojo",
		// TradeConverterService2Pojo.class);
		TradeConverterService2Pojo convertor = context.getBean("TradeConverterService2Pojo",
				TradeConverterService2Pojo.class);
		List<Map<String, String>> oldtradeMapLst = generater.serve(1);
		List<Trade> tradeMapLst2 = convertor.serve(oldtradeMapLst);
		trade = tradeMapLst2.get(0);*/

		TradeGenerator gen= TradeGeneratorHelper.getInstance("fi-irs");

		trade=gen.createTrade(2,1);

		return trade;
	}
}
