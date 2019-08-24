package com.gcppoc.upstreamloadtester.loadtest;

import java.util.Collection;

import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Database;
import foods.bigtable.repository.EntityNotFoundException;
import foods.bigtable.repository.RealRepository;
import foods.bigtable.repository.Repository;
import foods.bigtable.repository.TradesTable;



public class LoadTestInstance implements InitializingBean {
	static Logger logger = LoggerFactory.getLogger(LoadTestInstance.class);
	private static ApplicationContext context = null;
	private String projectId;
	private String instanceId;

	//private static LoadTestInstance ourInstance = new LoadTestInstance();
	

	private Connection hbaseconnection;
	private Repository repository;

	private LoadTestInstance() {
		// Random random = new Random(System.currentTimeMillis() *
		// Thread.currentThread().getId());
		logger.debug("Call LoadTestInstance");
	}

	public void connectBigTable() throws Exception {
		logger.info("BigTable connecting to " + projectId + " / " + instanceId);
		if (hbaseconnection == null || hbaseconnection.isClosed()) {
			hbaseconnection = Database.createConnection(projectId, instanceId);
			repository = new RealRepository(hbaseconnection);
		}
	}

	public static LoadTestInstance getInstance() {
//		if (ourInstance == null) {
//			ourInstance = new LoadTestInstance();
//		}
		return new LoadTestInstance();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Create connection");
		try {
			this.connectBigTable();
			this.createTables();
		} catch (Exception e) {
			logger.error(e.getMessage());

		}
	}

	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("SpringBeanConfig.xml");
		}
		return context;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Connection getHbaseconnection() {
		return hbaseconnection;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void createTables() throws Exception {
		TradesTable.createTable(hbaseconnection);
	}

	public void close() throws Exception {
		hbaseconnection.close();
	}

	public void save(Collection<EquityTrade> trades) throws Exception {
		logger.info("Creating " + trades.size() + " equity trades..");
		long start = System.currentTimeMillis();

		for (EquityTrade trade : trades) {
			repository.put(trade);
			System.out.println("Saved " + trade.getTradeId().getId() + " / " + trade.getTradeVersion());
		}

		long elapsed = System.currentTimeMillis() - start;
		double avg = elapsed / (double) trades.size();

		logger.info("Done. Elapsed: " + elapsed + " ms, Avg: " + avg);
	}

	public void read(Collection<EquityTrade> trades) throws Exception {
		logger.info("Reading " + trades.size() + " equity trades..");
		long start = System.currentTimeMillis();

		for (EquityTrade trade : trades) {
			read(trade.getTradeId(), trade.getTradeVersion());
		}

		long elapsed = System.currentTimeMillis() - start;
		double avg = elapsed / (double) trades.size();

		logger.info("Done. Elapsed: " + elapsed + " ms, Avg: " + avg);
	}

	public void read(TradeId tradeId, int version) throws Exception {
		logger.info("Reading " + tradeId.getId() + " / " + version);
		try {
			EquityTrade trade = repository.get(tradeId, version);
			logger.info("Got: " + trade);
		} catch (EntityNotFoundException ex) {
			logger.info("Not found: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
