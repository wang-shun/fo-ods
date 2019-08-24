package com.gcppoc.foods.stackdriver;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.monitoring.v3.Monitoring;
import com.google.api.services.monitoring.v3.MonitoringScopes;
import com.google.api.services.monitoring.v3.model.CreateTimeSeriesRequest;
import com.google.api.services.monitoring.v3.model.LabelDescriptor;
import com.google.api.services.monitoring.v3.model.ListMetricDescriptorsResponse;
import com.google.api.services.monitoring.v3.model.Metric;
import com.google.api.services.monitoring.v3.model.MetricDescriptor;
import com.google.api.services.monitoring.v3.model.MonitoredResource;
import com.google.api.services.monitoring.v3.model.Point;
import com.google.api.services.monitoring.v3.model.TimeInterval;
import com.google.api.services.monitoring.v3.model.TimeSeries;
import com.google.api.services.monitoring.v3.model.TypedValue;
import com.google.common.collect.Lists;

public class StackDriverPublisher {
	static Logger logger = LoggerFactory.getLogger(StackDriverPublisher.class);

	private static SimpleDateFormat rfc3339 =
		      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
	Monitoring monitoringService = null;

	static final String metric_domain = "custom.googleapis.com";
	static final String default_metric_type="custom_measurement2";
	
	String metric_type;

	// measure a value at a point in time.
	String metric_kind = "GAUGE";
	String projectResource;

	private String metricName;

	String myproject = "";

	private StackDriverPublisher(String myproject) {
		this.myproject = myproject;

		this.metric_type = metric_domain + "/" + this.default_metric_type;
		this.projectResource = "projects/" + myproject;
		this.metricName = projectResource + "/metricDescriptors/" + metric_type;

	}

	public static StackDriverPublisher getStackDriverPublisher(String myproject) throws Exception {
		StackDriverPublisher p = new StackDriverPublisher(myproject);
		p.monitoringService = authenticate(myproject);

		MetricDescriptor metricDescriptor = p.createCustomMetric();

		logger.info("listMetricDescriptors response");
		logger.info(metricDescriptor.toPrettyString());
		
		while (p.getCustomMetric() == null) {
		      Thread.sleep(2000);
		    }
		return p;
	}
	
	public void insertMetricTimeseriesValue(Map<String, String> metricLabel, Map<String, String> resourceLabel, DateTime dt,double value) throws Exception{
		
		CreateTimeSeriesRequest timeSeriesRequest = new CreateTimeSeriesRequest();
	    TimeSeries timeSeries = new TimeSeries();

	    Metric metric = new Metric();
	    metric.setType(this.metric_type);

	    metric.setLabels(metricLabel);
	    timeSeries.setMetric(metric);
	    MonitoredResource monitoredResource = new MonitoredResource();
	    monitoredResource.setType("gce_instance");
	    monitoredResource.setLabels(resourceLabel);
	    timeSeries.setResource(monitoredResource);
	    Point point = new Point();
	    TimeInterval ti = new TimeInterval();
	    String now = rfc3339.format(dt.toDate());
	    ti.setStartTime(now);
	    ti.setEndTime(now);

	    point.setInterval(ti);
	    point.setValue(new TypedValue().setDoubleValue(value));

	    timeSeries.setPoints(Lists.<Point>newArrayList(point));

	    timeSeriesRequest.setTimeSeries(Lists.<TimeSeries>newArrayList(timeSeries));
	    monitoringService.projects().timeSeries().create(projectResource, timeSeriesRequest).execute();
	}
	
	public MetricDescriptor getCustomMetric() throws IOException {
	    Monitoring.Projects.MetricDescriptors.List metrics =
	        monitoringService.projects().metricDescriptors()
	            .list(projectResource);
	    metrics.setFilter("metric.type=\"" + this.metric_type + "\"");
	    ListMetricDescriptorsResponse response = metrics.execute();
	    List<MetricDescriptor> descriptors = response.getMetricDescriptors();
	    logger.info("reading custom metric");
	    if (descriptors == null || descriptors.isEmpty()) {
	      logger.info("No metric descriptor matching that label found.");
	      return null;
	    } else {
	      logger.info(descriptors.get(0).toPrettyString());
	      return descriptors.get(0);
	    }
	  }

	public MetricDescriptor createCustomMetric() throws Exception {
		MetricDescriptor metricDescriptor = new MetricDescriptor();

		metricDescriptor.setName(metricName);
		metricDescriptor.setType(metric_type);

		LabelDescriptor labelDescriptor = new LabelDescriptor();
		labelDescriptor.setKey("environment");
		labelDescriptor.setValueType("STRING");
		labelDescriptor.setDescription("An arbitrary measurement.");
		labelDescriptor.setDescription("Custom Metric");
		List<LabelDescriptor> labelDescriptorList = new ArrayList<LabelDescriptor>();
		labelDescriptorList.add(labelDescriptor);
		metricDescriptor.setLabels(labelDescriptorList);

		metricDescriptor.setMetricKind(metric_kind);
		metricDescriptor.setValueType("DOUBLE");
		// Fake custom metric with unit 'items'
		metricDescriptor.setUnit("items");

		MetricDescriptor descriptorResponse = this.monitoringService.projects().metricDescriptors()
				.create(projectResource, metricDescriptor).execute();
		logger.info("create response" + descriptorResponse.toPrettyString());
		return descriptorResponse;

	}

	static Monitoring authenticate(String projectid) throws GeneralSecurityException, IOException {
		// Grab the Application Default Credentials from the environment.
		GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(MonitoringScopes.all());

		// Create and return the CloudMonitoring service object
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		Monitoring service = new Monitoring.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName("Monitoring" + projectid).build();
		return service;
	}

}
