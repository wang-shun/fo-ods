package com.foods.lifeloader.lifeloadercreator;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maplequad.fo.ods.tradecore.lcm.utils.stackdriver.StackDriverPublisher;

public class TestStackDriverPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestStackDriverPublisher.class);
	
	@Test
	public void test() throws Exception{
		String projectid="fo-ods";
		String metricType="test/num";
		StackDriverPublisher p = StackDriverPublisher.getStackDriverPublisher(projectid,metricType);
		
		p.createCustomMetric();
		
		p.listMetricDescriptors();
		
		Map<String, String> metricLabels = new HashMap<String, String>();
		//metricLabels.put("dataset", "set1");
		
		Map<String, String> resourceLabels = new HashMap<String, String>();
		resourceLabels.put("project_id", "fo-ods");
		
		
		p.insertMetricTimeseriesValue(metricLabels, resourceLabels, new DateTime(), Math.random(), "global");
		
		
		
	}

}
