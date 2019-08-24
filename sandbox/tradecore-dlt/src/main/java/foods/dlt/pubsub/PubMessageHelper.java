package foods.dlt.pubsub;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.lcm.utils.CreateUUIDHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class PubMessageHelper {
    ApplicationContext context=null;

    String topicId;

    private static final Logger LOGGER = LoggerFactory.getLogger(PubMessageHelper.class);
    Publisher publisher = null;
    public PubMessageHelper(String topicId)throws Exception{
        this.topicId=topicId;
        prepareTopic( topicId);
    }

    protected void prepareTopic(String topicId) throws Exception {

        LOGGER.info("Starting publisher for {}", topicId);
        try {
            // TopicName topicName = TopicName.create(projectid, topicid);
            ExecutorProvider pubExecutor = InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(2).build();

            publisher = Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topicId))
                    .setExecutorProvider(pubExecutor)
                    .build();
            LOGGER.info("Started publisher: {}", publisher);
            LOGGER.info("Batching settings: {}", publisher.getBatchingSettings());

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Server failed to get topic from env variable: 'DLT_PUBSUB_FAILQUEUE_TOPIC'");
        }
        //generater = context.getBean("TradeGeneratorService", TradeGeneratorService.class);
    }


    public String publishMessage(Trade trade, String action,String oldSerialNumber)  {

        String oJson = GsonWrapper.toJson(trade);
        Map<String, String> tradeMap = GsonWrapper.fromJson(oJson, Map.class);

        String tradeid=String.valueOf(tradeMap.getOrDefault("tradeId","0"));
        String serialNumber=null;
        if(oldSerialNumber==null) {
            serialNumber = String.valueOf(CreateUUIDHelper.createUUID());
        }else{
            serialNumber=oldSerialNumber;
        }


        tradeMap.put("batchserialnum", serialNumber);
        tradeMap.put("begintimestamp", String.valueOf(System.currentTimeMillis()));
        tradeMap.put("action",action);
        tradeMap.put("subnumber", "0");
        tradeMap.put("batchnumOfTrades", "1");
        tradeMap.put("str_TradeId",String.valueOf(trade.getTradeId()));

        String payload = GsonWrapper.toJson(tradeMap);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();
        ApiFuture<String> messageId = publisher.publish(pubsubMessage);
        LOGGER.info("{}: published to queue tradeid: {} serialnumber: {}" ,this.topicId, tradeid, serialNumber);

        return serialNumber;
    }
}
