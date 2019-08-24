package com.foods.statediagram.transition;

import java.util.List;

import com.foods.statediagram.Exception.TradeValidationException;
import com.foods.statediagram.StateTransitionGraphFactory;
import com.foods.statediagram.validation.ValidationInterface;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foods.statediagram.STATE;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;

public abstract class AbstractTransitionHandler implements TransitionHandlerInterface{
	static Logger logger = LoggerFactory.getLogger(AbstractTransitionHandler.class);
	public String from;
	public String to;
	public String action;
	
	
	
	public static TradeEvent getLatestEvent(Trade trade) throws NoTradeEventFoundException{
	List<TradeEvent> tl = trade.getTradeEventList();

		if (tl.size() > 1) {
//			tl.sort(new Comparator<TradeEvent>() {
//				@Override
//				public int compare(TradeEvent o1, TradeEvent o2) {
//					return o2.getValidTimeFrom().getNanos()-o1.getValidTimeFrom().getNanos() ;
//				}
//			});
			tl.sort( (o1,o2) -> o2.getValidTimeFrom().getNanos()-o1.getValidTimeFrom().getNanos() );
		}else if(tl.size() <1 ){
			throw new NoTradeEventFoundException ();
		}
		return tl.get(0);
	}

	protected TradeEvent setTradeLatestValidDate(TradeEvent et) {
		final Timestamp tStamp = Timestamp.now();
		//et.setEventStatus("VER");
		//et.setEventType("CREATE");
		et.setValidTimeFrom(tStamp);
		
		et.getTradeLegList().forEach(
				( l) -> {
					TradeLeg leg = (TradeLeg) l;
					leg.setValidTimeFrom(tStamp);
				}
		);
		et.getTradePartyList().forEach(
				(p) -> {
					p.setValidTimeFrom(tStamp);
				}
		);
		return et;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public Trade process(String action, Trade trade)
			throws NoTransitionFoundException, NoTradeEventFoundException, IncorrectTransitionforState ,TradeValidationException {
		// TODO Auto-generated method stub

		String productType = trade.getProductType();

		List<ValidationInterface> vList=StateTransitionGraphFactory.getInstance().getValidationList(productType);
		if(vList==null){
			throw new TradeValidationException("Trade validator No trade type find:"+productType);
		}
			for(ValidationInterface v: vList) {
					v.validate(trade);
			}

		trade.getTradeEventList().get(0).setEventStatus(this.from);
		String from = this.from;
		String to = this.to;
		
		if(!action.equals(this.action)){
			throw new  IncorrectTransitionforState();
		}
		
		
		logger.debug("Running:" + this.getClass().getName()+trade.getTradeId()+" with transition:"+from+" - "+action+" -> "+to);

		
		TradeEvent et = getLatestEvent(trade);
		this.setTradeLatestValidDate(et);
		et.setEventStatus(this.to);
		et.setEventType(this.action);
		
		return trade;
	}
	
	
	
}
