package com.foods.tradecreation.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.client.service.TradeGenerator;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.lcm.processor.DLTMessage;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;


public class TestTradeCreation {

/*
    @Test
    public void test()throws Exception{
        EquityTradeGenerator gen=TradeGeneratorHelper.getInstance("cash-eq");

        Trade t=gen.createTrade(1,1);

        Gson g = new Gson();

        String jsonstr = g.toJson(t);

        System.out.println(jsonstr);


        Gson gg = new GsonBuilder().registerTypeAdapter(ITradeLeg.class, new IdInstanceCreator()).create();
        Trade tt = gg.fromJson(jsonstr,Trade.class);

        String jsonstr2 = g.toJson(tt);
        System.out.println(jsonstr2);
        assert (t!=null);
        assert(t.getTradeEventList()!=null);
        assert(jsonstr.equals(jsonstr2));
    }*/

    @Test
    public void test2() throws Exception{
        TradeGenerator gen=TradeGeneratorHelper.getInstance("fi-irs");

        Trade t=gen.createTrade(2,1);


        DLTMessage dltmessage = new DLTMessage();
        dltmessage.trade=t;
        dltmessage.log=new LCTrackLog();
        String jsonStr = GsonWrapper.toJson(dltmessage);
        System.out.println(jsonStr);
        DLTMessage req = GsonWrapper.fromJson(jsonStr, DLTMessage.class);

        String ttJsonStr=GsonWrapper.toJson(req);
        System.out.println(ttJsonStr);
        DLTMessage tt = GsonWrapper.fromJson(ttJsonStr, DLTMessage.class);

        String jsonStr2=GsonWrapper.toJson(tt);

        System.out.println(jsonStr2);
        assert(jsonStr.equals(jsonStr2));





    }

    @Test
    public void test3() throws Exception{
        Fruit apple = new Fruit("apple");
        String a=GsonWrapper.toJson(apple);
        Fruit t = GsonWrapper.fromJson(a,Fruit.class);
        String b = GsonWrapper.toJson(t);
        assert(a.equals(b));
    }

    class IdInstanceCreator implements InstanceCreator<ITradeLeg> {
        public ITradeLeg createInstance(Type type) {
            return new Equity();
        }
    }


    class Fruit{
        public Fruit(String n){
            this.name=n;
        }
        public String name;
    }


}
