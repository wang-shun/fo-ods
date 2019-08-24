package com.foods.tradecreation.test;

import com.foods.lifeloader.lifeloaderdummy.Operator.TradeHelper;
import com.foods.lifeloader.lifeloaderdummy.Publisher.TrackLog;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGenerateRequest;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.LinkedList;
import java.util.List;

public class TestTradeCreationSet {

    ApplicationContext context;
    @Before
    public void prepare(){

         context= new ClassPathXmlApplicationContext("SpringBean.xml");
    }

    @Test
    public void test() throws Exception{

        List<Trade> lt = new LinkedList<Trade>();
        TrackLog log=new TrackLog();
        log.tradeSpec = new TradeGenerateRequest();
        log.tradeSpec.TradeType="fi-irs";
        log.tradeSpec.noOfLegs=2;
        log.tradeSpec.noOfParties=1;
        log.numOfTrades=10000;
        for (int i = 0; i < log.numOfTrades; i++) {
            TrackLog nl = new TrackLog(log);

            //Trade trade = tradeCreation(context);
            Trade trade = TradeGeneratorHelper.tradeCreation(log.tradeSpec);

            lt.add(trade);

        }

        for(Trade tt : lt){
            System.out.println(tt.getProductType());
            assert(tt.getProductType().toLowerCase().equals("irs"));
        }


        log=new TrackLog();
        log.tradeSpec = new TradeGenerateRequest();
        log.tradeSpec.TradeType="cash-eq";
        log.tradeSpec.noOfLegs=1;
        log.tradeSpec.noOfParties=1;
        log.numOfTrades=10000;
        lt.clear();
        for (int i = 0; i < log.numOfTrades; i++) {
            TrackLog nl = new TrackLog(log);

            //Trade trade = tradeCreation(context);
            Trade trade = TradeGeneratorHelper.tradeCreation(log.tradeSpec);

            lt.add(trade);

        }

        for(Trade tt : lt){
            System.out.println(tt.getProductType());
            assert(tt.getProductType().toLowerCase().equals("cash-equities"));
        }
    }
}
