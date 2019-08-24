package com.foods.grpc.test;

import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TestFailureMessage {

    @Test
    public void test()throws Exception{

        ApplicationContext context =  new ClassPathXmlApplicationContext("SpringBeanStorageGrpcService.xml");

        String filePath="/Users/dexter/sandbox/fo-ods/sandbox/tradecore-lcm/failExample/createFail.json";
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Trade t = GsonWrapper.fromJson(contentBuilder.toString(),Trade.class);

        TradeCoreStoreService storeservice = context.getBean("TradeCoreStoreService", TradeCoreStoreService.class);


        TradeCoreStoreResponse res=storeservice.createTrade(t);
        System.out.println(res.getTrade().getTradeId());
    }

}
