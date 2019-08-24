package com.foods.lifeloader.lifeloaderdummy;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.foods.lifeloader.lifeloaderdummy.Publisher.TrackLog;
import com.foods.lifeloaderutility.CloudStorageHelper;
import com.foods.lifeloaderutility.CreateUUIDHelper;

public class TestCloudStorage {

	
	
	@Test
	public void test() throws Exception{
		
		
//		Storage storage = StorageOptions.getDefaultInstance().getService();
//
//	    // The name for the new bucket
//	    String bucketName = "foodstest";  // "my-new-bucket";
//
//	    // Creates the new bucket
//	    if(storage.get(bucketName)==null){
//	    Bucket bucket = storage.create(BucketInfo.of(bucketName));
//
//	    System.out.printf("Bucket %s created.%n", bucket.getName());
//	    }
		ApplicationContext context= new ClassPathXmlApplicationContext("SpringBeanTest.xml");
		
		CloudStorageHelper helper = context.getBean("CloudStorageHelper",CloudStorageHelper.class);
		
		TrackLog log = new TrackLog();
		log.serialNumber=String.valueOf(3451234324L);
		log.numOfTrades=2;
		log.subnumber=1;log.requestTime=new Date().getTime();
		List<TrackLog> ll = new LinkedList<TrackLog>();
		ll.add(log);
		log = new TrackLog(log);
		log.subnumber=2;log.requestTime=new Date().getTime();
		ll.add(log);
		String filePath = CloudStorageHelper.adviseFileName("loader", "upstream", String.valueOf(CreateUUIDHelper.createUUID()), CreateUUIDHelper.createUUID(), "json");
		helper.saveObject2Jason(ll, filePath);
	}
}
