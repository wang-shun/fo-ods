package com.maplequad.fo.ods.tradecore.client.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;

public class GsonWrapper {

    private static final RuntimeTypeAdapterFactory<ITradeLeg> typeFactory = RuntimeTypeAdapterFactory
        .of(ITradeLeg.class, "type")
        .registerSubtype(Equity.class, ProductType.CASH_EQUITY)
        .registerSubtype(Ird.class, ProductType.IR_SWAP)
        .registerSubtype(Fxd.class, ProductType.FX_FORWARD);

    private static Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();

    private GsonWrapper() {}

    public static String toJson(Object o) {
        return gson.toJson(o);
    }


    public static <T> T fromJson(String json, Class<T> classOfT) {
        T obj = gson.fromJson(json, classOfT);
        return obj;
    }

}
