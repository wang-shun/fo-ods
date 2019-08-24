package com.maplequad.fo.ods.tradecore.bridge.pubsub;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.api.services.pubsub.model.Subscription;
import com.google.api.services.pubsub.model.Topic;
import com.google.common.base.Preconditions;
import com.maplequad.fo.ods.tradecore.bridge.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/***
 * PubSubHelper
 *
 * This is the class that helps to check and create topic and subscriptions with Google Pub Sub .
 *
 * @author Madhav Mindhe
 * @since :   07/09/2017
 */
public class PubSubHelper {

    private final static Logger LOGGER = LoggerFactory.getLogger(PubSubHelper.class);

    public Pubsub getClient() throws IOException {
        return getClient(Utils.getDefaultTransport(),
                Utils.getDefaultJsonFactory());
    }

    /**
     * Builds a new Pubsub client and returns it.
     *
     * @param httpTransport HttpTransport for Pubsub client.
     * @param jsonFactory   JsonFactory for Pubsub client.
     * @return Pubsub client.
     * @throws IOException when we can not get the default credentials.
     */
    public Pubsub getClient(final HttpTransport httpTransport, final JsonFactory jsonFactory)
            throws IOException {
        Preconditions.checkNotNull(httpTransport);
        Preconditions.checkNotNull(jsonFactory);

        GoogleCredential credential = null;
        if (SysEnv.GOOGLE_APPLICATION_CREDENTIALS != null) {
            final String keyFile = SysEnv.GOOGLE_APPLICATION_CREDENTIALS;
            final Path path = Paths.get(keyFile);
            credential = GoogleCredential.fromStream(Files.newInputStream(path));
        } else {
            credential = GoogleCredential.getApplicationDefault(httpTransport, jsonFactory);
        }

        if (credential.createScopedRequired()) {
            credential = credential.createScoped(PubsubScopes.all());
        }
        // Please use custom HttpRequestInitializer for automatic retry upon failures.
        HttpRequestInitializer initializer = new RetryHttpInitializerWrapper(credential);

        return new Pubsub.Builder(httpTransport,
                jsonFactory,
                initializer)
                .setApplicationName(SysEnv.APP_ID)
                .build();
    }

    /**
     * This method checks if the provided topic exists already.
     *
     * @param client
     * @param topicName
     * @return
     */
    public boolean checkTopicExists(final Pubsub client, final String topicName) {
        LOGGER.info("checkTopicExists : checking if topic {} already exists",topicName);
        boolean exists = false;
        Topic topic = null;
        try {
            topic = client.projects()
                    .topics()
                    .get(topicName)
                    .execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (topic != null) {
            exists = true;
            LOGGER.info("Topic: " + topic.getName() + " is already available.");
        }
        return exists;
    }

    /**
     * This method checks if the provided subscription exists already.
     *
     * @param client
     * @param subscriptionName
     * @return
     */
    public boolean checkSubscriptionExists(final Pubsub client, final String subscriptionName) {
    LOGGER.info("checkSubscriptionExists : checking if subscription {} already exists",subscriptionName);
        boolean exists = false;

        Subscription subscription = null;
        try {
            subscription = client.projects()
                    .subscriptions()
                    .get(subscriptionName)
                    .execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        if (subscription != null) {
            exists = true;
            LOGGER.info("Subscription: " + subscription.getName() + " is already available.");
        }
        return exists;
    }

    /**
     * This method creates a Topic with provided name.
     *
     * @param client
     * @param topicName
     * @throws IOException
     */
    public void createTopic(final Pubsub client, final String topicName)
            throws IOException {
        LOGGER.info("createTopic : creating the topic {}",topicName);
        Topic topic = client.projects()
                .topics()
                .create(topicName, new Topic())
                .execute();
        LOGGER.info("Topic: " + topic.getName() + " is now created.");
    }

    /**
     * This method creates a Subscription with provided name on a given topic.
     *
     * @param client
     * @param topicName
     * @param subscriptionName
     * @throws IOException
     */
    public void createSubscription(final Pubsub client, final String topicName, final String subscriptionName)
            throws IOException {
        LOGGER.info("createSubscription : creating the subscription {} on topic {}",subscriptionName, topicName);
        Subscription subscription =
                new Subscription().setTopic(topicName).setAckDeadlineSeconds(SysEnv.ACK_DEADLINE_IN_SECONDS);

        subscription =
                client.projects().subscriptions()
                        .create(subscriptionName, subscription)
                        .execute();

        LOGGER.info("Subscription: " + subscription.getName() + " is now created.");
    }
}