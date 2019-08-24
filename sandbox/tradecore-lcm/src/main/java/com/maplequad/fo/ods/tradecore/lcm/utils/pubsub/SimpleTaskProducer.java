package com.maplequad.fo.ods.tradecore.lcm.utils.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

public class SimpleTaskProducer implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTaskProducer.class);
	
	protected PlainPublisher pp=null;
	protected TaskExecutor taskExecutor;
	protected int mydelay;
	public SimpleTaskProducer(PlainPublisher p,TaskExecutor tt,int d){
		pp=p;
		this.taskExecutor=tt;
		this.mydelay=d;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = 0;
		while (i<10) {
			String Message = "m" + i;
			LOGGER.info("I start producing:"+Message);
			pp.putQueue(Message);
			LOGGER.info("I finish producing:"+Message);
			try{
			Thread.sleep(this.mydelay);
			i++;
			}catch(Exception e){
			}
		}
	}
	
	public void start(){
		taskExecutor.execute(this);
	
	}
}
