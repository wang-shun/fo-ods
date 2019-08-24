package com.gcppoc.upstreamloadtester;

import com.gcppoc.upstreamloadtester.Request.JobQueueRequest;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import foods.bigtable.model.EquityTrade;
import foods.bigtable.repository.Database;
import foods.bigtable.repository.RealRepository;
import foods.bigtable.repository.Repository;
import foods.bigtable.repository.query.Query;
import foods.bigtable.repository.query.SimpleQuery;
import foods.bigtable.repository.query.predicate.BookPredicate;
import foods.bigtable.repository.query.predicate.Predicate;
import io.swagger.annotations.Api;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

//[END import]

@RestController
@Api(value = "API to query trades"
		+ " parameters", description = "This API provides the capability to query trades", produces = "application/json")
public class QueryTaskGenerator {
	Logger LOGGER = LoggerFactory.getLogger(QueryTaskGenerator.class);
    private static final int MAX_RESULT_SIZE = 10000;
    private static final int MAX_LOAD_SIZE = 100;
    private static final String COMMA = ";";
    private static final String PROJECT_ID = "fo-ods";
    private static final String INSTANCE_ID = "repository-test";

	private ResponseEntity <List<TaskHandle>> handleRequest(JobQueueRequest jobRequest, String url, boolean aSyncFlag)
            throws Exception{
        LOGGER.info("Entered handleRequest");
		// Add the task to the default queue.
		// [START addQueue]

		List<TaskOptions> taskOptLst = new ArrayList<TaskOptions>();
		List<TaskHandle> finalList = new ArrayList<TaskHandle>();
		int numberOfTrades = jobRequest.getNumberOfTrades();
		int batchSize = jobRequest.getBatchSize();
		int numberOfTasks = numberOfTrades / batchSize;

        //Deriving Queue Name based on the batchSize
		Queue queue = QueueFactory.getQueue(JobQueueRequest.getQueueName(jobRequest.queueSize));

		//Creating connection to BigTable
		Connection conn = Database.createConnection(PROJECT_ID, INSTANCE_ID);
		Repository repo = new RealRepository(conn);
		Predicate predicate = BookPredicate.book("BOOK2");
		Query query = SimpleQuery.forAll(MAX_RESULT_SIZE, predicate);
        StringBuilder tradeListBuilder;
		List<EquityTrade> tradeList = repo.find(query);
		int tradeListSize = tradeList.size();
        LOGGER.info("Retrieved total {} trades..", tradeList.size());
        String tradeListStr;
        LOGGER.info("Submitting total {} tasks..", numberOfTasks);
        int lCount = 0;
        for (int tCount = 1; tCount <= numberOfTasks; tCount++) {
            tradeListBuilder = new StringBuilder();
            taskOptLst.clear();
            TaskOptions opt = TaskOptions.Builder.withUrl(url).param("task", new Integer(tCount).toString());
            for (int bCount = 0; bCount < batchSize; bCount++) {
                tradeListBuilder.append(tradeList.get(new Random().nextInt(tradeListSize)).getTradeId().getId());
                tradeListBuilder.append(COMMA);
            }
            tradeListStr = tradeListBuilder.toString();
            opt.param("tradeIds", tradeListStr.substring(0, tradeListStr.length() - 1));
            taskOptLst.add(opt);
            lCount++;
            if(lCount == 100 || tCount == numberOfTasks) {
                List<TaskHandle> taskHandleList = null;
                if (aSyncFlag) {
                    Future<List<TaskHandle>> taskHandles = queue.addAsync(taskOptLst);
                    taskHandleList = taskHandles.get();
                } else {
                    taskHandleList = queue.add(taskOptLst);
                }
                finalList.addAll(taskHandleList);
                lCount = 0;
            }
            LOGGER.info("Submitted Task {}", tCount);
        }
        LOGGER.info("Exited handleRequest");
		return new ResponseEntity< List<TaskHandle> > (finalList,HttpStatus.OK);
	}


	@RequestMapping(value = "/localEquityQueryJQ", method = RequestMethod.POST)
	public ResponseEntity <  List<TaskHandle> > localEquityQueryJobQueue(@RequestBody JobQueueRequest jobRequest) throws Exception{
		return handleRequest(jobRequest,"/localQueryCashEquity", false);
	}

	@RequestMapping(value = "/grpcEquityQueryJQ", method = RequestMethod.POST)
	public ResponseEntity <  List<TaskHandle> > grpcEquityQueryJobQueue(@RequestBody JobQueueRequest jobRequest) throws Exception{
		return handleRequest(jobRequest,"/grpcQueryCashEquity", true);
	}

}
