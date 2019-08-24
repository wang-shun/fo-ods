package com.foods.measure;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foods.lifeloaderutility.CloudStorageHelper;
import com.foods.lifeloaderutility.CreateUUIDHelper;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;

public class ExtractF2BResult2CloudStorage {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractF2BResult2CloudStorage.class);
	public String spannerInstanceId=null;
	public String spannerDbId=null;
	private DatabaseClient dbClient;

	public ExtractF2BResult2CloudStorage(String instance, String DB){
		SpannerOptions options = SpannerOptions.newBuilder().build();
		Spanner spanner = options.getService();

		spannerInstanceId=instance;
		spannerDbId=DB;
		LOGGER.info("============= CONNECTING to spanner =============== ");

		dbClient = spanner.getDatabaseClient(DatabaseId.of(options.getProjectId(), spannerInstanceId, spannerDbId));
		LOGGER.info("============= CONNECTED! ========================== ");
	}


	public List<LCTrackLog> queryLCTrackLog(String serialNumber){

		List<LCTrackLog> tl = new LinkedList<LCTrackLog>();
		StringBuffer query=new StringBuffer("select * from stats");
		if(serialNumber!=null && serialNumber.length()>1){
			query.append(" where serialNumber=");
			query.append(serialNumber);
		}

		ResultSet result =
				dbClient
						.singleUse()
						.executeQuery(
								// We use FORCE_INDEX hint to specify which index to use. For more details see
								// https://cloud.google.com/spanner/docs/query-syntax#from-clause
								Statement.of(query.toString()));
		try {
			while (result.next()) {
				LCTrackLog log = new LCTrackLog();
				log.tradeid = result.getString("tradeId");
				log.osTradeid = result.getString("osTradeid");
				log.action = result.getString("action");
				log.serialNumber = String.valueOf(result.getLong("serialNumber"));
				log.ULT_requestTimestamp = LCTrackLog.convertTimeStamp(result.getTimestamp("ult"));
				log.LC_CRUDStartTime = LCTrackLog.convertTimeStamp(result.getTimestamp("lcCrudStart"));
				log.LC_arrivalTime = LCTrackLog.convertTimeStamp(result.getTimestamp("lcStart"));
				log.LC_CRUDEndTime = LCTrackLog.convertTimeStamp(result.getTimestamp("lcCrudEnd"));
				log.DB_StartTime = LCTrackLog.convertTimeStamp(result.getTimestamp("dbStart"));
				log.DB_EndTime = LCTrackLog.convertTimeStamp(result.getTimestamp("dbEnd"));
				log.DLT_arriveTime = LCTrackLog.convertTimeStamp(result.getTimestamp("dltArriveTime"));
				log.calculate();
				tl.add(log);
			}
		}finally {
			result.close();
		}

		return tl;
	}

	public Info ExtractQueryLog(String serialNumber) throws Exception{
		List<LCTrackLog> loglst=queryLCTrackLog(serialNumber);
		String str = CSALogMapListWriter.writeCsvfromListLC(loglst);


		String _serialNumber=(serialNumber==null||serialNumber.length()==0)?String.valueOf( CreateUUIDHelper.createUUID()):serialNumber;
		CloudStorageHelper helper=new CloudStorageHelper("foodstest_service");
		String filePath = CloudStorageHelper.adviseFileNameshort("ExtractionStat",_serialNumber, "csv");

		String link = helper.saveTextObject(str, filePath);

		Info info = new Info();
		info.selfLink=link;
		info.gsLink = "gs://"+helper.bucketName+"/"+filePath;
		return info;
	}

	public class Info{
		public String selfLink;
		public String gsLink;
	}

}
