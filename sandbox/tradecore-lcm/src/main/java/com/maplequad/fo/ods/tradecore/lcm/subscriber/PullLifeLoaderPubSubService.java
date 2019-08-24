package com.maplequad.fo.ods.tradecore.lcm.subscriber;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.google.api.gax.batching.FlowControlSettings;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maplequad.fo.ods.tradecore.lcm.processor.TradeProcessorInterface;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.google.gson.JsonParser;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import sun.util.resources.cldr.ff.LocaleNames_ff;

public class PullLifeLoaderPubSubService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PullLifeLoaderPubSubService.class);

    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

    private final JsonParser jsonParser = new JsonParser();

    ApplicationContext context = null;

    TradeProcessorInterface myProcessor = null;

    // TradeConverterService myConvertor=null;

    String subscriptionId = null;

    public PullLifeLoaderPubSubService() throws Exception {

        subscriptionId = System.getenv("ULT_SUB_ID");
        LOGGER.info("Subscribing to {}", subscriptionId);
        prepareToken(subscriptionId);
    }

    public PullLifeLoaderPubSubService(String subname) throws Exception {
        subscriptionId = subname;
        prepareToken(subname);
    }

    private void prepareToken(String token) throws Exception {
        context = new ClassPathXmlApplicationContext("SpringBean.xml");
        myProcessor = context.getBean("TradeProcessor", TradeProcessorInterface.class);

        subscriptionId = token;

        if (subscriptionId == null) {
            throw new Exception("Server failed to get pubsub subscriptionid from env variable: 'ULT_SUB_ID'");
        }

    }

    public List<Map<String, String>> processJsonStr(String jsonStr) throws Exception {
        List<Map<String, String>> map = GsonWrapper.fromJson(jsonStr, List.class);
        return map;
    }

    public void start_new(int nThreads) throws Exception {
        LOGGER.info("Starting subscription");
        SubscriptionName subscriptionName = SubscriptionName.create(PROJECT_ID, subscriptionId);
        Subscriber subscriber = null;

        LOGGER.info("Subscribed!");

        // create a subscriber bound to the asynchronous message receiver
        ExecutorProvider executorProvider = InstantiatingExecutorProvider.newBuilder()
                .setExecutorThreadCount(nThreads).build();
        Long maxMemory = null;//Runtime.getRuntime().maxMemory() * 20 / 100L;
        Long maxElements = 1000L;
        FlowControlSettings flowSettings = FlowControlSettings.newBuilder()
            .setMaxOutstandingRequestBytes(maxMemory)
            .setMaxOutstandingElementCount(maxElements)
            .build();
        LOGGER.info("Flow control settings: maxMemory={}, maxElements={}, settings={}", maxMemory, maxElements, flowSettings);

        subscriber = Subscriber.defaultBuilder(subscriptionName, new MessageReceiverOnline())
            .setExecutorProvider(executorProvider)
            .setFlowControlSettings(flowSettings)
            .build();
        subscriber.startAsync().awaitRunning();
    }

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            LOGGER.info("Received message!");
            consumer.ack();
        }
    }

    class MessageReceiverOnline implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            LOGGER.debug("Received message Id: {}", message.getMessageId());

            String jsonStr = "unparsed";

            try {
                jsonStr = message.getData().toStringUtf8();

                myProcessor.processTradeFromMapSingle(jsonStr);       //LOGGER.info("Get Msg: {}",t);

            } catch (NoTransitionFoundException ne) {
                LOGGER.error("Cannot process trade: {}", jsonStr);
                LOGGER.error(ne.getMessage());

                //ne.printStackTrace();
                //process failure queue for technical reason
            } catch (NoTradeEventFoundException te) {
                LOGGER.error("Cannot process trade: {}", jsonStr);
                LOGGER.error(te.getMessage());

                //te.printStackTrace();
                //process failure queue for technical reason
            } catch (TradeValidationException ve) {
                LOGGER.error("Cannot process trade: {}", jsonStr);
                LOGGER.error(ve.getMessage());

                //ve.printStackTrace();
                //process failure queue for technical reason
            } catch (IncorrectTransitionforState s) {
                LOGGER.error("Cannot process trade: {}", jsonStr);
                LOGGER.error(s.getMessage());

                //s.printStackTrace();
                //process failure queue for technical reason
            } catch (Exception e) {
                LOGGER.error("Cannot process trade: {}", jsonStr);
                LOGGER.error(e.getMessage());

            } finally {
                consumer.ack();

            }
        }

        private String decode(String data) {
            return new String(Base64.getDecoder().decode(data));
        }
    }

}
