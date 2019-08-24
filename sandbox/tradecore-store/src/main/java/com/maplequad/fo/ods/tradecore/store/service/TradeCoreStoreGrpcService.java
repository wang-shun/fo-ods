package com.maplequad.fo.ods.tradecore.store.service;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendId;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.Empty;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ReadTradeRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ReadTradeResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TestRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TestResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreStoreGrpc;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.UpdateTradeRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.UpdateTradeResponse;
import com.maplequad.fo.ods.tradecore.store.data.access.query.SupportedQueries;
import com.maplequad.fo.ods.tradecore.store.data.access.service.DataAccessService;
import com.maplequad.fo.ods.tradecore.store.data.access.service.TradeDataAccessService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

/***
 * TradeCoreStoreGrpcService -
 *
 * This service class exposes the handle to database access operations.
 *
 * @author Madhav Mindhe
 * @since :   12/08/2017
 */
public class TradeCoreStoreGrpcService extends TradeCoreStoreGrpc.TradeCoreStoreImplBase {


    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreStoreService.class);

    private DataAccessService tradeDataAccessService = null;

    public TradeCoreStoreGrpcService() throws Exception {
        tradeDataAccessService = new TradeDataAccessService();
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void createTrade(CreateTradeRequest request, StreamObserver<CreateTradeResponse> responseObserver) {
        responseObserver.onNext(insertTrade(request));
        responseObserver.onCompleted();
    }

    @Override
    public void readTrade(TradeQuery request, StreamObserver<ReadTradeResponse> responseObserver) {
        responseObserver.onNext(readTrade(request, Timestamp.now()));
        responseObserver.onCompleted();
    }

    @Override
    public void readTradesAsOf(ReadTradeRequest request, StreamObserver<ReadTradeResponse> responseObserver) {
        responseObserver.onNext(readTrade(request.getQuery(), Timestamp.fromProto(request.getAsOf())));
        responseObserver.onCompleted();
    }

    /**
     * @param request
     * @param responseObserver
     */
    @Override
    public void updateTrade(UpdateTradeRequest request, StreamObserver<UpdateTradeResponse> responseObserver) {
        responseObserver.onNext(updateTrade(request));
        responseObserver.onCompleted();
    }

    @Override
    public void createBulkAmend(BulkAmendOuterClass.BulkAmend request, StreamObserver<BulkAmendId> responseObserver) {
        responseObserver.onNext(BulkAmendId.newBuilder().setBulkAmendId(
                tradeDataAccessService.createBulkAmend(request)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getBulkAmend(BulkAmendId request, StreamObserver<BulkAmendOuterClass.BulkAmend> responseObserver) {
        responseObserver.onNext(tradeDataAccessService.getBulkAmend(request.getBulkAmendId()));
        responseObserver.onCompleted();
    }

    @Override
    public void executeBulkAmend(BulkAmendResponse request, StreamObserver<Empty> responseObserver) {
        tradeDataAccessService.executeBulkAmend(request);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void cancelBulkAmend(BulkAmendId request, StreamObserver<Empty> responseObserver) {
        tradeDataAccessService.cancelBulkAmend(request.getBulkAmendId());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
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

    /**
     * @param request
     * @return
     */
    private TestResponse tastyConnection(TestRequest request) {
        TestResponse response = TestResponse.newBuilder().setResponse("I am served!").build();
        return response;
    }

    /**
     * @param request
     * @return
     */
    private CreateTradeResponse insertTrade(CreateTradeRequest request) {
        Timestamp dsStartTS = Timestamp.now();
        TradeOuterClass.Trade trade = request.getTrade();
        LOGGER.info("insertTrade {}", trade.getOsTradeId());
        LOGGER.debug("insertTrade {}", trade);
        Trade newTrade = null;
        try {
            newTrade = tradeDataAccessService.insert(TradeTransformer.fromProto(trade));
            LOGGER.info("Inserted the trade {}", newTrade.getTradeId());
        } catch (Exception e) {
            throw new RuntimeException("Cannot insert the trade: " + trade, e);
        }
        CreateTradeResponse response =
                CreateTradeResponse.newBuilder()
                        .setTrade(TradeTransformer.toProto(newTrade))
                        .setDsStartTimestamp(dsStartTS.toProto())
                        .setDsFinishTimestamp(Timestamp.now().toProto())
                        .build();
        return response;
    }

    /**
     * @return
     */
    private ReadTradeResponse readTrade(TradeQuery query, Timestamp asOf) {
        String subQuery = SupportedQueries.subQuery(query);
        List<Trade> trades = tradeDataAccessService.readTrades(subQuery, asOf);
        List<TradeOuterClass.Trade> tradesAsProto =
                trades.stream().map(TradeTransformer::toProto).collect(toList());
        return ReadTradeResponse.newBuilder().
                addAllTrades(tradesAsProto).
                setDsFinishTimestamp(Timestamp.now().toProto()).
                build();
    }


    /**
     * @param request
     * @return
     */
    private UpdateTradeResponse updateTrade(UpdateTradeRequest request) {
        Timestamp dsStartTS = Timestamp.now();
        TradeOuterClass.Trade trade = request.getTrades(0);
        LOGGER.info("updateTrade: {}", trade);
        Trade uTrade = null;
        try {
            uTrade = tradeDataAccessService.update(TradeTransformer.fromProto(trade));
            LOGGER.info("Updated the trade");
        } catch (Exception e) {
            throw new RuntimeException("Cannot update the trade: " + trade, e);
        }
        UpdateTradeResponse response = UpdateTradeResponse.newBuilder().addTrades(TradeTransformer.toProto(uTrade))
                .setDsStartTimestamp(dsStartTS.toProto())
                .setDsFinishTimestamp(Timestamp.now().toProto())
                .build();
        return response;
    }
}
