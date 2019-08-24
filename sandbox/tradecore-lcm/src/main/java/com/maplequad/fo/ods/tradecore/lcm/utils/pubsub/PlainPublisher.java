package com.maplequad.fo.ods.tradecore.lcm.utils.pubsub;

import java.util.concurrent.BlockingQueue;

public class PlainPublisher {
	private final BlockingQueue queue;

	PlainPublisher(BlockingQueue q) {
		queue = q;
	}

	

	public boolean putQueue(Object o) {
		try {
			queue.put(o);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
