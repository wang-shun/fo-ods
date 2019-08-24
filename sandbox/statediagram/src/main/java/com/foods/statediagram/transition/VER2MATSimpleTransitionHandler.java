package com.foods.statediagram.transition;

import java.util.Date;

import com.foods.statediagram.Exception.TradeValidationException;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foods.statediagram.STATE;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;

public class VER2MATSimpleTransitionHandler extends AbstractTransitionHandler{
	static Logger logger = LoggerFactory.getLogger(VER2MATSimpleTransitionHandler.class);
	
	@Override
	public Trade process(String action, Trade trade) throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,TradeValidationException {
		// TODO Auto-generated method stub
		
		
		TradeEvent et = this.getLatestEvent(trade);
		
		
		if (from.equals(STATE.MAT)) {
			throw new IncorrectTransitionforState();
		}
		
		if (!action.equals(STATE.ACTION.MATURE)) {
			throw new IncorrectTransitionforState();
		}
		logger.debug("Running:" + this.getClass().getName()+trade.getTradeId()+" with transition:"+from+" - "+action+" -> "+to);
		/*		et.getTradeLegList().forEach(
				(L) ->{
					IR_SWAP ird = (IR_SWAP) L;
					ird.setSettlementDate(new Date());
				}
		);*/
		
		return super.process(action,trade);
	}

}
