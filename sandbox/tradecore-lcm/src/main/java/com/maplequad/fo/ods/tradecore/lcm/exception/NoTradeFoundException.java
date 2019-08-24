package com.maplequad.fo.ods.tradecore.lcm.exception;

public class NoTradeFoundException extends Exception {
	String msg;
	
	public NoTradeFoundException(){
		super("No trade found exception");
	}
	
	public NoTradeFoundException(String ostradeid){
		super("No trade found exception with ostradeid:"+ostradeid);
	}
	
	
}
