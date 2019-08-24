package com.maplequad.fo.ods.tradecore.store.data.access.mutation;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Mutation;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.BulkAmend;

/***
 * BulkAmendMutation -
 *
 * This class creates all required mutations.
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class BulkAmendMutation {
    public static final String ENTITY = "BULK_AMEND";
    public static final String BULK_AMEND_ID = "bulkAmendId";
    public static final String ASSET_CLASS = "assetClass";
    public static final String BULK_AMEND_TYPE = "bulkAmendType";
    public static final String DESCRIPTION = "description";
    public static final String SCOPE = "scope";
    public static final String STATUS = "status";
    public static final String CREATED_BY = "createdBy";
    public static final String AS_OF = "asOf";
    public static final String OS_REQUEST_TIMESTAMP = "osRequestTimestamp";
    public static final String LCM_START_TIMESTAMP = "lcmStartTimestamp";
    public static final String DS_START_TIMESTAMP = "dsStartTimestamp";
    public static final String DB_START_TIMESTAMP = "dbStartTimestamp";
    public static final String DB_FINISH_TIMESTAMP = "dbFinishTimestamp";
    public static final String DS_FINISH_TIMESTAMP = "dsFinishTimestamp";
    public static final String LCM_FINISH_TIMESTAMP = "lcmFinishTimestamp";


    public static Mutation insert(BulkAmend bulkAmend, String id) {
        Mutation.WriteBuilder builder = Mutation.newInsertBuilder(ENTITY);
        builder.set(BULK_AMEND_ID).to(id);
        builder.set(STATUS).to("INITIATING");
        return upsert(bulkAmend, builder);
    }

    public static Mutation update(String bulkAmendId, String status) {
        Mutation.WriteBuilder builder = Mutation.newUpdateBuilder(ENTITY);
        builder.set(STATUS).to(status);
        builder.set(BULK_AMEND_ID).to(bulkAmendId);
        return builder.build();
    }

    /**
     * This method generates the required mutation for BULK_AMEND table
     *
     * @param bulkAmend
     * @return Mutation for BULK_AMEND
     */
    private static Mutation upsert(BulkAmend bulkAmend, Mutation.WriteBuilder builder) {

        //Attributes
        builder.set(ASSET_CLASS).to(bulkAmend.getAssetClass());
        builder.set(BULK_AMEND_TYPE).to(bulkAmend.getBulkAmendType());
        builder.set(DESCRIPTION).to(bulkAmend.getDescription());
        builder.set(CREATED_BY).to(bulkAmend.getCreatedBy());
        try {
            builder.set(SCOPE).to(JsonFormat.printer().print(bulkAmend.getType()));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        builder.set(AS_OF).to(Timestamp.fromProto(bulkAmend.getAsOf()));
        builder.set(OS_REQUEST_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getOsRequestTimestamp()));
        builder.set(LCM_START_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getOsRequestTimestamp()));
        builder.set(DS_START_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getDsStartTimestamp()));
        builder.set(DB_START_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getDbStartTimestamp()));
        builder.set(DB_FINISH_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getDbFinishTimestamp()));
        builder.set(DS_FINISH_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getDsFinishTimestamp()));
        builder.set(LCM_FINISH_TIMESTAMP).to(Timestamp.fromProto(bulkAmend.getLcmFinishTimestamp()));
        return builder.build();
    }
}
