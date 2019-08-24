package com.maplequad.fo.ods.tradecore.lcm.app;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maplequad.fo.ods.tradecore.lcm.grpc.TradeCoreLifeCycleServiceGrpcServer;
import com.maplequad.fo.ods.tradecore.lcm.subscriber.PullLifeLoaderPubSubService;

//@SpringBootApplication
public class LifeCycleManagementApp {

	private static final Logger LOGGER = LoggerFactory.getLogger(LifeCycleManagementApp.class);
	
	static TradeCoreLifeCycleServiceGrpcServer server=null;

	public static void main(String[] args) throws Exception{
		LOGGER.info("Args: {}", Arrays.asList(args));

		String runULTPUBSUB=System.getenv("RUNULTPUB_SUB");
		
		//GRPC service
		 ApplicationContext context =  new ClassPathXmlApplicationContext("SpringBeanStorageGrpcService.xml");

			server = context.getBean("TradeCoreLifeCycleServiceGrpcServer",TradeCoreLifeCycleServiceGrpcServer.class);
			
			server.start();
		
		if(runULTPUBSUB!=null&&!runULTPUBSUB.equals("Y") && !runULTPUBSUB.equals("y")){
			server.blockUntilShutdown();
			return;
		}
			
		int nThreads = 2;
		String threads = System.getenv("SUBSCRIBER_THREADS");
		if (threads != null) {
			nThreads = Integer.parseInt(threads);
		}
		LOGGER.info("Running with {} subscriber threads", nThreads);

		
		//This line is for push strategy... no need by now
		//SpringApplication.run(LifeCycleManagementApp.class, args);
		
		
		PullLifeLoaderPubSubService service = new PullLifeLoaderPubSubService();
		service.start_new(nThreads);

   
	    //server.blockUntilShutdown();
	    
	    
	}
}
