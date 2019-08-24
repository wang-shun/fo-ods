package com.foods.statediagram.validation;

import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;

import java.util.List;

public class IRValidation implements ValidationInterface {

    @Override
    public void validate(Trade t) throws TradeValidationException {


        TradeEvent et = t.getTradeEventList().get(0);
        List<ITradeLeg> legs= et.getTradeLegList();
        for(ITradeLeg l : legs){
            if(l instanceof Ird) {
                Ird asset = (Ird)l;
                if(asset.getIndex()==null || asset.getIndex().length()==0){
                    throw new TradeValidationException("Index cannot be null");
                }
            }
        }

    }
}
