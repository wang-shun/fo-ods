package com.maplequad.fo.ods.tradecore.lcm.processor;

import java.util.List;

import com.maplequad.fo.ods.tradecore.lcm.utils.CloudStorageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractSpannerServiceProcessor implements TradeProcessorInterface, ApplicationContextAware {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSpannerServiceProcessor.class);
	protected ApplicationContext context = null;
	
	public boolean enableStatisticOut=false;
	
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.context = arg0;

	}

	public void pushStatistics2CloudStorage(com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog l) {
		if(l==null)
			return;
		if(!enableStatisticOut)
			return;
		String serialnum =l.serialNumber;
		int orderNUM=l.subnumber;
		
		try {
			CloudStorageHelper helper = context.getBean("CloudStorageHelper", CloudStorageHelper.class);
			String filePath = CloudStorageHelper.adviseFileName("spannerservicelayer", l.action, String.valueOf(serialnum),
					orderNUM, "json");
			LOGGER.info("Begin to write Track Log link");
			String link = helper.saveObject2Jason(l, filePath);
			LOGGER.info("End to write Track Log link:" + link);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void pushStatistics2CloudStorage(List<com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog> lst) {
		if(lst.size()==0)
			return;
		if(!enableStatisticOut)
			return;
		String serialnum = lst.get(0).serialNumber;
		int orderNUM=lst.get(0).subnumber;
		
		try {
			CloudStorageHelper helper = context.getBean("CloudStorageHelper", CloudStorageHelper.class);
			String filePath = CloudStorageHelper.adviseFileName("loader", "spannerservicelayer", String.valueOf(serialnum),
					orderNUM, "json");
			LOGGER.info("Begin to write Track Log link");
			String link = helper.saveObject2Jason(lst, filePath);
			LOGGER.info("End to write Track Log link:" + link);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
