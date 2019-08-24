package com.maplequad.fo.ods.tradecore.lcm.utils.stackdriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.SimpleTaskHandler;

public class StackDriverPublishQueueHandler extends SimpleTaskHandler {
private static final Logger LOGGER = LoggerFactory.getLogger(StackDriverPublishQueueHandler.class);
	
	StackDriverPublishQueueHandler(){
		super(0);
	}
	
	@Override
	public void consumetask(Object t){
		String s = (String)t;
		LOGGER.info("I start consuming:"+s);
		try{
		//Thread.sleep(this.mydelay);
		}catch(Exception e){}
		LOGGER.info("I finish consuming:"+s);
	}
}
