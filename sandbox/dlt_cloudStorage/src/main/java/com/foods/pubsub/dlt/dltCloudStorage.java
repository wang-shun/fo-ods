package com.foods.pubsub.dlt;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.foods.lifeloader.lifeloadercreator.processor.LCTrackLog;
import com.foods.lifeloaderutility.CloudStorageHelper;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;

public class dltCloudStorage {
	private static final Logger LOGGER = LoggerFactory.getLogger(dltCloudStorage.class);
	// use the default project id
	private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

	static ApplicationContext context=null;
	private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

	static Gson g = new Gson();
	static class MessageReceiverExample implements MessageReceiver {

		@Override
		public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
			messages.offer(message);
			consumer.ack();
		}
	}

	public static void startLoading(String subscriptionId) throws Exception {
		context = new ClassPathXmlApplicationContext("SpringBean.xml");
		System.out.println("Subscribe to :" + subscriptionId);
		SubscriptionName subscriptionName = SubscriptionName.create(PROJECT_ID, subscriptionId);
		Subscriber subscriber = null;
		try {
			// create a subscriber bound to the asynchronous message receiver
			subscriber = Subscriber.defaultBuilder(subscriptionName, new MessageReceiverExample()).build();
			subscriber.startAsync().awaitRunning();
			// Continue to listen to messages
			while (true) {
				PubsubMessage message = messages.take();
				LOGGER.info("Message Id: {}" , message.getMessageId());
				String dataStr=message.getData().toStringUtf8();
				
				DLTMessage msg = g.fromJson(dataStr, DLTMessage.class);
				
				
				LCTrackLog l = msg.log;
				
				String mRaw = g.toJson(l);
				
				Map<String, String> map =  msg.LogtoMap();
				String csvout=CSAMapWriter.writeCSV(map);
				
				
				long serialnum =l.serialNumber;
				int orderNUM=l.subnumber;
				
				try {
					CloudStorageHelper helper = context.getBean("CloudStorageHelper", CloudStorageHelper.class);
					String filePath = CloudStorageHelper.adviseFileName("dltLayer", l.action, String.valueOf(serialnum),
							orderNUM, "json");
					LOGGER.info("Begin to write Track Log Json link");
					String link = helper.saveObject2Jason(l, filePath);
					LOGGER.info("End to write Track Log Json link:{}" , link);
					
					filePath = CloudStorageHelper.adviseFileName("dltLayer", l.action, String.valueOf(serialnum),
							orderNUM, "csv");
					LOGGER.info("Begin to write Track Log csv link");
					link = helper.saveTextObject(csvout, filePath);
					LOGGER.info("End to write Track Log csv link:{}" , link);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
			}
		} finally {
			if (subscriber != null) {
				subscriber.stopAsync();
			}
		}

	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// set subscriber id, eg. my-sub
		String subscriptionId = args[0];

		//testCSV();

		startLoading(subscriptionId);
	}

	static void testCSV() throws Exception {

		LCTrackLog req = new LCTrackLog();
		req.ULT_requestTimestamp = 100;
		req.LC_arrivalTime = 101;
		req.LC_CRUDStartTime = 103;
		req.DB_StartTime = 106;
		req.DB_EndTime = 110;
		req.LC_CRUDEndTime = 115;
		req.tradeid = "tradeId-" + System.currentTimeMillis();
		req.osTradeid = "osTradeId";
		req.action = "action";
		req.calculate();
		DLTMessage m = new DLTMessage();
		m.log=req;
		m.trade=null;
		
		
		Map<String, String> map = m.LogtoMap();
		
		String csvout=CSAMapWriter.writeCSV(map);
		System.out.println(csvout);
		
	}

	
	

}
