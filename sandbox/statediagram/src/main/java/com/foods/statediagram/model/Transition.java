package com.foods.statediagram.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.foods.statediagram.transition.AbstractTransitionHandler;

public class Transition implements ApplicationContextAware{


	/*
 * {
			"from":"NEW",
			"to":"VER",
			"action":"CREATE",
			"validator":"NEW2VERSimpleTransition"
		}
 * 
 */
	public String from;
	public String to;
	public String action;
	public AbstractTransitionHandler handler;
	
	public final String getKey(){
		return from+","+action;
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

	public AbstractTransitionHandler getHandler() {
		return handler;
	}

	public void setHandler(AbstractTransitionHandler handler) {
		this.handler = handler;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getKey() + ":" +this.handler.getClass().getName();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.handler.setFrom(from);
		this.handler.setTo(to);
		this.handler.setAction(action);
	}
}
