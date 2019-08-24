package com.maplequad.fo.ods.tradecore.balcm.service;

import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CancelBulkAmendRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CancelBulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ExecuteBulkAmendRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TestRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TestResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreBulkAmendGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * TradeCoreBulkAmendGrpcService -
 *
 * This service class exposes the handle to database access operations.
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class TradeCoreBulkAmendGrpcService extends TradeCoreBulkAmendGrpc.TradeCoreBulkAmendImplBase {


    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreBulkAmendGrpcService.class);

    private BulkAmendProcessorService bulkAmendProcessorService;

    /**
     * @throws Exception
     */
    public TradeCoreBulkAmendGrpcService() throws Exception {
        this.bulkAmendProcessorService = new BulkAmendProcessorService();
    }


    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void testConnection(TestRequest request, StreamObserver<TestResponse> responseObserver) {
        responseObserver.onNext(tastyConnection(request));
        responseObserver.onCompleted();
    }

    @Override
    public void initiateBulkAmend(BulkAmendOuterClass.BulkAmend request, StreamObserver<BulkAmendResponse> responseObserver) {
        responseObserver.onNext(bulkAmendProcessorService.initiateBulkAmend(request));
        responseObserver.onCompleted();
    }

    @Override
    public void executeBulkAmend(ExecuteBulkAmendRequest request, StreamObserver<BulkAmendResponse> responseObserver) {
        responseObserver.onNext(bulkAmendProcessorService.executeBulkAmend(request));
        responseObserver.onCompleted();
    }

    @Override
    public void cancelBulkAmend(CancelBulkAmendRequest request, StreamObserver<CancelBulkAmendResponse> responseObserver) {
        responseObserver.onNext(bulkAmendProcessorService.cancelBulkAmend(request.getBulkAmendId()));
        responseObserver.onCompleted();
    }

    /**
     * @param request
     * @return
     */
    private TestResponse tastyConnection(TestRequest request) {
        TestResponse response = TestResponse.newBuilder().setResponse("I am served!").build();
        return response;
    }
}
