package com.foods.statediagram;

import java.util.List;
import java.util.Map;

import com.foods.statediagram.validation.ValidationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.foods.statediagram.Exception.NoTransitionFoundException;

public class StateTransitionGraphFactory {
	
	static Logger logger = LoggerFactory.getLogger(StateTransitionGraphFactory.class);
	Map<String, StateTransitionGraph> myStateTransitionMap=null;

	private static ApplicationContext context = null;
	public static StateTransitionGraphFactory myInstance=null;

	Map<String, List<ValidationInterface>> validationMap=null;
	
	
	public static StateTransitionGraphFactory getInstance()  {
		
		if(myInstance==null){
			//double check idiom
			synchronized (StateTransitionGraphFactory.class){
				if(myInstance==null){
					myInstance = new StateTransitionGraphFactory();
				}
			}
		}
		return myInstance;
	}
	
	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("SpringBeanStateDiagram.xml");
		}


		return context;
	}

	


	public Map<String, StateTransitionGraph> getMyStateTransitionMap() {
		return myStateTransitionMap;
	}

	public void setMyStateTransitionMap(Map<String, StateTransitionGraph> myStateTransitionMap) {
		this.myStateTransitionMap = myStateTransitionMap;
	}
	
	public StateTransitionGraphInterface getMyTransitionDiagram(String tradetype) throws NoTransitionFoundException{
		StateTransitionGraphInterface inf = this.myStateTransitionMap.get(tradetype.toLowerCase().trim());
		
		if(inf==null){
			throw new NoTransitionFoundException(tradetype);
		}
		return inf;
	}

	public List<ValidationInterface> getValidationList(String tradetype){
		return this.validationMap.get(tradetype);
	}

	public Map<String, List<ValidationInterface>> getValidationMap() {
		return validationMap;
	}

	public void setValidationMap(Map<String, List<ValidationInterface>> validationMap) {
		this.validationMap = validationMap;
	}
}
