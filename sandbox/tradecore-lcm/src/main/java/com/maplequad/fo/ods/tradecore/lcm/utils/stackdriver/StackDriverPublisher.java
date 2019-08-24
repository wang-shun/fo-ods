package com.maplequad.fo.ods.tradecore.lcm.utils.stackdriver;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.Metric;
import com.google.api.MetricDescriptor;
import com.google.api.MonitoredResource;
import com.google.api.MonitoredResourceDescriptor;
import com.google.api.MetricDescriptor.MetricKind;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.cloud.monitoring.v3.PagedResponseWrappers;
import com.google.monitoring.v3.CreateMetricDescriptorRequest;
import com.google.monitoring.v3.CreateTimeSeriesRequest;
import com.google.monitoring.v3.ListMetricDescriptorsRequest;
import com.google.monitoring.v3.MonitoredResourceDescriptorName;
import com.google.monitoring.v3.Point;
import com.google.monitoring.v3.ProjectName;
import com.google.monitoring.v3.TimeInterval;
import com.google.monitoring.v3.TimeSeries;
import com.google.monitoring.v3.TypedValue;
import com.google.protobuf.util.Timestamps;

public class StackDriverPublisher {
	static Logger logger = LoggerFactory.getLogger(StackDriverPublisher.class);

	private static SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

	MetricServiceClient client;

	static final String metric_domain = "custom.googleapis.com";
	static final String default_metric_type = "custom_measurement2";

	String metric_type;

	// measure a value at a point in time.
	MetricKind metric_kind = MetricDescriptor.MetricKind.GAUGE;;

	String projectResource;

	private String metricName;

	String myproject = "";

	ProjectName myProjectName;

	private StackDriverPublisher(String myproject, String metricType) throws Exception {

		client = MetricServiceClient.create();

		this.myproject = myproject;

		myProjectName = ProjectName.create(this.myproject);

		this.metric_type = metric_domain + "/" + metricType;
		this.projectResource = "projects/" + myproject;
		this.metricName = projectResource + "/metricDescriptors/" + metric_type;

	}

	public static StackDriverPublisher getStackDriverPublisher(String myproject, String metricType) throws Exception {
		StackDriverPublisher p = new StackDriverPublisher(myproject, metricType);

		return p;
	}

	public MetricDescriptor createCustomMetric() throws Exception {
		ProjectName name = ProjectName.create(this.myproject);

		MetricDescriptor descriptor = MetricDescriptor.newBuilder().setType(this.metric_type)
				.setDescription("This is a simple example of a custom metric.").setMetricKind(this.metric_kind)
				.setValueType(MetricDescriptor.ValueType.DOUBLE).build();

		CreateMetricDescriptorRequest request = CreateMetricDescriptorRequest.newBuilder().setNameWithProjectName(name)
				.setMetricDescriptor(descriptor).build();

		MetricDescriptor m = client.createMetricDescriptor(request);

		return m;

	}

	public void describeMonitoredResources() throws IOException {
		// [START monitoring_get_descriptor]
		// Your Google Cloud Platform project ID

		MonitoredResourceDescriptorName name = MonitoredResourceDescriptorName.create(this.myproject, this.metric_type);
		MonitoredResourceDescriptor response = client.getMonitoredResourceDescriptor(name);

		System.out.println("Printing monitored resource descriptor: ");
		System.out.println(response);
		// [END monitoring_get_descriptor]
	}

	public void listMetricDescriptors() throws IOException {
		// [START monitoring_list_descriptors]
		// Your Google Cloud Platform project ID
		ProjectName name = ProjectName.create(this.myproject);

		ListMetricDescriptorsRequest request = ListMetricDescriptorsRequest.newBuilder().setNameWithProjectName(name)
				.build();
		PagedResponseWrappers.ListMetricDescriptorsPagedResponse response = client.listMetricDescriptors(request);

		System.out.println("Listing descriptors: ");

		for (MetricDescriptor d : response.iterateAll()) {
			System.out.println(d.getName() + " " + d.getDisplayName());
		}
		// [END monitoring_list_descriptors]
	}

	public void insertMetricTimeseriesValue(Map<String, String> metricLabels, Map<String, String> resourceLabels,
			DateTime dt, double dValue, String TypeName) throws Exception {

		// Prepares an individual data point
		TimeInterval interval = TimeInterval.newBuilder().setEndTime(Timestamps.fromMillis(dt.getMillis())).build();
		TypedValue value = TypedValue.newBuilder().setDoubleValue(dValue).build();
		Point point = Point.newBuilder().setInterval(interval).setValue(value).build();

		List<Point> pointList = new ArrayList<>();
		pointList.add(point);
		ProjectName name = this.myProjectName;

		// Prepares the metric descriptor
		Metric metric = Metric.newBuilder().setType(this.metric_type).putAllLabels(metricLabels).build();

		// Prepares the monitored resource descriptor
		MonitoredResource resource = MonitoredResource.newBuilder().setType(TypeName).putAllLabels(resourceLabels)
				.build();

		// Prepares the time series request
		TimeSeries timeSeries = TimeSeries.newBuilder().setMetric(metric).setResource(resource).addAllPoints(pointList)
				.build();

		List<TimeSeries> timeSeriesList = new ArrayList<>();
		timeSeriesList.add(timeSeries);

		CreateTimeSeriesRequest request = CreateTimeSeriesRequest.newBuilder().setNameWithProjectName(name)
				.addAllTimeSeries(timeSeriesList).build();

		// Writes time series data
		this.client.createTimeSeries(request);
		//System.out.println("Done writing time series value.");

		return;
	}

	//
	// static Monitoring authenticate(String projectid) throws
	// GeneralSecurityException, IOException {
	// // Grab the Application Default Credentials from the environment.
	// GoogleCredential credential =
	// GoogleCredential.getApplicationDefault().createScoped(MonitoringScopes.all());
	//
	// // Create and return the CloudMonitoring service object
	// HttpTransport httpTransport = new NetHttpTransport();
	// JsonFactory jsonFactory = new JacksonFactory();
	// Monitoring service = new Monitoring.Builder(httpTransport, jsonFactory,
	// credential)
	// .setApplicationName("Monitoring" + projectid).build();
	// return service;
	// }

}
