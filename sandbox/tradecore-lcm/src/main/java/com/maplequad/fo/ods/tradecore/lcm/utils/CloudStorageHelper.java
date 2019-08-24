package com.maplequad.fo.ods.tradecore.lcm.utils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.maplequad.fo.ods.tradecore.client.service.GsonWrapper;

public class CloudStorageHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudStorageHelper.class);
	//static CloudStorageHelper instance=null;
	Storage storage =null;
	String bucketName=null;

	static SimpleDateFormat mydf = new SimpleDateFormat("yyyyMMdd");
	
	public  CloudStorageHelper (String bucketName) throws Exception{
		CloudStorageHelper instance=this;
		//if(instance==null){
			
			instance.storage = StorageOptions.getDefaultInstance().getService();

			instance.bucketName=bucketName;
		    // Creates the new bucket
		    if(instance.storage.get(bucketName)==null){
		    	
			    Bucket bucket = instance.storage.create(BucketInfo.of(bucketName));
			    LOGGER.info("Bucket created:"+ bucket.getName());
		    }
		
		//}
		//return instance;
	}
	
public <T> String saveObject2Jason (T o, String fileName) throws Exception{
		
		
		String str= GsonWrapper.toJson(o);
		InputStream in = IOUtils.toInputStream(str, "UTF-8");
		
		BlobInfo blobInfo =
		        storage.create(
		            BlobInfo
		                .newBuilder(bucketName, fileName)
		                // Modify access list to allow all users with link to read file
		                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
		                .build(),
		            in);
		
		return blobInfo.getSelfLink();
		
	}
	
	public <T> String saveObject2Jason (List<T> lst, String fileName) throws Exception{
		
		
		String str= GsonWrapper.toJson(lst);
		InputStream in = IOUtils.toInputStream(str, "UTF-8");
		
		BlobInfo blobInfo =
		        storage.create(
		            BlobInfo
		                .newBuilder(bucketName, fileName)
		                // Modify access list to allow all users with link to read file
		                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
		                .build(),
		            in);
		
		return blobInfo.getSelfLink();
		
	}
	
	public static String adviseFileName(String operationType, String stage, String identifier,long l, String fileType){
		StringBuffer path=new StringBuffer();
		
		String date1 = mydf.format(new Date());  
		path.append(date1);
		path.append("/");
		path.append(identifier);
		path.append("/");
		path.append(operationType);
		path.append(".");
		path.append(stage);
		path.append(".");
		
		path.append(l);
		path.append('.');
		path.append(fileType);
		
		return path.toString();
	}
	
}
