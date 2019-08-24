package com.foods.statediagram.validation;

import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;

import java.util.List;

public class BasicValidation implements ValidationInterface{

    @Override
    public void validate(Trade t) throws TradeValidationException {
        String TradeType=t.getProductType();

        TradeEvent et = t.getTradeEventList().get(0);

        if(et.getTradeLegList().size()==0){
            throw new TradeValidationException("No trade leg defined");
        }
        if(et.getTradePartyList().size()==0){
            throw new TradeValidationException("No customer defined");
        }

/*        if(et.isLockedFlag()){
            throw new TradeValidationException("Trade is locked for bulk action");
        }*/

        et.setNoOfLegs(et.getTradeLegList().size());

        List<ITradeLeg> legs= et.getTradeLegList();
        for(ITradeLeg l : legs){
            if(l instanceof TradeLeg){
                TradeLeg leg = (TradeLeg)l;
                if(leg.getBook()==null || leg.getBook().length()==0) {
                    throw new TradeValidationException("No book defined");
                }
            }
        }

    }
}
