package com.maplequad.fo.ods.tradecore.lcm.processor;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.foods.statediagram.Exception.IncorrectTransitionforState;
import com.foods.statediagram.Exception.NoTradeEventFoundException;
import com.foods.statediagram.Exception.NoTransitionFoundException;
import com.foods.statediagram.Exception.TradeValidationException;
import com.foods.statediagram.STATE;
import com.foods.statediagram.StateTransitionGraphFactory;
import com.foods.statediagram.StateTransitionGraphInterface;
import com.foods.statediagram.transition.AbstractTransitionHandler;
import com.google.api.core.ApiFuture;
import com.google.cloud.ServiceOptions;
import com.google.cloud.Timestamp;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.maplequad.fo.ods.tradecore.client.service.UltGsonObject;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.lcm.exception.NoTradeFoundException;
import com.maplequad.fo.ods.tradecore.lcm.model.PubSubHeaderHelper;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.store.data.access.service.SpannerDatabaseService;
import com.maplequad.fo.ods.tradecore.store.data.access.service.TradeDataAccessService;
import com.maplequad.fo.ods.tradecore.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class GrpcSpannerServiceProcessor extends AbstractSpannerServiceProcessor
		implements TradeProcessorInterface, ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(GrpcSpannerServiceProcessor.class);

	private SpannerDatabaseService spannerservice = SpannerDatabaseService.getInstance();
	private DatabaseClient dbClient = spannerservice.getDBClient();

	// ApplicationContext context = null;
	// TradeConverterService2Pojo myConvertor = null;
	private TradeStoreService storeService = null;
	private TradeDataAccessService accessService = null;
	private StateTransitionGraphFactory transitionFactory = null;
	// ObjectMapper mapper = new ObjectMapper();
	private Publisher publisher = null;

	private MetricRegistry registry = new MetricRegistry();
	private Meter requests = registry.meter("requests");

	boolean tacticalFix=false;

	public GrpcSpannerServiceProcessor() throws Exception {
		String topicid = System.getenv("DLT_PUBSUB_TOPIC");

		LOGGER.info("My topic is {}", topicid);
		assert (topicid!=null);
		this.prepareTopic(topicid);
	}

	public void prepareTopic(String topicId) throws Exception {

		try {
			// TopicName topicName = TopicName.create(projectid, topicid);
			publisher = Publisher.defaultBuilder(TopicName.create(ServiceOptions.getDefaultProjectId(), topicId))
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Server failed to get topic from env variable: 'PUBSUB_TOPIC'");
		}

	}
	
	private String queryTradeByOsTradeId(String ostradeid) throws NoTradeFoundException{
		String result="";

		StringBuffer sb=new StringBuffer();
		sb.append("select ostradeid,tradeid from Trade@{FORCE_INDEX=searchByOSTradeID} where ostradeid='");
		sb.append(ostradeid);
		sb.append("';");


		ResultSet resultSet =
				dbClient
						.singleUse()
						.executeQuery(
								// We use FORCE_INDEX hint to specify which index to use. For more details see
								// https://cloud.google.com/spanner/docs/query-syntax#from-clause
								Statement.of(sb.toString()));
		try {
			if (resultSet.next()) {
				result = resultSet.getString("tradeid");
			} else {
				throw new NoTradeFoundException("No trade found for: " + ostradeid);
			}
		}finally {
			resultSet.close();
		}


		return result;
	}

	public Trade retrieveOldTrade (String osTradeId,String assetClass) throws NoTradeFoundException{
		if(tacticalFix) {
			String tradeid = queryTradeByOsTradeId(osTradeId);
			TradeCoreStoreResponse rt = storeService.getLatestTrade(tradeid);

			Trade tt = rt.getTrade();

			return tt;
		}else {
			return storeService.getLatestTrades(TradeQuery.newBuilder().setByOsTradeId(osTradeId).build()).getTrade();
		}
	}

	@Override
	public Trade processTradeFromMapSingle(String orgJson)
			throws Exception, NoTradeFoundException {

		long start = System.currentTimeMillis();
		requests.mark();
		UltGsonObject ultObj = GsonWrapper.fromJson(orgJson, UltGsonObject.class);

		Trade t = ultObj.trade;
		TradeEvent et = t.getTradeEventList().get(0);
		if(!et.getEventType().equals(STATE.ACTION.REJECT)) {
			t.getTradeEventList().get(0).setEventRemarks("");
		}

		Trade tts = t;
		LCTrackLog tLog = new LCTrackLog();
		LOGGER.warn("Save trade {} to grpc service", t.getOsTradeId());

		try {
			tLog.serialNumber = ultObj.batchserialnum;
			tLog.ULT_requestTimestamp = ultObj.begintimestamp;
			tLog.subnumber = ultObj.subnumber;
			tLog.numOfTrades = ultObj.batchnumOfTrades;
			tLog.action = ultObj.action;
			tLog.LC_arrivalTime = start;
			tLog.osTradeid = t.getOsTradeId();

			if (tLog.action == null) {
				tLog.action = "CREATE";
			} else if (tLog.action.equals("CREATE")) {
				AbstractTransitionHandler.getLatestEvent(t).setEventStatus("NEW");
			}

			// Retrieve the old trade
			if (!tLog.action.equals("CREATE")) {
				
				try{
				Trade rTrade=retrieveOldTrade(t.getOsTradeId(),t.getAssetClass());
				
				
				t.setTradeId(rTrade.getTradeId());
				t.setOsTradeId(rTrade.getOsTradeId());
				
				String prevTradeStatus=rTrade.getTradeEventList().get(0).getEventStatus();
				
				t.getTradeEventList().get(0).setValidTimeFrom(Timestamp.now());
				t.getTradeEventList().get(0).setTradeId(rTrade.getTradeId());
				t.getTradeEventList().get(0).setEventStatus(prevTradeStatus);
				}catch(NoTradeFoundException ne){
					LOGGER.error(ne.getMessage());
					//ne.printStackTrace();
					throw ne;
					//return t;
				}
			}

			try {
				StateTransitionGraphInterface graphInterface = transitionFactory
						.getMyTransitionDiagram(t.getProductType());

				Trade tt = graphInterface.transitionState(t, t.getProductType(), tLog.action);
				LOGGER.info("run transition ok");

				TradeCoreStoreResponse response=null;
				tLog.LC_CRUDStartTime = new Date().getTime();
				//Storage operation
				if(tLog.action.equals("CREATE")){
					response = storeService.createTrade(tt);
				}else{
					
					response= storeService.updateTrade(tt);
				}
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
					LOGGER.error(e.getMessage());
					//e.printStackTrace();
				}
			} catch (Exception e) {
				LOGGER.error("Failed to process: {}", e.getMessage(), e);
				
				//e.printStackTrace();
				return t;
			}
		} finally {
			this.pushStatistics2CloudStorage(tLog);
		}

		return tts;
	}

	public void publish2Downstream(Trade tt, com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog l) throws Exception {

		DLTMessage dm = new DLTMessage();
		dm.trade = tt;
		dm.log = l;

		String payload = GsonWrapper.toJson(dm);

		PubsubMessage.Builder builder=PubsubMessage.newBuilder();
		builder=PubSubHeaderHelper.fillPubSubBuilder(builder,tt);

		PubsubMessage pubsubMessage = builder.setData(ByteString.copyFromUtf8(payload)).build();

		ApiFuture<String> messageId = publisher.publish(pubsubMessage);
		// ensure we get acknowledgement from the server
		messageId.get();

		LOGGER.info("DLT: published to queue tradeid: {} serialnumber: {} action: {}" , tt.getTradeId(), l.serialNumber,tt.getTradeEventList().get(0).getEventType());
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		context = arg0;
		LOGGER.info("Using store service: {}", storeService);

		LogUtils.startMetricsReporter(registry, LOGGER, Slf4jReporter.LoggingLevel.WARN);

		this.transitionFactory = context.getBean("StateTransitionGraphFactory", StateTransitionGraphFactory.class);
		assert (storeService !=null);
		assert(this.transitionFactory!=null);
	}

	@Override
	public List<Trade> processTradeList(List<Trade> mapList) throws Exception {
		return null;
	}

	@Override
	public List<Trade> queryTradeList(List<String> tradeId) throws Exception {
		return null;
	}

	public TradeStoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(TradeStoreService storeService) {
		this.storeService = storeService;
	}

	public TradeDataAccessService getAccessService() {
		return accessService;
	}

	public void setAccessService(TradeDataAccessService accessService) {
		this.accessService = accessService;
	}
}
