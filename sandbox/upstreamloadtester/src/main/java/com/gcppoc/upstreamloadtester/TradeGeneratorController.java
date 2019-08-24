package com.gcppoc.upstreamloadtester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gcppoc.upstreamloadtester.Request.EquityRequest;
import com.gcppoc.upstreamloadtester.Request.JobQueueRequest;
import com.gcppoc.upstreamloadtester.loadtest.LoadTestInstance;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;

import foods.bigtable.loadtest.EquityTradeGenerator;
import foods.bigtable.model.EquityTrade;
//[END import]
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "API to generate trades"
		+ " parameters", description = "This API provides the capability to test trade", produces = "application/json")
public class TradeGeneratorController {
	Logger logger = LoggerFactory.getLogger(TradeGeneratorController.class);

	final static int maxCall = 100;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String defaultpage(HttpServletRequest request) {
		String req = request.getRequestURL().toString();
		String page = "Hello! please use <a href=\"" + req
				+ "swagger-ui.html#\">link</a> to goto restful function list";
		return page;
	}

	@RequestMapping(value = "/testjobQueue", method = RequestMethod.POST)
	public ResponseEntity<List<TaskHandle>> testjobQueue(@RequestBody JobQueueRequest jobRequest) throws Exception {

		// Add the task to the default queue.
		// [START addQueue]
		Queue queue = QueueFactory.getQueue("bigtestqueue");

		List<TaskOptions> taskOptLst = new ArrayList<TaskOptions>();
		List<TaskHandle> finalList = new ArrayList<TaskHandle>();
		int qsize = jobRequest.numOfLot;

		while (qsize > 0) {
			int runSize = qsize;
			if (runSize > maxCall) {
				runSize = maxCall;
			}
			taskOptLst.clear();
			logger.info("Submiting:" + runSize);
			for (int i = 0; i < runSize; i++) {

				logger.info("submitted:" + i);

				TaskOptions opt = TaskOptions.Builder.withUrl("/worker").param("lot", new Integer(i).toString());
				opt.param("numoftrade", new Integer(jobRequest.numoftradesPerLot).toString());
				taskOptLst.add(opt);

			}
			qsize -= runSize;
			List<TaskHandle> h = queue.add(taskOptLst);
			/*
			 * java.util.concurrent.Future<java.util.List<TaskHandle>>
			 * taskHandles=queue.addAsync(taskOptLst);
			 * 
			 * List<TaskHandle> h =taskHandles.get();
			 */
			finalList.addAll(h);

			logger.info("finished :" + runSize);
		}

		return new ResponseEntity<List<TaskHandle>>(finalList, HttpStatus.OK);
	}

	@RequestMapping(value = "/genericEquityJobQueueMadhav", method = RequestMethod.POST)
	public ResponseEntity<List<TaskHandle>> genericEquityJobQueueMadhav(@RequestBody JobQueueRequest jobRequest)
			throws Exception {

		// Add the task to the default queue.
		// [START addQueue]
		Queue queue = QueueFactory.getQueue("bigtestqueue");

		List<TaskOptions> taskOptLst = new ArrayList<TaskOptions>();
		List<TaskHandle> finalList = new ArrayList<TaskHandle>();
		int qsize = jobRequest.numOfLot;

		while (qsize > 0) {
			int runSize = qsize;
			if (runSize > maxCall) {
				runSize = maxCall;
			}
			taskOptLst.clear();
			logger.info("Submiting:" + runSize);
			for (int i = 0; i < runSize; i++) {

				logger.info("submitted:" + i);

				TaskOptions opt = TaskOptions.Builder.withUrl("/insertcashequityjson").param("lot",
						new Integer(i).toString());
				opt.param("numoftrade", new Integer(jobRequest.numoftradesPerLot).toString());
				taskOptLst.add(opt);

			}
			qsize -= runSize;
			List<TaskHandle> h = queue.add(taskOptLst);
			/*
			 * java.util.concurrent.Future<java.util.List<TaskHandle>>
			 * taskHandles=queue.addAsync(taskOptLst);
			 * 
			 * List<TaskHandle> h =taskHandles.get();
			 */
			finalList.addAll(h);

			logger.info("finished :" + runSize);
		}

		return new ResponseEntity<List<TaskHandle>>(finalList, HttpStatus.OK);
	}

	@ApiOperation(value = "Dummy create equity trade, queueSize: 0 - BIG; 1-MID; 2-SMALL", produces = "application/json")
	@RequestMapping(value = "/dummyEquityJobQueueRoman", method = RequestMethod.POST)
	public ResponseEntity<List<TaskHandle>> dummyEquityJobQueueRoman(@RequestBody JobQueueRequest jobRequest)
			throws Exception {
		List<TaskHandle> finalList = new ArrayList<TaskHandle>();
		String queueName = "bigtestqueue";
		
		queueName = jobRequest.getQueueName(jobRequest.queueSize);
		Queue queue = QueueFactory.getQueue(queueName);
		logger.info("Chosen queue:"+queueName);
		List<TaskOptions> taskOptLst = new ArrayList<TaskOptions>();

		
		
		int qsize = jobRequest.numOfLot;

		while (qsize > 0) {
			int runSize = qsize;
			if (runSize > maxCall) {
				runSize = maxCall;
			}
			taskOptLst.clear();
			logger.info("Submiting:" + runSize);
			for (int i = 0; i < runSize; i++) {

				logger.info("submitted:" + i);

				TaskOptions opt = TaskOptions.Builder.withUrl("/dummycashequity").param("lot",
						new Integer(i).toString());
				opt.param("numoftrade", new Integer(jobRequest.numoftradesPerLot).toString());
				opt.param("numofversionPerTrade", new Integer(jobRequest.numofVersionPerTrade).toString());
				taskOptLst.add(opt);

			}
			qsize -= runSize;
			// List<TaskHandle> h =queue.add(taskOptLst);

			java.util.concurrent.Future<java.util.List<TaskHandle>> taskHandles = queue.addAsync(taskOptLst);

			List<TaskHandle> h = taskHandles.get();
			finalList.addAll(h);

			logger.info("finished :" + runSize);
		}

		return new ResponseEntity<List<TaskHandle>>(finalList, HttpStatus.OK);
	}

