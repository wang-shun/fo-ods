package com.gcppoc.upstreamloadtester.Request;

public class JobQueueRequest {

	public final static int BIG2=3;
	public final static int BIG=0;
	public final static int MID=1;
	public final static int SMALL=2;
	
	public static String getQueueName(int size){
		if(size==BIG){
			return "bigtestqueue";
		}else if(size==MID){
			return "midtestqueue";
		}else if(size==SMALL){
			return "smalltestqueue";
		}else{
			return "bigtestqueue2";
		}
	}
	
	public int numOfLot;
	public int numoftradesPerLot;
	public int numofVersionPerTrade=1;
	public int queueSize=BIG;

	private int numberOfTrades;
	private int batchSize;

    public int getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(int numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
