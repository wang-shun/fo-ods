package com.foods.lifeloader.lifeloaderdummy;

import org.junit.Test;

import com.foods.measure.ExtractF2BResult2CloudStorage;

public class TestExtractEnd2EndStatistic {
	@Test
	public void test() throws Exception{
		ExtractF2BResult2CloudStorage f2b = new ExtractF2BResult2CloudStorage("tradecore-stats","statsdb");
		
		Object link=f2b.ExtractQueryLog("*");
		
		System.out.println(link);
	}
}
