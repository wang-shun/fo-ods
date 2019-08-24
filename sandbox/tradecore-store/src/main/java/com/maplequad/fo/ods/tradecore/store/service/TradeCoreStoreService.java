package com.maplequad.fo.ods.tradecore.store.service;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.BulkAmend;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendId;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CreateTradeResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ReadTradeRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ReadTradeResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.TradeCoreStoreGrpc;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.UpdateTradeRequest;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.UpdateTradeResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * TradeCoreStoreService -
 * <p>
 * This service class exposes the handle to carry out all trade related operations using TradeCore datamodel
 * and hides the underlying GRPC Implementation.
 *
 * @author Madhav Mindhe
 * @since :   14/08/2017
 */
public class TradeCoreStoreService {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreStoreService.class);

    private final TradeCoreStoreGrpc.TradeCoreStoreBlockingStub blockingStub;

    public TradeCoreStoreService(String serviceHost, int servicePort) {

        Channel channel = ManagedChannelBuilder.forAddress(serviceHost, servicePort).usePlaintext(true).build();
        blockingStub = TradeCoreStoreGrpc.newBlockingStub(channel);

    }

    /**
     * @param trade
     * @return instance of new Trade
     */
    public TradeCoreStoreResponse createTrade(Trade trade) {
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        CreateTradeRequest request =
                CreateTradeRequest.newBuilder().setTrade(TradeTransformer.toProto(trade)).build();
        CreateTradeResponse response = blockingStub.createTrade(request);
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrade(TradeTransformer.fromProto(response.getTrade()));
        return tcsResponse;
    }

    /**
     * @param tradeId
     * @return The details of the latest version of the provided tradeId
     */
    public TradeCoreStoreResponse getLatestTrade(String tradeId) {
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        Trade trade = null;
        TradeQuery query = TradeQuery.newBuilder().setByTradeId(tradeId).build();
        ReadTradeResponse response = blockingStub.readTrade(query);
        if (response.getTradesCount() > 0 && response.getTrades(0) != null) {
            trade = TradeTransformer.fromProto(response.getTrades(0));
            LOGGER.info("Found trade {}", response.getTrades(0).getTradeId());
        } else {
            LOGGER.info("No trades are found matching the given criteria {}", query);
        }
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrade(trade);
        return tcsResponse;
    }

    /**
     * @return The details of the latest version of all trades that are matching with the provided criteria
     */
    public TradeCoreStoreResponse getLatestTrades(TradeQuery query) {
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        List<Trade> tradeList = new ArrayList<>();
        ReadTradeResponse response = blockingStub.readTrade(query);
        if (!response.getTradesList().isEmpty()) {
            LOGGER.info("Found total {} trades..", response.getTradesList().size());
            for (TradeOuterClass.Trade ptrade : response.getTradesList()) {
                tradeList.add(TradeTransformer.fromProto(ptrade));
                LOGGER.trace("Trade: {}", ptrade);
            }
        } else {
            LOGGER.info("No trades are found matching the given criteria {}", query);
        }
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrades(tradeList);
        return tcsResponse;
    }

    /**
     * @return The details of the latest version of all trades that are matching with the provided criteria
     */
    public TradeCoreStoreResponse getLatestTrades(TradeQuery query, Timestamp timestamp) {
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        List<Trade> tradeList = new ArrayList<>();
        ReadTradeResponse response = blockingStub.readTradesAsOf(
                ReadTradeRequest.newBuilder().
                        setQuery(query).
                        setAsOf(timestamp.toProto()).
                        build());
        if (!response.getTradesList().isEmpty()) {
            LOGGER.info("Found total {} trades..", response.getTradesList().size());
            for (TradeOuterClass.Trade ptrade : response.getTradesList()) {
                tradeList.add(TradeTransformer.fromProto(ptrade));
                LOGGER.info("Trade: {}", ptrade);
            }
        } else {
            LOGGER.info("No trades are found matching the given criteria {}", query);
        }
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrades(tradeList);
        return tcsResponse;
    }

    /**
     * @param trade
     * @return instance of the updated Trade
     */
    public TradeCoreStoreResponse updateTrade(Trade trade) {
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        UpdateTradeRequest request =
                UpdateTradeRequest.newBuilder().addTrades(TradeTransformer.toProto(trade)).build();
        UpdateTradeResponse response = blockingStub.updateTrade(request);
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrade(TradeTransformer.fromProto(response.getTrades(0)));
        return tcsResponse;
    }

    public String createBulkAmend(BulkAmend request) {
        return blockingStub.createBulkAmend(request).getBulkAmendId();
    }

    public BulkAmend getBulkAmend(String bulkAmendId) {
        return blockingStub.getBulkAmend(BulkAmendId.newBuilder().setBulkAmendId(bulkAmendId).build());
    }

    public void executeBulkAmend(BulkAmendResponse request) {
        blockingStub.executeBulkAmend(request);
    }

    public void cancelBulkAmend(String bulkAmendId) {
        blockingStub.cancelBulkAmend(BulkAmendId.newBuilder().setBulkAmendId(bulkAmendId).build());
    }
}
