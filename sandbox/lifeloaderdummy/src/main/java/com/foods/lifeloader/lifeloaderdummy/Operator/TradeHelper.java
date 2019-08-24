package com.foods.lifeloader.lifeloaderdummy.Operator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.foods.lifeloader.lifeloaderdummy.Publisher.MessagePublisher;
import com.maplequad.fo.ods.tradecore.client.service.UltGsonObject;
import com.maplequad.fo.ods.tradecore.lcm.services.TradeGeneratorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.foods.lifeloader.lifeloaderdummy.Publisher.TrackLog;
import com.foods.lifeloaderutility.CloudStorageHelper;
import com.foods.lifeloaderutility.CreateUUIDHelper;
import com.foods.measure.CSALogMapListWriter;
import com.foods.statediagram.STATE;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;

public class TradeHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(TradeHelper.class);
	private static final int maxTradePerLot=2000;
	private static final double WAIT_TIME_SECOND=0.5;
	
	
	public static List<TrackLog> generateTradeOperation(ApplicationContext context, MessagePublisher publisher, TrackLog log,
																											int lot) throws Exception {

		if (log.operation.equals("randomnew")) {
			return generateRandomNewTradeSingle(context, publisher, log);
		} /*else if (log.operation.equals("randomnew_old")) {
			return generateRandomNewTrade(context, publisher, log, lot);
		} */
		else if (log.operation.equals("radomnew+amend")) {
			return generateRandomNewFollowAmendTradeSingle(context, publisher, log);
		}

		return null;
	}

	/*
	public static List<Trade> tradeCreationList(ApplicationContext context,int numOfTrade) throws Exception {
		List<Trade> tradeLst = new LinkedList<Trade>();
		TradeGeneratorServiceLegacy generater = context.getBean("TradeGeneratorServiceLegacy",
				TradeGeneratorServiceLegacy.class);
		// TradeConverterService2Pojo myConvertor =
		// context.getBean("TradeConverterService2Pojo",
		// TradeConverterService2Pojo.class);
		TradeConverterService2Pojo convertor = context.getBean("TradeConverterService2Pojo",
				TradeConverterService2Pojo.class);
		List<Map<String, String>> oldtradeMapLst = generater.serve(numOfTrade);
		List<Trade> tradeMapLst2 = convertor.serve(oldtradeMapLst);

		tradeMapLst2.forEach( (a) -> tradeLst.add(a));
		return tradeLst;
	}*/

