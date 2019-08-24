package com.maplequad.fo.ods.tradecore.utils;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * SysEnv - All System environment variables at one place
 *
 * @author Madhav Mindhe
 * @since :   08/09/2017
 */
public class SysEnv {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysEnv.class);
    private static final String TRUE = "TRUE";

    public static final String TC_PROJECT_ID = System.getenv("TC_PROJECT_ID"); //"fo-ods";
    public static final String TC_ASSET_CLASS = System.getenv("TC_ASSET_CLASS"); //"ird";

    public static final int NO_OF_THREADS = parseInt(System.getenv("NO_OF_THREADS")); //2

    public static final String TC_FB_SRVC_ACCT_KEY = System.getenv("TC_FB_SRVC_ACCT_KEY");//src/main/resources/service-account/fo-ods-cred.json";
    public static final String TC_FB_DB_URL = System.getenv("TC_FB_DB_URL");//https://fo-ods.firebaseio.com";
    public static final String TC_FB_BASE_GRID = System.getenv("TC_FB_BASE_GRID");//irdBulkAmendGrid";
    public static final String TC_FB_AUTH_ID = System.getenv("TC_FB_AUTH_ID");
    public static final String TC_FB_AUTH_VALUE = System.getenv("TC_FB_AUTH_VALUE");
    public static final boolean TC_FB_SAVE_FLAG =
            !Strings.isNullOrEmpty(System.getenv("TC_FB_SAVE_FLAG"))
                    ? TRUE.equalsIgnoreCase(System.getenv("TC_FB_SAVE_FLAG").toUpperCase()): false;

    public static final String TC_STORE_SRVC_HOST = System.getenv("TC_STORE_SRVC_HOST"); //"35.189.219.191";
    public static final int TC_STORE_SRVC_PORT = parseInt(System.getenv("TC_STORE_SRVC_PORT")); //5555;

    public static final String TC_VS_DLT_TOPIC_SUBS = System.getenv("TC_VS_DLT_TOPIC_SUBS");
    public static final String TC_MD_TOPIC_SUBS = System.getenv("TC_MD_TOPIC_SUBS");

    public static final String TC_BALCM_SRVC_HOST = System.getenv("TC_BALCM_SRVC_HOST");
    public static final int TC_BALCM_SRVC_PORT = parseInt(System.getenv("TC_BALCM_SRVC_PORT")); //5151;
    public static final int TC_BALCM_SRVC_THREADS = parseInt(System.getenv("TC_BALCM_SRVC_THREADS")); //5151;

    public static final int MD_FB_UPDT_BFR_WINDOW_IN_MS = parseInt(System.getenv("MD_FB_UPDT_BFR_WINDOW_IN_MS")); //"500";

    public static final boolean MARKET_SIMULATOR_FLAG =
            !Strings.isNullOrEmpty(System.getenv("MARKET_SIMULATOR_FLAG"))
                    ? TRUE.equalsIgnoreCase(System.getenv("MARKET_SIMULATOR_FLAG").toUpperCase()): false;

    //Test
    public static final String LOCAL_RUN_FLAG = System.getenv("LOCAL_RUN_FLAG"); //"fo-ods";
    public static final int RUN_NO = parseInt(System.getenv("RUN_NO")); //"001";
    public static final String TC_TG_ASSET_CLASS = System.getenv("TC_TG_ASSET_CLASS"); //fi-irs
    public static final String TG_LOG_GEN = System.getenv("TG_LOG_GEN"); // ON or OFF
    public static final int TEST_TRADE_COUNT = parseInt(System.getenv("TEST_TRADE_COUNT"));
    public static final int TEST_MAX_TRADE_LEGS = parseInt(System.getenv("TEST_MAX_TRADE_LEGS"));
    public static final int TEST_MAX_TRADE_PARTIES = parseInt(System.getenv("TEST_MAX_TRADE_PARTIES"));

    /**
     * @param arg
     * @return
     */
    private static int parseInt(String arg) {
        int value = 0;
        if (arg != null) {
            try {
                value = Integer.parseInt(arg);
            }catch (NumberFormatException nfe){
                LOGGER.error("ERROR : Please specify valid number instead of {}", arg);
                throw new IllegalArgumentException(arg);
            }
        }
        return value;
    }

}
