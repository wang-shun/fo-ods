package com.foods.lifeloader.lifeloaderdummy.Publisher;

import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.testing.FakeOperationApi;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.PubsubMessage;

public class MockPublisher implements MessagePublisher {

	private final ApiFuture<String> result = new FakeOperationApi.FakeOperationFuture("result", "operation");

	@Override
	public ApiFuture<String> publish(PubsubMessage message) {
		return result;
	}

	@Override
	public void init(String topicName) {
	}
}
