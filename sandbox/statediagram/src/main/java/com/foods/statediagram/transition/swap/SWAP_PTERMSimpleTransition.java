package com.foods.statediagram.transition.swap;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.foods.statediagram.transition.AbstractTransitionHandler;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SWAP_PTERMSimpleTransition extends AbstractTransitionHandler {
    static Logger logger = LoggerFactory.getLogger(SWAP_PTERMSimpleTransition.class);


    @Override
    public Trade process(String action, Trade trade) throws NoTransitionFoundException,NoTradeEventFoundException
            ,IncorrectTransitionforState,TradeValidationException {
        // TODO Auto-generated method stub



        return super.process(action, trade);
    }

}