/*
	public static Trade tradeCreation(ApplicationContext context) throws Exception {
		Trade trade = null;


		TradeGeneratorServiceLegacy generater = context.getBean("TradeGeneratorServiceLegacy",
				TradeGeneratorServiceLegacy.class);
		// TradeConverterService2Pojo myConvertor =
		// context.getBean("TradeConverterService2Pojo",
		// TradeConverterService2Pojo.class);
		TradeConverterService2Pojo convertor = context.getBean("TradeConverterService2Pojo",
				TradeConverterService2Pojo.class);
		List<Map<String, String>> oldtradeMapLst = generater.serve(1);
		List<Trade> tradeMapLst2 = convertor.serve(oldtradeMapLst);
		trade = tradeMapLst2.get(0);

		return trade;
	}*/
	
	private static void writeCloudStorage(ApplicationContext context,List<TrackLog> nlist,String system, String operation,String serialNumber){
		
		try {
			CloudStorageHelper helper = context.getBean("CloudStorageHelper", CloudStorageHelper.class);
			String filePath = CloudStorageHelper.adviseFileName(system, operation,
					serialNumber, CreateUUIDHelper.createUUID(), "csv");
			LOGGER.info("Begin to write Track Log link");
			
			String s=CSALogMapListWriter.writeCsvfromList(nlist);
			String link = helper.saveTextObject(s, filePath);
			LOGGER.info("End to write Track Log link:" + link);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private static List<TrackLog> generateRandomNewFollowAmendTradeSingle(ApplicationContext context,
																																				MessagePublisher publisher, TrackLog log) throws Exception {
		List<ApiFuture<String>> apiFutures = new ArrayList<>();
		List<TrackLog> nlist = generateRandomNewTradeSingle(context, publisher, log);
		
		Thread.sleep(20*1000);
		
		TrackLog r = (TrackLog) log;
		try {
			int i = 0;
			for (TrackLog nl : nlist) {
				// randomize trade for amendment
				//Trade trade = tradeCreation(context);
				Trade trade=TradeGeneratorHelper.tradeCreation(log.tradeSpec);
				trade.setOsTradeId(nl.osTradeId);

				String oJson = GsonWrapper.toJson(trade);
				nl.tradeJson=oJson;
				Map<String, String> tradeMap = GsonWrapper.fromJson(oJson, Map.class);

				nl.subnumber = i++;
				nl.action = STATE.ACTION.SAVE;
				nl.requestTime = new Date().getTime();
				tradeMap.put("batchserialnum", String.valueOf(nl.serialNumber));
				tradeMap.put("begintimestamp", String.valueOf(nl.requestTime));
				tradeMap.put("subnumber", String.valueOf(nl.subnumber));
				tradeMap.put("batchnumOfTrades", String.valueOf(nl.numOfTrades));
				tradeMap.put("action", nl.action);
				
				nl.requestTime = new Date().getTime();
				TradeEvent et = trade.getTradeEventList().get(0);
				et.setEventStatus(STATE.VER);
				
				
				String payload = GsonWrapper.toJson(tradeMap);
				PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();
				
				nl.beginQTime=new Date().getTime();
				ApiFuture<String> messageId = publisher.publish(pubsubMessage);
				nl.endQTime=new Date().getTime();
				apiFutures.add(messageId);
				LOGGER.info("Processed:" + (i ) + "trades");
			}
			List<String> messageIds = ApiFutures.allAsList(apiFutures).get();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		} finally {

			//writeCloudStorage( context, nlist,"loader", "ULT_RANDOMNewAndAmend",String.valueOf(r.serialNumber));
			
		}

		return nlist;
	}

	private static List<TrackLog> generateRandomNewTradeSingle(ApplicationContext context, MessagePublisher publisher,
																														 TrackLog log) throws Exception {
		List<TrackLog> rl = new ArrayList<TrackLog>();
    publishMessageLst(context, log, STATE.ACTION.CREATE, STATE.NEW, rl, false, null, publisher);
		return rl;

	}

	private static void publishMessageLst(ApplicationContext context, TrackLog log, String action,
																											 String finalState, List<TrackLog> rl, boolean overwriteTradeId,
																											 List<TrackLog> oldTrackLog,
																											 MessagePublisher publisher) throws Exception{
		TrackLog r = log;

		int logFreq = 1000;
		int logCnt = 0;
		int i = 0;

		for (; i < r.numOfTrades; i++, logCnt++) {

			Trade trade = TradeGeneratorHelper.tradeCreation(log.tradeSpec);
			TradeEvent et = trade.getTradeEventList().get(0);
			et.setEventStatus(finalState);

			UltGsonObject obj = new UltGsonObject();
			obj.trade = trade;
			obj.batchserialnum = r.serialNumber;
			obj.begintimestamp = System.currentTimeMillis();
			obj.subnumber = i;
			obj.batchnumOfTrades = r.numOfTrades;
			obj.action = action;

			String payload = GsonWrapper.toJson(obj);

			//LOGGER.info("payload=============: {}", payload);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();
			publisher.publish(pubsubMessage);

			if (logCnt == logFreq) {
				LOGGER.info("Published {}/{} {} trades, serial = {}", i, r.numOfTrades, trade.getProductType(), log.serialNumber);
				logCnt = 0;
			}
		}


	}

	/*
	private static List<TrackLog> generateRandomNewTrade(ApplicationContext context, MessagePublisher publisher, TrackLog log,
			int lot) throws Exception {
		TradeGeneratorServiceLegacy generater = null;
		generater = context.getBean("TradeGeneratorServiceLegacy", TradeGeneratorServiceLegacy.class);
		// TradeConverterService2Pojo myConvertor =
		// context.getBean("TradeConverterService2Pojo",
		// TradeConverterService2Pojo.class);
		TradeConverterService2Pojo convertor = context.getBean("TradeConverterService2Pojo",
				TradeConverterService2Pojo.class);

		List<ApiFuture<String>> apiFutures = new ArrayList<>();

		TrackLog r = (TrackLog) log;
		List<TrackLog> rl = new LinkedList<TrackLog>();
		try {
			for (int i = 0; i < r.numOfTrades; i++) {
				TrackLog nl = new TrackLog(r);
				nl.requestTime = new Date().getTime();
				List<Map<String, String>> oldtradeMapLst = generater.serve(lot);
				List<Trade> tradeMapLst2 = convertor.serve(oldtradeMapLst);
				String oJson = gson.toJson(tradeMapLst2);
				nl.tradeJson=oJson;
				List<Map<String, String>> tradeMapLst = gson.fromJson(oJson, List.class);

				// Date tt = new Date();
				nl.subnumber = i;
				

				tradeMapLst.forEach((aa) -> {
					aa.put("batchserialnum", String.valueOf(nl.serialNumber));
					aa.put("begintimestamp", String.valueOf(nl.requestTime));
					aa.put("subnumber", String.valueOf(nl.subnumber));
					aa.put("batchnumOfTrades", String.valueOf(nl.numOfTrades));
					aa.put("action", "CREATE");
				});

				String payload = gson.toJson(tradeMapLst);
				PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload))
						.build();
				nl.beginQTime=new Date().getTime();
				ApiFuture<String> messageId = publisher.publish(pubsubMessage);
				nl.endQTime=new Date().getTime();
				apiFutures.add(messageId);
				LOGGER.info("Processed:" + (i + 1) + "trades");
				rl.add(nl);
			}

			List<String> messageIds = ApiFutures.allAsList(apiFutures).get();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		} finally {
			
			writeCloudStorage( context, rl,"loader", "upstream",String.valueOf(r.serialNumber));
			
			
//			try {
//				CloudStorageHelper helper = context.getBean("CloudStorageHelper", CloudStorageHelper.class);
//				String filePath = CloudStorageHelper.adviseFileName("loader", "upstream",
//						String.valueOf(r.serialNumber), CreateUUIDHelper.createUUID(), "json");
//				LOGGER.info("Begin to write Track Log link");
//				String link = helper.saveObject2Jason(rl, filePath);
//				LOGGER.info("End to write Track Log link:" + link);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}

		return rl;

	}
	*/

}
