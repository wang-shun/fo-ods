package com.foods.statediagram.transition.swap;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.foods.statediagram.transition.AbstractTransitionHandler;
import com.foods.statediagram.transition.AmendSimpleTransitionHandler;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;

import java.util.Date;
import java.util.List;

public class SWAP_FTERMSimpleTransition extends AbstractTransitionHandler {

    static Logger logger = LoggerFactory.getLogger(SWAP_FTERMSimpleTransition.class);


    @Override
    public Trade process(String action, Trade trade) throws NoTransitionFoundException,NoTradeEventFoundException
            ,IncorrectTransitionforState,TradeValidationException {
        // TODO Auto-generated method stub
        if(!trade.getProductType().equals("irs")){
            throw new IncorrectTransitionforState("Wrong mapping of swap transition");
        }
        Trade t=super.process(action, trade);
        //Change maturity date to today
        List<ITradeLeg> lt= t.getTradeEventList().get(0).getTradeLegList();

        for(ITradeLeg leg: lt){
            Ird ird = (Ird)leg;
            ird.setMaturityDate(new Date());
        }

        return t;
    }
}
