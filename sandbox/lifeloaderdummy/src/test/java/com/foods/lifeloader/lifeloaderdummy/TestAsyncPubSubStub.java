package com.foods.lifeloader.lifeloaderdummy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.foods.lifeloader.lifeloaderdummy.Publisher.TrackLog;
import com.foods.lifeloaderutility.CreateUUIDHelper;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.PlainConsumer;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.PlainPublisher;

public class TestAsyncPubSubStub {
	public static void main(String args[]) {
		ApplicationContext context = new ClassPathXmlApplicationContext("SpringBeanTest.xml");

		// PubSubShooter
		// shooter=context.getBean("PubSubShooter",PubSubShooter.class);
		TrackLog r = new TrackLog();
		r.serialNumber = String.valueOf(CreateUUIDHelper.createUUID());
		r.numOfTrades = 1000;

		PlainPublisher publisher = context.getBean("PlainPublisher", PlainPublisher.class);
		PlainConsumer consumer = context.getBean("PlainConsumer", PlainConsumer.class);
		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

		consumer.start();
		publisher.putQueue(r);

		for (;;) {
			int count = taskExecutor.getActiveCount();
			//System.out.println("Active Threads : " + count);
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
