package com.foods.statediagram.transition;

import com.foods.statediagram.Exception.TradeValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foods.statediagram.STATE;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;

public class DONE2VERSimpleTransitionHandler extends AbstractTransitionHandler {
	static Logger logger = LoggerFactory.getLogger(DONE2VERSimpleTransitionHandler.class);

	@Override
	public Trade process(String action, Trade trade)
			throws NoTransitionFoundException, NoTradeEventFoundException, IncorrectTransitionforState,TradeValidationException {
		return super.process(action, trade);
	}

}
