package com.foods.lifeloader.lifeloaderdummy.Publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foods.lifeloader.lifeloaderdummy.Operator.TradeHelper;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.SimpleTaskHandler;


public class PubSubShooter extends SimpleTaskHandler implements ApplicationContextAware {
	ApplicationContext context=null;
	private static final Logger LOGGER = LoggerFactory.getLogger(PubSubShooter.class);
	MessagePublisher publisher;
	//ObjectMapper mapper = new ObjectMapper();
	
	public String topicid="";
	int mydelay = 100;
/*
	public PubSubShooter(int delay) throws Exception {
		super(delay);
		topicid = System.getenv("PUBSUB_TOPIC");
		LOGGER.info("My topic is" + topicid);
		
	}*/

	public PubSubShooter(String topicId) throws Exception {
		super(0);
		this.topicid=topicId;
		LOGGER.info("My topic is" + topicid);
	}
/*
	public PubSubShooter() throws Exception {
		super(0);
		topicid = System.getenv("PUBSUB_TOPIC");
		LOGGER.info("My topic is" + topicid);
	}*/
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		this.context=arg0;
		try{
			this.prepareTopic(this.topicid);
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void prepareTopic(String topicId) throws Exception {
		LOGGER.info("Preparing publisher: {}", publisher);
		publisher.init(topicId);
	}

	public void consumetask(Object t)  {
		long start = System.currentTimeMillis();
		TrackLog trackLog = (TrackLog)t;
		try {
			TradeHelper.generateTradeOperation(context, publisher, trackLog, 1);
			long elapsed = System.currentTimeMillis() - start;
			LOGGER.info("Published {} trades to {}, serial={}, elapsed={} s", trackLog.numOfTrades, topicid,
					trackLog.serialNumber, (elapsed / 1000.0));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public MessagePublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(MessagePublisher publisher) {
		this.publisher = publisher;
	}
}
