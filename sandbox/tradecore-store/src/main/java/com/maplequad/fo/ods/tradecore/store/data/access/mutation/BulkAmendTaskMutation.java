package com.maplequad.fo.ods.tradecore.store.data.access.mutation;

import com.google.cloud.spanner.Mutation;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendTaskOuterClass.BulkAmendTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * BulkAmendTaskMutation -
 *
 * This class creates all required mutations.
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class BulkAmendTaskMutation {

    protected static final String BULK_AMEND_TASK = "BULK_AMEND_TASK";
    protected static final String UPSERT_IN = "upsert entered for table {} with {}";
    protected static final String UPSERT_OUT = "upsert exited for table {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkAmendTaskMutation.class);


    /**
     * This method generates the required mutation for BULK_AMEND_TASK table
     *
     * @param bulkAmendTask
     * @return Mutation for BULK_AMEND_TASK
     */
    public static Mutation upsert(BulkAmendTask bulkAmendTask) {

        Mutation.WriteBuilder builder = Mutation.newInsertOrUpdateBuilder(BULK_AMEND_TASK);
//
//        //Primary Key
//        builder.set(BulkAmendTask.BULK_AMEND_ID).to(bulkAmendTask.getBulkAmendId());
//        builder.set(BulkAmendTask.TRADE_ID).to(bulkAmendTask.getBulkAmendId());
//
//        //Attributes
//        builder.set(BulkAmendTask.ACTION).to(bulkAmendTask.getAction());
//        builder.set(BulkAmendTask.METADATA).to(bulkAmendTask.getMetaData());
//
//        LOGGER.info(UPSERT_OUT, BULK_AMEND_TASK);
        return builder.build();
    }
}
