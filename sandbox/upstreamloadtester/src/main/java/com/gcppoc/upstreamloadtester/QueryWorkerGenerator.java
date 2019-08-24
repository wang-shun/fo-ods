package com.gcppoc.upstreamloadtester;

import com.maplequad.fo.ods.ulg.dao.ChannelHandle;
import com.maplequad.fo.ods.ulg.dao.DatabaseHandle;
import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.RealRepository;
import foods.bigtable.repository.Repository;
import foods.traderepository.proto.GetLatestVersionRequest;
import foods.traderepository.proto.GetLatestVersionResponse;
import foods.traderepository.proto.TradeRepositoryGrpc;
import io.grpc.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;

@Controller
public class QueryWorkerGenerator {

	private static Logger LOGGER = LoggerFactory.getLogger(QueryWorkerGenerator.class);
	private static final String PROJECT_ID = "fo-ods";
    private static final String INSTANCE_ID = "repository-test";
    private static final String COMMA = ",";
    private static final String REPO_HOST_NAME = "35.195.139.204";
    private static final int PORT = 8888;


	@RequestMapping(value = "/localQueryCashEquity", method = RequestMethod.POST)
	public ResponseEntity <  String > querycashequitylocal1(HttpServletRequest request) throws Exception{
		String task = request.getParameter("task");
		String[] tradeIds = request.getParameter("tradeIds").split(COMMA);
        LOGGER.info("Worker is starting to process task # {} with numoftrades = {}", task, tradeIds.length);
		Repository repo = new RealRepository(DatabaseHandle.getInstance(PROJECT_ID,INSTANCE_ID).getConnection());
		for (String tradeId: tradeIds) {
			EquityTrade trade = repo.getLatest(TradeId.tradeId(tradeId));
		}
        LOGGER.info("Worker has processed task # {} with numoftrades = {}", task, tradeIds.length);
        return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/grpcQueryCashEquity", method = RequestMethod.POST)
	public ResponseEntity <  String > querycashequitygrpc(HttpServletRequest request) throws Exception{
        String task = request.getParameter("task");
        String[] tradeIds = request.getParameter("tradeIds").split(COMMA);
        Channel channel = ChannelHandle.getInstance(REPO_HOST_NAME, PORT).getChannel();
		TradeRepositoryGrpc.TradeRepositoryBlockingStub blockingStub = TradeRepositoryGrpc.newBlockingStub(channel);
        LOGGER.info("Worker is starting to process task # {} with numoftrades = {}", task, tradeIds.length);
		for (String tradeId: tradeIds) {
			GetLatestVersionRequest grpcRequest = GetLatestVersionRequest.newBuilder().setTradeId(tradeId).build();
			GetLatestVersionResponse trade = blockingStub.getLatestVersion(grpcRequest);
		}
        LOGGER.info("Worker has processed task # {} with numoftrades = {}", task, tradeIds.length);
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}

}
