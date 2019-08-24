package foods.dlt.test;

import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import foods.dlt.processor.BackOfficeProcesser;
import foods.dlt.stats.StatsSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBackOfficeProcess {

    ApplicationContext context =  null;

    StatsSubscriber sub = null;
    @Before
    public void prepare(){
        context =  new ClassPathXmlApplicationContext("SpringBeanTest.xml");
        sub = context.getBean("StatsSubscriber",StatsSubscriber.class);
    }

    @Test
    public void test()throws Exception{
        BackOfficeProcesser b =sub.getBackOfficeProcesser();
        Trade t = tradeCreation(context);



        b.setSuccessRate(0);
        Trade newtrade=b.processTrade(t,new LCTrackLog());


        System.out.println(t.getTradeEventList().get(0).getEventType());
        System.out.println(newtrade.getTradeEventList().get(0).getEventType());
        assert(!t.getTradeEventList().get(0).getEventType().equals(newtrade.getTradeEventList().get(0).getEventType()));
    }

    private static Trade tradeCreation(ApplicationContext context) throws Exception {
        TradeGenerator gen= TradeGeneratorHelper.getInstance("cash-eq");

        Trade t=gen.createTrade(1,1);
        return t;
    }

}
