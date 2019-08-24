package com.maplequad.fo.ods.tradecore.lcm.helper;

import java.io.IOException;
import java.util.Date;

import com.foods.statediagram.Exception.TradeValidationException;
import com.google.gson.Gson;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.lcm.processor.DLTMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import com.foods.statediagram.STATE;
import com.foods.statediagram.StateTransitionGraphFactory;
import com.foods.statediagram.StateTransitionGraphInterface;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.store.data.access.service.SpannerDatabaseService;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;


public class TradeStoreGRPCHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(TradeStoreGRPCHelper.class);

	//SpannerDatabaseService spannerservice = SpannerDatabaseService.getInstance();
	private static final String ACTION_CREATE=STATE.ACTION.CREATE;

	// ApplicationContext context = null;
	// TradeConverterService2Pojo myConvertor = null;
	TradeCoreStoreService storeservice = null;
	StateTransitionGraphFactory transitionFactory = null;
	// ObjectMapper mapper = new ObjectMapper();
	Publisher publisher = null;
	String topicid;
	
	public TradeStoreGRPCHelper(String topic)throws IOException{
		topicid = topic; //System.getenv("DLT_PUBSUB_TOPIC");
		LOGGER.info("My topic is" + topicid);
		this.prepareTopic(topicid);
	}
	
	public void prepareTopic(String topicId) throws IOException {

		try {
			// TopicName topicName = TopicName.create(projectid, topicid);
			publisher = Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topicId))
					.build();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.info("Server failed to get topic from env variable: 'PUBSUB_TOPIC'");
			throw e;
		}

	}
	
	public Trade insertTrade(Trade t,LCTrackLog tLog) throws NoTransitionFoundException,NoTradeEventFoundException,IncorrectTransitionforState,TradeValidationException {
		
		tLog.LC_arrivalTime=new Date().getTime();
		tLog.action=ACTION_CREATE;
		Trade tts = null;
		
		StateTransitionGraphInterface graphInterface = transitionFactory
				.getMyTransitionDiagram(t.getProductType());

		Trade tt = graphInterface.transitionState(t, t.getProductType(), ACTION_CREATE);
		LOGGER.info("run transition ok");

		TradeCoreStoreResponse response=null;
		tLog.LC_CRUDStartTime = new Date().getTime();
		
			
			response=storeservice.createTrade(tt);
		tLog.LC_CRUDEndTime = new Date().getTime();
		tts = response.getTrade();
		if (response.getDsStartTS() != null) {
			tLog.DB_StartTime = response.getDsStartTS().toSqlTimestamp().getTime();
		}
		if (response.getDsFinishTS() != null) {
			tLog.DB_EndTime = response.getDsFinishTS().toSqlTimestamp().getTime();
		}
		tLog.tradeid = String.valueOf(tts.getTradeId());
		tLog.osTradeid=tts.getOsTradeId();
		tLog.calculate();

		LOGGER.info("Save trade to grpc service finish:Operation{} TradeId{} Product{}",tLog.action,tts.getTradeId(), tts.getProductType());

		
		
		try {
			this.publish2Downstream(tts, tLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tts;
		
	}

	public TradeCoreStoreService getStoreservice() {
		return storeservice;
	}

	public void setStoreservice(TradeCoreStoreService storeservice) {
		this.storeservice = storeservice;
	}

	public StateTransitionGraphFactory getTransitionFactory() {
		return transitionFactory;
	}

	public void setTransitionFactory(StateTransitionGraphFactory transitionFactory) {
		this.transitionFactory = transitionFactory;
	}
	
	public void publish2Downstream(Trade tt, LCTrackLog l) throws Exception {

		DLTMessage dm = new DLTMessage();
		dm.trade = tt;
		dm.log = l;

		String payload = GsonWrapper.toJson(dm);
		PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();
		ApiFuture<String> messageId = publisher.publish(pubsubMessage);
		LOGGER.info("{}: published to queue {} serial num: {}",topicid , tt.getTradeId(),l.serialNumber);
	}
	
	
}
