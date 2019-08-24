package com.foods.lifeloader.lifeloaderdummy.Publisher;

import com.maplequad.fo.ods.tradecore.lcm.services.TradeGenerateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.foods.lifeloaderutility.CreateUUIDHelper;
import com.foods.measure.ExtractF2BResult2CloudStorage;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.PlainConsumer;
import com.maplequad.fo.ods.tradecore.lcm.utils.pubsub.PlainPublisher;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@RestController
@Api(value = "API to generate testpubsub"
		+ " parameters", description = "This API provides the publish capability to pub sub", produces = "application/json")
public class LifeLoaderPubSubPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(LifeLoaderPubSubPublisher.class);
	ApplicationContext context=null;
	//MessagePublisher publisher = null;
	//ObjectMapper mapper = new ObjectMapper();
	//TradeGeneratorService generater=null;
	//PlainPublisher publisher = null;
	//PlainConsumer consumer = null;
	ThreadPoolTaskExecutor taskExecutor=null;
	
	Map<String,PlainPublisher> myPublisherMap=null;
	List<PlainConsumer> myConsumerLst=null;

	public LifeLoaderPubSubPublisher(String topicId) throws Exception{
		this.prepareTopic();

	}

	public LifeLoaderPubSubPublisher() throws Exception{
		/*
		String topicid = System.getenv("PUBSUB_TOPIC");
		
		LOGGER.info("My topic is"+ topicid);
		*/
		this.prepareTopic();
	}

	public void prepareTopic() throws Exception{
		context = new ClassPathXmlApplicationContext("SpringBean.xml");
		//generater = context.getBean("TradeGeneratorService", TradeGeneratorService.class);

		myPublisherMap = context.getBean("PubShooterMap",Map.class);
		myConsumerLst = context.getBean("ConsumerList",List.class);
		 //publisher = context.getBean("PlainPublisher", PlainPublisher.class);
		 //consumer = context.getBean("PlainConsumer", PlainConsumer.class);
		 taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");

		//consumer.start();
		myConsumerLst.forEach( (c) -> { c.start(); });
		
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String defaultpage(HttpServletRequest request) {
		String req = request.getRequestURL().toString();
		String page = "Hello! please use <a href=\"" + req
				+ "swagger-ui.html#\">link</a> to goto restful function list";
		return page;
	}

	@ApiOperation(value = "publish random new trades via pub/sub: cash-eq = cash equity, fi-irs = swap, fx-fwd = fxfwd", produces = "application/json")
	@RequestMapping(value = "/publishRandomTrade", method = RequestMethod.POST)
	public ResponseEntity <  TrackLog > publishRandomTrade(int numberOfTrade,@RequestBody TradeGenerateRequest tradeSpec) throws Exception {
		LOGGER.debug("Received parameter:"+numberOfTrade);

		checkTradeType(tradeSpec.TradeType);

		TrackLog r = new TrackLog();
		r.serialNumber=String.valueOf(CreateUUIDHelper.createUUID());
		r.tradetype=tradeSpec.TradeType;
		r.numOfTrades=numberOfTrade;
		r.operation="randomnew";
		r.tradeSpec=tradeSpec;

		this.myPublisherMap.get(tradeSpec.TradeType).putQueue(r);

		//publisher.putQueue(r);
		
		LOGGER.info(String.valueOf(r.serialNumber));
	    return new ResponseEntity< TrackLog >(r, HttpStatus.OK);
	}

	private void checkTradeType(String tradetype) throws Exception{
		if(!tradetype.equals("cash-eq") && !tradetype.equals("fi-irs") && !tradetype.equals("fx-fwd")){
			throw new Exception("Trade Type not supported!... only support cash-eq,fi-irs,fx-fwd");
		}

	}
	
	
	
	@ApiOperation(value = "amend random trades via pub/sub: cash-eq = cash equity, fi-irs = swap, fx-fwd = fxfwd", produces = "application/json")
	@RequestMapping(value = "/amendRandomTrade", method = RequestMethod.POST)
	public ResponseEntity <  TrackLog > amendRandomTrade(int numberOfTrade,@RequestBody TradeGenerateRequest tradeSpec) throws Exception {
		String TradeType=tradeSpec.TradeType;
		LOGGER.debug("Received parameter:"+TradeType+","+numberOfTrade);
		
		TrackLog r = new TrackLog();
		r.serialNumber=String.valueOf(CreateUUIDHelper.createUUID());
		r.numOfTrades=numberOfTrade;
		r.tradetype=TradeType;
		r.numOfTrades=numberOfTrade;
		r.operation="radomnew+amend";
		r.tradeSpec=tradeSpec;
		//publisher.putQueue(r);
		this.myPublisherMap.get(tradeSpec.TradeType).putQueue(r);
		
	    return new ResponseEntity< TrackLog >(r, HttpStatus.OK);
	}


	@ApiOperation(value = "extract performance status from spanner to cloud storage", produces = "application/json")
	@RequestMapping(value = "/runExtractStatus2CloudStorage", method = RequestMethod.POST)
	public ResponseEntity< ExtractStatus > runExtractStatus2CloudStorage(String serialnumber) throws Exception {

		ExtractF2BResult2CloudStorage f2b = this.context.getBean("ExtractF2BResult2CloudStorage",ExtractF2BResult2CloudStorage.class);

		ExtractF2BResult2CloudStorage.Info link=f2b.ExtractQueryLog(serialnumber);
		ExtractStatus e = new ExtractStatus();
		e.link=link.selfLink;
		e.gsLink=link.gsLink;
		e.instance=f2b.spannerInstanceId;
		e.DB=f2b.spannerDbId;

		return new ResponseEntity< ExtractStatus >(e, HttpStatus.OK);

	}
	
	
}
