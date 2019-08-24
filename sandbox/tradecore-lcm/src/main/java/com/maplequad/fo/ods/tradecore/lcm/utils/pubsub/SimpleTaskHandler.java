package com.maplequad.fo.ods.tradecore.lcm.utils.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTaskHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTaskHandler.class);
	
	int mydelay=100;
	public SimpleTaskHandler(int delay){
		this.mydelay=delay;
	}
	
	public void consumetask(Object t){
		String s = (String)t;
		LOGGER.info("I start consuming:"+s);
		try{
		Thread.sleep(this.mydelay);
		}catch(Exception e){}
		LOGGER.info("I finish consuming:"+s);
	}
}
