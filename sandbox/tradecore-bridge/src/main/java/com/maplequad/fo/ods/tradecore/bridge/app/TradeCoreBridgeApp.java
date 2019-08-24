package com.maplequad.fo.ods.tradecore.bridge.app;

import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.bridge.service.PubSubBridgeService;
import com.maplequad.fo.ods.tradecore.bridge.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * TradeCoreBridgeApp
 *
 * This is the main class that creates a bridge between the source Topic and the destination topic.
 *
 * @author Madhav Mindhe
 * @since :   07/09/2017
 */
public class TradeCoreBridgeApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeCoreBridgeApp.class);

    public static void main(String[] args) throws Exception {
            LOGGER.info("Starting the app with the provided environment variables..");
        if(mandatoryInputsCheck()) {
            new PubSubBridgeService().start();
        } else {
            throw new IllegalArgumentException
                    ("ERROR : Please provide valid source and destination topic details along with service account details..");
        }
    }

    /**
     * This method checks if all the mandatory fields are set in the environments variables
     * @return true if all is well
     */
    private static boolean mandatoryInputsCheck(){
        boolean mandatoryInputsCheckFlag = false;
        if(!Strings.isNullOrEmpty(SysEnv.SRC_PROJECT_ID) && !Strings.isNullOrEmpty(SysEnv.SRC_TOPIC_NAME) &&
                !Strings.isNullOrEmpty(SysEnv.DEST_PROJECT_ID) && !Strings.isNullOrEmpty(SysEnv.DEST_TOPIC_NAME)){
            LOGGER.info("Setting up a bridge between source topic =>{} and a destination topic =>{}"
                    , SysEnv.SRC_FULL_TOPIC_NAME, SysEnv.DEST_FULL_TOPIC_NAME);
            if(!Strings.isNullOrEmpty(SysEnv.GOOGLE_APPLICATION_CREDENTIALS)){
                mandatoryInputsCheckFlag = true;
            }
            if(SysEnv.SRC_PROJECT_ID.equalsIgnoreCase(SysEnv.DEST_PROJECT_ID)){
                throw new IllegalArgumentException("ERROR: Its overkill to use this app for what you are trying to achieve. Please use basic pubsub subscription instead.");
            }
        }
        return mandatoryInputsCheckFlag;
    }
}