	@ApiOperation(value = "GRPC create equity trade, queueSize: 0 - BIG; 1-MID; 2-SMALL", produces = "application/json")
	@RequestMapping(value = "/grpcEquityJobQueueRoman", method = RequestMethod.POST)
	public ResponseEntity<List<TaskHandle>> grpcEquityJobQueueRoman(@RequestBody JobQueueRequest jobRequest)
			throws Exception {
		List<TaskHandle> finalList = new ArrayList<TaskHandle>();
		List<TaskOptions> taskOptLst = new ArrayList<TaskOptions>();
		
		String queueName = jobRequest.getQueueName(jobRequest.queueSize);
		Queue queue = QueueFactory.getQueue(queueName);
		logger.info("Chosen queue:"+queueName);

		int qsize = jobRequest.numOfLot;

		while (qsize > 0) {
			int runSize = qsize;
			if (runSize > maxCall) {
				runSize = maxCall;
			}
			taskOptLst.clear();
			logger.info("Submiting:" + runSize);
			for (int i = 0; i < runSize; i++) {

				logger.info("submitted:" + i);

				TaskOptions opt = TaskOptions.Builder.withUrl("/insertcashequitygrpc").param("lot",
						new Integer(i).toString());
				opt.param("numoftrade", new Integer(jobRequest.numoftradesPerLot).toString());
				opt.param("numofversionPerTrade", new Integer(jobRequest.numofVersionPerTrade).toString());
				taskOptLst.add(opt);

			}
			qsize -= runSize;
			// List<TaskHandle> h =queue.add(taskOptLst);

			java.util.concurrent.Future<java.util.List<TaskHandle>> taskHandles = queue.addAsync(taskOptLst);

			List<TaskHandle> h = taskHandles.get();
			finalList.addAll(h);

			logger.info("finished :" + runSize);
		}

		return new ResponseEntity<List<TaskHandle>>(finalList, HttpStatus.OK);
	}

	@ApiOperation(value = "HBASE create equity trade, queueSize: 0 - BIG; 1-MID; 2-SMALL", produces = "application/json")
	@RequestMapping(value = "/equityjobQueueRoman", method = RequestMethod.POST)
	public ResponseEntity<List<TaskHandle>> equityjobQueueRoman(@RequestBody JobQueueRequest jobRequest)
			throws Exception {

		// Add the task to the default queue.
		// [START addQueue]
		String queueName = jobRequest.getQueueName(jobRequest.queueSize);
		Queue queue = QueueFactory.getQueue(queueName);
		logger.info("Chosen queue:"+queueName);
		
		
		List<TaskOptions> taskOptLst = new ArrayList<TaskOptions>();
		List<TaskHandle> finalList = new ArrayList<TaskHandle>();
		int qsize = jobRequest.numOfLot;

		while (qsize > 0) {
			int runSize = qsize;
			if (runSize > maxCall) {
				runSize = maxCall;
			}
			taskOptLst.clear();
			logger.info("Submiting:" + runSize);
			for (int i = 0; i < runSize; i++) {

				logger.info("submitted:" + i);

				TaskOptions opt = TaskOptions.Builder.withUrl("/insertcashequity").param("lot",
						new Integer(i).toString());
				opt.param("numoftrade", new Integer(jobRequest.numoftradesPerLot).toString());
				opt.param("numofversionPerTrade", new Integer(jobRequest.numofVersionPerTrade).toString());
				taskOptLst.add(opt);

			}
			qsize -= runSize;
			List<TaskHandle> h = queue.add(taskOptLst);
			/*
			 * java.util.concurrent.Future<java.util.List<TaskHandle>>
			 * taskHandles=queue.addAsync(taskOptLst);
			 * 
			 * List<TaskHandle> h =taskHandles.get();
			 */
			finalList.addAll(h);

			logger.info("finished :" + runSize);
		}

		return new ResponseEntity<List<TaskHandle>>(finalList, HttpStatus.OK);
	}

	@RequestMapping(value = "/singlelotinsertcashequity", method = RequestMethod.POST)
	public ResponseEntity<Collection<EquityTrade>> insertEquityTrade(HttpServletRequest request,
			@RequestBody EquityRequest jobRequest) throws Exception {
		logger.info("Worker is processing " + jobRequest.numoftrades);

		LoadTestInstance loader = LoadTestInstance.getContext().getBean("LoadTestInstance", LoadTestInstance.class);
		EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
		Collection<EquityTrade> trades = tradeGenerator.createTrades(jobRequest.numoftrades, jobRequest.numofversion);
		loader.save(trades);
		//loader.close();
		return new ResponseEntity<Collection<EquityTrade>>(trades, HttpStatus.OK);
	}
}
