package com.thread.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.PlainConsumer;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.SimpleTaskProducer;

public class TestPublishConsumerBlockQueue {

	@Test
	public void test(){
		
		System.out.println("Hello! Java pubsub test");
		
		ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeanTest.xml");
		
		SimpleTaskProducer producer=context.getBean("SimpleTaskProducer",SimpleTaskProducer.class);
		
		
		PlainConsumer consumer = context.getBean("PlainConsumer",PlainConsumer.class);
		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

		producer.start();
		consumer.start();
	
		
		
		for (;;) {
			int count = taskExecutor.getActiveCount();
			System.out.println("Active Threads : " + count);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count == 0) {
				taskExecutor.shutdown();
				break;
			}
		}
		
	}
	
	
	
}
