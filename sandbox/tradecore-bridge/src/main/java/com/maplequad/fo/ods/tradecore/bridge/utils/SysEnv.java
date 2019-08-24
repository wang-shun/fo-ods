package com.maplequad.fo.ods.tradecore.bridge.utils;

import com.google.cloud.ServiceOptions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysEnv {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysEnv.class);

    private static final String SEPARATOR = ".";
    public static final String APP_ID = System.getenv("APP_ID");
    public static final String SRC_PROJECT_ID = System.getenv("SRC_PROJECT_ID");
    public static final String SRC_TOPIC_NAME = System.getenv("SRC_TOPIC_NAME");
    public static final String DEST_PROJECT_ID = System.getenv("DEST_PROJECT_ID");
    public static final String DEST_TOPIC_NAME = System.getenv("DEST_TOPIC_NAME");
    public static final int NO_OF_EXEC_THREADS =  parseInt(System.getenv("NO_OF_EXEC_THREADS"));
    public static final int ACK_DEADLINE_IN_SECONDS = parseInt(System.getenv("ACK_DEADLINE_IN_SECONDS"));
    public static final  int BLOCKING_QUEUE_SIZE =
            Strings.isNullOrEmpty(System.getenv("BLOCKING_QUEUE_SIZE")) ?
                    600 : Integer.parseInt(System.getenv("BLOCKING_QUEUE_SIZE"));
    public static final String GOOGLE_APPLICATION_CREDENTIALS = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

    public static final String SRC_FULL_TOPIC_NAME = getFullTopicName(SRC_PROJECT_ID, SRC_TOPIC_NAME);
    public static final String DEST_FULL_TOPIC_NAME = getFullTopicName(DEST_PROJECT_ID, DEST_TOPIC_NAME);
    //purposefully private
    private static final String srcTopicSubsName = System.getenv("SRC_TOPIC_SUBS_NAME");
    public static final String SRC_TOPIC_SUBS_NAME =
            Strings.isNullOrEmpty(srcTopicSubsName) ?
                    SRC_PROJECT_ID + SEPARATOR + SRC_TOPIC_NAME : srcTopicSubsName;

    public static final boolean TXN_LOGGING_FLAG = Strings.isNullOrEmpty(System.getenv("TXN_LOGGING_FLAG")) ?
            false : "TRUE".equalsIgnoreCase( System.getenv("TXN_LOGGING_FLAG"));
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

    /**
     * @param projectId
     * @param topicId
     * @return
     */
    private static String getFullTopicName(String projectId, String topicId){
        StringBuilder builder = new StringBuilder();
        builder.append("projects/");
        builder.append(projectId);
        builder.append("/topics/");
        builder.append(topicId);
        return builder.toString();
    }

    /**
     * @param projectId
     * @param subscriptioId
     * @return
     */
    public static String getFullSubscriptionName(String projectId, String subscriptioId){
        StringBuilder builder = new StringBuilder();
        builder.append("projects/");
        builder.append(projectId);
        builder.append("/subscriptions/");
        builder.append(subscriptioId);
        return builder.toString();
    }
}
