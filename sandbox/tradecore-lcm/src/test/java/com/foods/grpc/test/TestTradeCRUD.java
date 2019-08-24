package com.foods.grpc.test;

import com.foods.statediagram.STATE;
import com.foods.statediagram.StateTransitionGraphFactory;
import com.foods.statediagram.StateTransitionGraphInterface;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.processor.GrpcSpannerServiceProcessor;
import com.maplequad.fo.ods.tradecore.lcm.processor.TradeProcessorInterface;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestTradeCRUD {

    @Test
    public void test() throws Exception{

        ApplicationContext context =  new ClassPathXmlApplicationContext("SpringBeanTest.xml");

        StateTransitionGraphFactory transitionFactory = context.getBean("StateTransitionGraphFactory", StateTransitionGraphFactory.class);
        TradeGenerator gen= TradeGeneratorHelper.getInstance("fx-fwd");

        Trade t=gen.createTrade(1,1);

        StateTransitionGraphInterface graphInterface = transitionFactory
                .getMyTransitionDiagram(t.getProductType());

        Trade tt = graphInterface.transitionState(t, t.getProductType(), STATE.ACTION.CREATE);

        GrpcSpannerServiceProcessor myProcessor = context.getBean("TradeProcessor", GrpcSpannerServiceProcessor.class);
        //tt.getTradeEventList().get(0).setEventRemarks("");

        Trade ntrade=myProcessor.getAccessService().insert(tt);
/*
        ntrade.getTradeEventList().get(0).setSalesman("Peter");
        //ntrade.getTradeEventList().get(0).setEventRemarks("");
        myProcessor.getAccessService().update(ntrade);*/
        Trade rTrade=myProcessor.retrieveOldTrade(ntrade.getOsTradeId(),ntrade.getAssetClass());



        System.out.println(ntrade.getTradeId());
        assert(t!=null);
    }
}
