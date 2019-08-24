package com.maplequad.fo.ods.tradecore.store.data.access.rowmapper;

import com.google.cloud.spanner.ResultSet;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.store.data.access.mutation.BulkAmendMutation;

public class BulkAmendMapper {
    public static BulkAmendOuterClass.BulkAmend readBulkAmend(ResultSet resultSet) {
        BulkAmendOuterClass.BulkAmendDetails.Builder details = BulkAmendOuterClass.BulkAmendDetails.newBuilder();
        String scope = resultSet.getString(BulkAmendMutation.SCOPE);
        try {
            JsonFormat.parser().merge(scope, details);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable to parse amend details: " + scope);
        }
        return BulkAmendOuterClass.BulkAmend.newBuilder().
                setAssetClass(resultSet.getString(BulkAmendMutation.ASSET_CLASS)).
                setBulkAmendType(resultSet.getString(BulkAmendMutation.BULK_AMEND_TYPE)).
                setType(details.build()).
                setDescription(resultSet.getString(BulkAmendMutation.ASSET_CLASS)).
                setCreatedBy(resultSet.getString(BulkAmendMutation.CREATED_BY)).
                setAsOf(resultSet.getTimestamp(BulkAmendMutation.AS_OF).toProto()).
                build();
    }
}
