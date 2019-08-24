package com.maplequad.fo.ods.tradecore.lcm.grpc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class TradeCoreLifeCycleServiceGrpcServer implements ApplicationContextAware {
	private static final Logger logger = LoggerFactory.getLogger(TradeCoreLifeCycleServiceGrpcServer.class);
	private final int port;
	private Server server;

	ApplicationContext context = null;
	TradeCoreLifeCycleService service =null;

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// TODO Auto-generated method stub
		context = arg0;
		// context = new
		// ClassPathXmlApplicationContext("SpringBeanStorageGrpcService.xml");
		service = context.getBean("TradeCoreLifeCycleService", TradeCoreLifeCycleService.class);
		this.setupServer(service);
		
	}

	public TradeCoreLifeCycleServiceGrpcServer(int port) throws IOException {
		this.port = port;
		// this(ServerBuilder.forPort(port), port);
	}
	
	public void setupServer(TradeCoreLifeCycleService service){
		server = ServerBuilder.forPort(port).addService(service).build();
	}

	/** Start serving requests. */
	public void start() throws IOException {
		server.start();
		logger.info("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may has been reset by its
				// JVM shutdown hook.
				logger.error("*** shutting down gRPC server since JVM is shutting down");
				TradeCoreLifeCycleServiceGrpcServer.this.stop();
				logger.error("*** server shut down");
			}
		});
	}

	/** Stop serving requests and shutdown resources. */
	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	public void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	public TradeCoreLifeCycleService getService() {
		return service;
	}

	public void setService(TradeCoreLifeCycleService service) {
		this.service = service;
	}

	
}
