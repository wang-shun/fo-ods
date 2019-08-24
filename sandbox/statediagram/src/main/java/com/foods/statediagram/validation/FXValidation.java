package com.foods.statediagram.validation;

import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;

import java.util.List;

public class FXValidation implements ValidationInterface {

    @Override
    public void validate(Trade t) throws TradeValidationException {

//        String TradeType=t.getProductType();
//        if(!t.getPrimaryAssetClass().equals("fxd")){
//            throw new TradeValidationException("Not a FX type");
//        }
        TradeEvent et = t.getTradeEventList().get(0);
        List<ITradeLeg> legs= et.getTradeLegList();
        for(ITradeLeg l : legs){
            if(l instanceof Fxd) {
                Fxd fxd = (Fxd)l;
                if(fxd.getBaseCcy().equals(fxd.getCounterCcy())){
                    throw new TradeValidationException("ccy and counterccy cannot be equal");
                }
                if(fxd.getSpotRate()<0){
                    throw new TradeValidationException("Spot rate cannot < 0");
                }
                if(fxd.getAllInRate()<0){
                    throw new TradeValidationException("AllinRate rate cannot < 0");
                }
            }
        }


    }
}
