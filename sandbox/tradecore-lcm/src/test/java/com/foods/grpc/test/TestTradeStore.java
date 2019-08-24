package com.foods.grpc.test;

import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestTradeStore {


    @Test
    public void test() throws Exception{
        ApplicationContext context =  new ClassPathXmlApplicationContext("SpringBeanStorageGrpcService.xml");

        TradeGenerator gen= TradeGeneratorHelper.getInstance("fi-irs");

        Trade t=gen.createTrade(1,1);

        TradeCoreStoreService storeservice = context.getBean("TradeCoreStoreService", TradeCoreStoreService.class);


        TradeCoreStoreResponse res=storeservice.createTrade(t);
        System.out.println(res.getTrade().getTradeId());

    }
}
