package com.foods.statediagram;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.foods.statediagram.Exception.TradeValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.model.Transition;
import com.foods.statediagram.transition.AbstractTransitionHandler;
import com.foods.statediagram.transition.TransitionHandlerInterface;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;

public class StateTransitionGraph implements ApplicationContextAware ,StateTransitionGraphInterface,InitializingBean{
	static Logger logger = LoggerFactory.getLogger(StateTransitionGraph.class);

	private StateTransitionGraph copyfrom=null;
	private Set<String> states = new HashSet<String>();
	private Set<String> actions = new HashSet<String>();
	private String name;

	private Map<String, Transition> transitionMap = new HashMap<String, Transition>();
	
	private List<Transition> transitionList = new LinkedList<Transition>();

	protected StateTransitionGraph(){
	}
	StateTransitionGraphFactory myFactory=null;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
		if(this.copyfrom!=null){
			for(String s : copyfrom.states){
				this.states.add(s);
			}
			for(String a : copyfrom.actions){
				this.actions.add(a);
			}
			for(Transition t : copyfrom.transitionList){
				if(!this.transitionMap.containsKey(t.getKey())){
					this.transitionMap.put(t.getKey(), t);
				}
			}
		}
		
		for (Transition t : this.transitionList){
			logger.info(t.toString());
			String key = t.getKey();
			this.transitionMap.put(key, t);
		}
		
	}



	@Override
	public void setApplicationContext(ApplicationContext mycontext) throws BeansException {
		myFactory =mycontext.getBean("StateTransitionGraphFactory",StateTransitionGraphFactory.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public Set<String> getActions() {
		return actions;
	}

	public void setActions(Set<String> actions) {
		this.actions = actions;
	}



	public List<Transition> getTransitionList() {
		return transitionList;
	}



	public void setTransitionList(List<Transition> transitionList) {
		this.transitionList = transitionList;
	}



	public Set<String> getStates() {
		return states;
	}



	public void setStates(Set<String> states) {
		this.states = states;
	}

	public Map<String, Transition> getTransitionMap() {
		return transitionMap;
	}

	public void setTransitionMap(Map<String, Transition> transitionMap) {
		this.transitionMap = transitionMap;
	}

	public StateTransitionGraph getCopyfrom() {
		return copyfrom;
	}

	public void setCopyfrom(StateTransitionGraph copyfrom) {
		this.copyfrom = copyfrom;
	}

	@Override
	public Trade transitionState(Trade trade,String tradetype, String action)throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,TradeValidationException {
		// TODO Auto-generated method stub
		TradeEvent et;
		try{
		 et = AbstractTransitionHandler.getLatestEvent(trade);
		}catch(Exception e){throw new NoTransitionFoundException();}
		Transition t = new Transition();
		Transition newt;
		t.setFrom(et.getEventStatus().toUpperCase());
		
		t.setAction(action.toUpperCase());
		newt = this.transitionMap.get(t.getKey());
		tradetype = tradetype.toLowerCase();
		if(newt==null){
			logger.error("Fail to find transitionmap for tradetype:{}, status:{}, action{}",tradetype,t.getFrom(),action);
			throw new NoTransitionFoundException(tradetype,t.getFrom(),action);
		}
		
		//run the logic
		TransitionHandlerInterface handler = newt.getHandler();
		if(handler==null){
			NoTransitionFoundException ex= new NoTransitionFoundException(tradetype,newt.getFrom(),action);
			logger.error(ex.getMessage());
			throw ex;
		}
		return handler.process( action, trade);
	}

	
	
}
