package com.maplequad.fo.ods.ulg.app;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.maplequad.fo.ods.ulg.services.TradePersistenceService;




public class GenericLoadTestInstance implements InitializingBean,DisposableBean{

	

	static Logger logger = LoggerFactory.getLogger(GenericLoadTestInstance.class);
	private static ApplicationContext context = null;
	private String projectId;
	private String instanceId;
	private String tableName;
	 //Instance of all Services
    private  TradePersistenceService perService;
	
	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
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


	public static GenericLoadTestInstance getInstance() {
		return new GenericLoadTestInstance();
	}

	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ClassPathXmlApplicationContext("SpringBeanConfig.xml");
		}
		return context;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		perService = new TradePersistenceService(projectId, instanceId, tableName);
		logger.info("Generic Load testing created");
	}
	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		perService.close();
		logger.info("Generic Load testing destroyed");
	}

	public void save(List<Map<String, String>> tradeList) {
		perService.serve(tradeList);
	}
	
}
