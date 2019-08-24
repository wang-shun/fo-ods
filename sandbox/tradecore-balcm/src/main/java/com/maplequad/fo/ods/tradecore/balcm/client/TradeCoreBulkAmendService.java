package com.maplequad.fo.ods.tradecore.balcm.client;

import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CancelBulkAmendRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CancelBulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ExecuteBulkAmendRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreBulkAmendGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TradeCoreBulkAmendService -
 *
 * This service class exposes the handle to carry out all trade related operations using TradeCore datamodel
 * and hides the underlying GRPC Implementation.
 *
 * @author Madhav Mindhe
 * @since :   06/09/2017
 */
public class TradeCoreBulkAmendService {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreBulkAmendService.class);

    private final TradeCoreBulkAmendGrpc.TradeCoreBulkAmendBlockingStub blockingStub;

    public TradeCoreBulkAmendService(String serviceHost, int servicePort) {
        Channel channel = ManagedChannelBuilder.forAddress(serviceHost, servicePort).usePlaintext(true).build();
        blockingStub = TradeCoreBulkAmendGrpc.newBlockingStub(channel);
    }

    public BulkAmendResponse initiateBulkAmend(BulkAmendOuterClass.BulkAmend request) {
        return blockingStub.initiateBulkAmend(request);
    }

    public BulkAmendResponse executeBulkAmend(String bulkAmendId) {
        return blockingStub.executeBulkAmend(ExecuteBulkAmendRequest.newBuilder().setBulkAmendId(bulkAmendId).build());
    }

    public CancelBulkAmendResponse cancelBulkAmend(String bulkAmendId) {
        return blockingStub.cancelBulkAmend(CancelBulkAmendRequest.newBuilder().setBulkAmendId(bulkAmendId).build());
    }

}
