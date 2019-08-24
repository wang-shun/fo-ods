package com.foods.statediagram;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;

public interface StateTransitionGraphInterface {

	public Trade transitionState(Trade trade,String tradetype,  String action) throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,TradeValidationException;
}
