package com.maplequad.fo.ods.tradecore.lcm.utils.pubsub;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

public class PlainConsumer implements Runnable {
	private final BlockingQueue queue;
	private static final Logger LOGGER = LoggerFactory.getLogger(PlainConsumer.class);

	private SimpleTaskHandler taskHandle;
	private TaskExecutor taskExecutor;

	public PlainConsumer(BlockingQueue q, SimpleTaskHandler h,TaskExecutor tt) {
		queue = q;
		taskHandle = h;
		this.taskExecutor=tt;
	}

	

	@Override
	public void run() {
		try {
			while (true) {

				taskHandle.consumetask(queue.take());

			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			LOGGER.error(ex.getMessage());
		}
	}
	
	public void start(){
		taskExecutor.execute(this);
	
	}

}
