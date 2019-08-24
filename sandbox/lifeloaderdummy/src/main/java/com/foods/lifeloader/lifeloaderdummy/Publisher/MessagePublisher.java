package com.foods.lifeloader.lifeloaderdummy.Publisher;

import com.google.api.core.ApiFuture;
import com.google.pubsub.v1.PubsubMessage;

public interface MessagePublisher {
	ApiFuture<String> publish(PubsubMessage message);
	void init(String topicName);
}
