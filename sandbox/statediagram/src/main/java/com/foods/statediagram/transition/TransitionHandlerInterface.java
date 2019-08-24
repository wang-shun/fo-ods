package com.foods.statediagram.transition;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

public interface TransitionHandlerInterface {
	public Trade process( String action, Trade trade) throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,TradeValidationException;
}
