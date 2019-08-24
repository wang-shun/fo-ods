package com.foods.lifeloader.lifeloaderdummy.Publisher;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubPublisher implements MessagePublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(PubSubPublisher.class);

	private Publisher publisher;

	@Override
	public ApiFuture<String> publish(PubsubMessage message) {
		return publisher.publish(message);
	}

	@Override
	public void init(String topicName) {
		LOGGER.info("Starting publisher for {}", topicName);
		try {
			// TopicName topicName = TopicName.create(projectid, topicid);
			ExecutorProvider pubExecutor = InstantiatingExecutorProvider.newBuilder().build();

			publisher = Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topicName))
					.setExecutorProvider(pubExecutor)
					.build();
			LOGGER.info("Started publisher: {}", publisher);
			LOGGER.info("Batching settings: {}", publisher.getBatchingSettings());

		} catch (Exception e) {
			throw new RuntimeException("Server failed to get topic from env variable: 'PUBSUB_TOPIC'", e);
		}

	}
}
