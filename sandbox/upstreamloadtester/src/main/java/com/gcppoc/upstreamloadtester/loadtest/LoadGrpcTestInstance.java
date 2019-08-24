package com.gcppoc.upstreamloadtester.loadtest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import foods.traderepository.proto.EquityTrade;
import foods.traderepository.proto.SaveTradeRequest;
import foods.traderepository.proto.SaveTradeResponse;
import foods.traderepository.proto.TradeRepositoryGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class LoadGrpcTestInstance implements InitializingBean {

	static Logger logger = LoggerFactory.getLogger(LoadGrpcTestInstance.class);
	private static ApplicationContext context = null;
	private String hostname;
	private int port;
	private TradeRepositoryGrpc.TradeRepositoryBlockingStub blockingStub=null;

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		Channel channel = ManagedChannelBuilder.forAddress(this.hostname, this.port).usePlaintext(true).build();
		this.blockingStub = TradeRepositoryGrpc.newBlockingStub(channel);
	}

	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("SpringBeanConfig.xml");
		}
		return context;
	}

	public static LoadGrpcTestInstance getInstance() {
		// if (ourInstance == null) {
		// ourInstance = new LoadTestInstance();
		// }
		return new LoadGrpcTestInstance();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void save(Collection<foods.bigtable.model.EquityTrade> trades) throws Exception {
		
		logger.info("Creating " + trades.size() + " equity trades..");
		long start = System.currentTimeMillis();
		Gson gson = new Gson();
		ObjectMapper _mapper = new ObjectMapper();
		for (foods.bigtable.model.EquityTrade trade : trades) {
			String json = gson.toJson(trade);
			//String json = _mapper.writeValueAsString(trade);
			logger.info("json: {}", json);

			EquityTrade grpcTrade = EquityTrade.newBuilder().setJson(json).build();
			SaveTradeRequest request = SaveTradeRequest.newBuilder().setTrade(grpcTrade).build();
			SaveTradeResponse response = blockingStub.saveTrade(request);
			logger.info("response: {}", response);
		}
		long elapsed = System.currentTimeMillis() - start;
		double avg = elapsed / (double) trades.size();
		logger.info("{} trades took {} ms to save, avg={} ms/trade", trades.size(), elapsed, avg);
	}

}
