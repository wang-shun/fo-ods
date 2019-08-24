package com.foods.lifeloader.lifeloadercreator;

import org.junit.Test;

public class TestReceiveMessage {

	@Test
	public void test() throws Exception{
		/*
		String data="[{\"sequenceNumber\":\"4877116949644135522\",\"symbol\":\"GOOG.L\",\"side\":\"1\",\"sophisEntityId\":\"BBB\",\"amount\":\"1190200.1\",\"sophisFolioId\":\"290456\",\"exchangeExecutionId\":\"FE-BMAO-241\",\"executionType\":\"1\",\"assetClass\":\"EQ\",\"tradeDate\":\"2017-08-11\",\"version\":\"1\",\"cfiCode\":\"ES\",\"bookingId\":\"a0fbaae4-d4e3-40c2-86d4-26de850de333\",\"executionTime\":\"1502488625850\",\"quantityFilled\":\"629\",\"market\":\"LSE\",\"sophisCouterpartyId\":\"UUU\",\"price\":\"1892.21\",\"emsOrderId\":\"LNNBABK6PF000\",\"noOfLegs\":\"1\",\"bookingStatus\":\"NEW\",\"currency\":\"EUR\",\"username\":\"mattandr\"},{\"sequenceNumber\":\"6295597640045379795\",\"symbol\":\"GOOG.L\",\"side\":\"1\",\"sophisEntityId\":\"III\",\"amount\":\"3017522.5\",\"sophisFolioId\":\"290556\",\"exchangeExecutionId\":\"FE-EQER-885\",\"executionType\":\"1\",\"assetClass\":\"EQ\",\"tradeDate\":\"2017-08-11\",\"version\":\"1\",\"cfiCode\":\"ES\",\"bookingId\":\"14ccb28e-286d-4fff-ac71-f10c415029a2\",\"executionTime\":\"1502488625850\",\"quantityFilled\":\"898\",\"market\":\"FEX\",\"sophisCouterpartyId\":\"SSS\",\"price\":\"3360.27\",\"emsOrderId\":\"LNNBABK6PF000\",\"noOfLegs\":\"5\",\"bookingStatus\":\"NEW\",\"currency\":\"GBX\",\"username\":\"bondjames\"}]";
		String payload = Base64.getEncoder().encodeToString(data.getBytes());
		Message m = new Message();
		m.setData(payload);
		m.setMessageId("xxx");
		m.setPublishTime("20170101");
		
		
		LifeLoaderPubSubSubsriber p = new LifeLoaderPubSubSubsriber("we_eat_for_life");
		System.out.println(m.getData());
		
		String t=new String(Base64.getDecoder().decode(m.getData()));
		
		assert(t.equals(data));
		

		
		List<Map <String, String> >ourmap = p.processJsonStr(t);
		

		String newpayload = new ObjectMapper().writeValueAsString(ourmap);
		
		System.out.println(newpayload);
		assert(newpayload.equals(data));
		*/
		
	}

}
