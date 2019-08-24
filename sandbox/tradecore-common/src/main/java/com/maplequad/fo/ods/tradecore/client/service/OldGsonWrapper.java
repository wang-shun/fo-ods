package com.maplequad.fo.ods.tradecore.client.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;
import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class OldGsonWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(OldGsonWrapper.class);


    //

    public OldGsonWrapper() {

    }

    final public String toJson(Object o) {
        Gson g;
        g = new GsonBuilder().registerTypeAdapter(ITradeLeg.class, new DummyInstanceCreator()).create();
        return g.toJson(o);
    }


    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        Gson g;
        g = new GsonBuilder().registerTypeAdapter(ITradeLeg.class, new DummyInstanceCreator()).create();
        T obj = g.fromJson(json, classOfT);

        try {
            if (obj instanceof Trade) {
                Trade t = (Trade) obj;
                JSONParser parser = new JSONParser();
                JSONObject jobj = (JSONObject) parser.parse(json);
                JSONArray msg = (JSONArray) jobj.get("tradeEventList");
                if (msg == null) {
                    return obj;
                }

                for (int eventNum = 0; eventNum < msg.size(); eventNum++) {
                    //Event loop
                    JSONObject event = (JSONObject) msg.get(eventNum);
                    //Try to loop to tradeLegList
                    JSONArray legs = (JSONArray) event.get("tradeLegList");
                    if (legs == null) {
                        continue;
                    }
                    t.getTradeEventList().get(eventNum).getTradeLegList().clear();
                    for (int nleg = 0; nleg < legs.size(); nleg++) {
                        JSONObject jleg = (JSONObject) legs.get(nleg);
                        //Check trade type --- legType
                        String legType = (String) jleg.get("legType");
                        if (legType == null) {
                            continue;
                        }
                        LOGGER.debug("legs" + nleg + " " + legType.toString());

                        String strLeg = jleg.toJSONString();
                        //System.out.println(strLeg);
                        TradeLeg realLeg = null;
                        if (legType.equals(ProductType.CASH_EQUITY)) {
                            realLeg = g.fromJson(strLeg, Equity.class);
                        } else if (legType.equals(ProductType.IR_SWAP)) {
                            realLeg = g.fromJson(strLeg, Ird.class);
                        } else if (legType.equals(ProductType.FX_FORWARD)) {
                            realLeg = g.fromJson(strLeg, Fxd.class);
                        }
                        t.getTradeEventList().get(eventNum).getTradeLegList().add(realLeg);
                    }
                }
            }
        } catch (ParseException p) {
            LOGGER.error(p.getMessage());
            LOGGER.error("Faulty msg:{}", json);
            p.printStackTrace();
            throw new JsonSyntaxException(p.getMessage());
        }
        return obj;
    }


    private class DummyInstanceCreator implements InstanceCreator<ITradeLeg> {
        public ITradeLeg createInstance(Type type) {
            return new DummyTradeLeg();
        }
    }

    final private class DummyTradeLeg implements ITradeLeg {
    }

}