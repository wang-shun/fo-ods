package com.foods.lifeloader.lifeloaderdummy;

import org.junit.Test;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.maplequad.fo.ods.tradecore.store.data.access.service.SpannerDatabaseService;

public class TestSpannerQuery {
	
	
	/*
	 * @Test
	public void test() throws Exception {
		SpannerDatabaseService service = SpannerDatabaseService.getInstance();
		
		DatabaseClient dbClient=service.getDBClient();
		
		ResultSet resultSet =
			      dbClient
			          .singleUse()
			          .executeQuery(Statement.of("select tradeid, ostradeid from trade where lower(productType)='cash-eq'  limit 10"));
			  while (resultSet.next()) {
				  Trade t = new Trade();
				  t.setTradeId(resultSet.getLong(0));
				  t.setOsTradeId( resultSet.getString(1));
			    System.out.printf(
			        "%d %s \n", t.getTradeId(), t.getOsTradeId());
			  }
		
		
	}*/
	
	
	@Test
	public void test() throws Exception{
		SpannerDatabaseService service = SpannerDatabaseService.getInstance();
		
		System.out.println("Test query by tradeid");
		DatabaseClient dbClient=service.getDBClient();
		
		StringBuffer sb=new StringBuffer();
		sb.append("select ostradeid,tradeid from Trade@{FORCE_INDEX=searchByOSTradeID} where ostradeid='");
		sb.append("5784340129014367973");
		sb.append("';");
		
		ResultSet resultSet =
		        dbClient
		            .singleUse()
		            .executeQuery(
		                // We use FORCE_INDEX hint to specify which index to use. For more details see
		                // https://cloud.google.com/spanner/docs/query-syntax#from-clause
		                Statement.of(sb.toString()));
		while (resultSet.next()) {
		      System.out.printf(
		          "%s %s\n",
		          resultSet.getString("ostradeid"),resultSet.getLong("tradeid"));
		    }
		
	}
}
