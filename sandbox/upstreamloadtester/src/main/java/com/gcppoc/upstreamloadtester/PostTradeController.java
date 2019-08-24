package com.gcppoc.upstreamloadtester;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gcppoc.upstreamloadtester.loadtest.LoadGrpcTestInstance;
import com.gcppoc.upstreamloadtester.loadtest.LoadTestInstance;
import com.maplequad.fo.ods.ulg.app.GenericLoadTestInstance;
import com.maplequad.fo.ods.ulg.services.TradeConverterService;
import com.maplequad.fo.ods.ulg.services.TradeGeneratorService;

import foods.bigtable.loadtest.EquityTradeGenerator;
import foods.bigtable.model.EquityTrade;

@Controller
public class PostTradeController {
	Logger logger = LoggerFactory.getLogger(PostTradeController.class);
	
	
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public ResponseEntity <  String > test(HttpServletRequest request) {
		
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/worker", method = RequestMethod.POST)
	public ResponseEntity <  String > worker(HttpServletRequest request) {
		String key = request.getParameter("lot");
	    String extra = request.getParameter("numoftrade");
	    // Do something with key.
	    // [START_EXCLUDE]
	    logger.info("Worker is processing " + key+":"+extra);
		
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/insertcashequity", method = RequestMethod.POST)
	public ResponseEntity <  String > insertcashequity(HttpServletRequest request) throws Exception{
		String key = request.getParameter("lot");
		String _numOfTrade = request.getParameter("numoftrade");
		String _numOfTradePerVersion = request.getParameter("numofversionPerTrade");
		
		logger.info("Worker is processing " + key+":with numoftrades:"+_numOfTrade + " numofversion:"+_numOfTradePerVersion);
		int numOfTrade = Integer.parseInt(_numOfTrade);
		int numOfVersionPerTrade = Integer.parseInt(_numOfTradePerVersion);
		
		LoadTestInstance loader = LoadTestInstance.getContext().getBean("LoadTestInstance",LoadTestInstance.class);
		EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        Collection<EquityTrade> trades = tradeGenerator.createTrades(numOfTrade,numOfVersionPerTrade);
        loader.save(trades);
		
		
		logger.info("Worker finished processing " + key+":with numoftrades:"+_numOfTrade + " numofversion:"+_numOfTradePerVersion);
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/insertcashequityjson", method = RequestMethod.POST)
	public ResponseEntity <  String > insertcashequityjson(HttpServletRequest request) throws Exception{
		String key = request.getParameter("lot");
		String _numOfTrade = request.getParameter("numoftrade");
		String assetClass="cash-eq";
		logger.info("Worker is processing " + key+":with numoftrades:"+_numOfTrade);
		int numOfTrade = Integer.parseInt(_numOfTrade);
		
		GenericLoadTestInstance loader = GenericLoadTestInstance.getContext().getBean("GenericLoadTestInstance",GenericLoadTestInstance.class);
		TradeGeneratorService genService=null;
	    TradeConverterService conService=null;
	    
	    genService = new TradeGeneratorService(assetClass);
        conService = new TradeConverterService(assetClass);
        
        loader.save(conService.serve(genService.serve(numOfTrade)));
        logger.info("trade saved");
		
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/insertcashequitygrpc", method = RequestMethod.POST)
	public ResponseEntity <  String > insertcashequitygrpc(HttpServletRequest request) throws Exception{
		String key = request.getParameter("lot");
		String _numOfTrade = request.getParameter("numoftrade");
		String _numOfTradePerVersion = request.getParameter("numofversionPerTrade");
		
		logger.info("Worker is processing " + key+":with numoftrades:"+_numOfTrade + " numofversion:"+_numOfTradePerVersion);
		int numOfTrade = Integer.parseInt(_numOfTrade);
		int numOfVersionPerTrade = Integer.parseInt(_numOfTradePerVersion);
		
		LoadGrpcTestInstance loader = LoadGrpcTestInstance.getContext().getBean("GrpcLoadTestInstance",LoadGrpcTestInstance.class);
		assert(loader!=null);
			
		EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        Collection<EquityTrade> trades = tradeGenerator.createTrades(numOfTrade,numOfVersionPerTrade);
        loader.save(trades);
        logger.info("Worker finished processing " + key+":with numoftrades:"+_numOfTrade + " numofversion:"+_numOfTradePerVersion);
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
	
	@RequestMapping(value = "/dummycashequity", method = RequestMethod.POST)
	public ResponseEntity <  String > dummycashequity(HttpServletRequest request) throws Exception{
		String key = request.getParameter("lot");
		String _numOfTrade = request.getParameter("numoftrade");
		String _numOfTradePerVersion = request.getParameter("numofversionPerTrade");
		
		logger.info("Worker is processing " + key+":with numoftrades:"+_numOfTrade + " numofversion:"+_numOfTradePerVersion);
		int numOfTrade = Integer.parseInt(_numOfTrade);
		int numOfVersionPerTrade = Integer.parseInt(_numOfTradePerVersion);
		
		
			
		EquityTradeGenerator tradeGenerator = new EquityTradeGenerator();
        Collection<EquityTrade> trades = tradeGenerator.createTrades(numOfTrade,numOfVersionPerTrade);
        logger.info("Worker finished processing " + key+":with numoftrades:"+_numOfTrade + " numofversion:"+_numOfTradePerVersion);
		return new ResponseEntity< String > ("done",HttpStatus.OK);
	}
}
