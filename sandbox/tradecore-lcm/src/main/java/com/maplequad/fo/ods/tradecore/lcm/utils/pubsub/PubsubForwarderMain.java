package com.maplequad.fo.ods.tradecore.lcm.utils.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.lcm.model.PubSubHeaderHelper;
import com.maplequad.fo.ods.tradecore.lcm.subscriber.PullLifeLoaderPubSubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import java.util.Base64;
import java.util.concurrent.BlockingQueue;

public class PubsubForwarderMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(PubsubForwarderMain.class);

	public static void main(String[] args) throws Exception {

		String subscription = args[0];
		int nThreads = Integer.parseInt(args[1]);
		String topic = args[2];

		LOGGER.info("Starting subscription");
		SubscriptionName subscriptionName = SubscriptionName.create(ServiceOptions.getDefaultProjectId(), subscription);

		// create a subscriber bound to the asynchronous message receiver
		ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder()
				.setExecutorThreadCount(nThreads).build();
		Long maxMemory = null;//Runtime.getRuntime().maxMemory() * 20 / 100L;
		Long maxElements = 500L;
		FlowControlSettings flowSettings = FlowControlSettings.newBuilder()
				.setMaxOutstandingRequestBytes(maxMemory)
				.setMaxOutstandingElementCount(maxElements)
				.build();
		LOGGER.info("Flow control settings: maxMemory={}, maxElements={}, settings={}", maxMemory, maxElements, flowSettings);

		Publisher publisher = Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topic)).build();

		Subscriber subscriber = Subscriber.defaultBuilder(subscriptionName, new MessageReceiverImpl(publisher))
				.setExecutorProvider(executorProvider)
				.setFlowControlSettings(flowSettings)
				.build();
		subscriber.startAsync().awaitTerminated();

	}

	static class MessageReceiverImpl implements MessageReceiver {


		private final Publisher publisher;

		public MessageReceiverImpl(Publisher publisher) {
			this.publisher = publisher;
		}

		@Override
		public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
			LOGGER.warn("Received messageId: {}", message.getMessageId());

			try {
				String jsonStr = message.getData().toStringUtf8();

				PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(jsonStr)).build();
				ApiFuture<String> messageId = publisher.publish(pubsubMessage);
				messageId.get();

			} catch (Exception e) {
				LOGGER.error("Cannot process trade", e);

			} finally {
				consumer.ack();

			}
		}

		private String decode(String data) {
			return new String(Base64.getDecoder().decode(data));
		}
	}
}
