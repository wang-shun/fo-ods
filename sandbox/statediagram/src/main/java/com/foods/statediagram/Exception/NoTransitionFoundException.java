package com.foods.statediagram.Exception;

public class NoTransitionFoundException  extends Exception{
	String from;
	String action;
	String tradetype;
	
	public NoTransitionFoundException(String tradetype){
		this(tradetype,null,null);
	}
	
	public NoTransitionFoundException(String tradetype,String from, String action){
		this.from=from;
		
		this.action=action;
		this.tradetype=tradetype;
	}
	public NoTransitionFoundException(){}
	
	
	@Override
	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		sb.append("Failed to get transition:");
		sb.append(" tradetype:"+tradetype);
		if(from!=null)
		sb.append(" from:"+from);
		if(action!=null)
		sb.append(" action:"+action);
		return sb.toString();
	}
}
