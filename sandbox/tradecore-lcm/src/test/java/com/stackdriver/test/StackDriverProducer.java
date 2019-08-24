package com.stackdriver.test;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.PlainPublisher;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.SimpleTaskProducer;
import com.maplequad.fo.ods.tradecore.lcm.utils.stackdriver.StackDriverPublisher;

public class StackDriverProducer extends SimpleTaskProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(StackDriverProducer.class);
	
	
	public StackDriverProducer(PlainPublisher p,TaskExecutor tt,int d){
		super(p,tt,d);
	}

	String projectid="fo-ods";
	String metricType="test/num";
	StackDriverPublisher p ;
	Map<String, String> metricLabels;
	Map<String, String> resourceLabels ;
	
	public void init()throws Exception{
		p = StackDriverPublisher.getStackDriverPublisher(projectid,metricType);
		p.createCustomMetric();
		
		p.listMetricDescriptors();
		
		metricLabels = new HashMap<String, String>();
		resourceLabels = new HashMap<String, String>();
		resourceLabels.put("project_id", "fo-ods");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = 0;
		while (i<10) {
			
			LOGGER.info("I start producing:"+i);
			pp.putQueue(Math.random());
			LOGGER.info("I finish producing:"+i);
			try{
			Thread.sleep(this.mydelay);
			i++;
			}catch(Exception e){
			}
		}
	}
	
	
}
