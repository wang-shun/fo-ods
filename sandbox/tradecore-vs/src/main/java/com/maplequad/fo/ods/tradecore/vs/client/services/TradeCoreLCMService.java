package com.maplequad.fo.ods.tradecore.vs.client.services;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.TradeCoreStoreResponse;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.lcm.processor.LCTrackLog;
import com.maplequad.fo.ods.tradecore.lcm.utils.CreateUUIDHelper;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.*;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * TradeCoreLCMService -
 * <p>
 * This service class exposes the handle to carry out all trade related operations using TradeCoreLCM
 * and hiders the underlying GRPC Implementation.
 *
 * @author Madhav Mindhe
 * @since :   03/09/2017
 */
public class TradeCoreLCMService {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeCoreLCMService.class);

    private TradeCoreLifeCycleGrpc.TradeCoreLifeCycleBlockingStub blockingStub;

    public TradeCoreLCMService(String serviceHost, int servicePort) {

        Channel channel = ManagedChannelBuilder.forAddress(serviceHost, servicePort).usePlaintext(true).build();
        blockingStub = TradeCoreLifeCycleGrpc.newBlockingStub(channel);
    }

    /**
     * @param trade
     * @return instance of new Trade
     */
    public TradeCoreStoreResponse createTrade(Trade trade) {
        LCTrackLog trackLog = new LCTrackLog();
        trackLog.osTradeid = trade.getOsTradeId();
        trackLog.serialNumber = String.valueOf(CreateUUIDHelper.createUUID());
        trackLog.ULT_requestTimestamp = new Date().getTime();
        trackLog.numOfTrades = 1;
        trackLog.subnumber = 0;
        trackLog.action = "CREATE";

        CreateTradeSTDRequest request = CreateTradeSTDRequest.newBuilder()
                .setTrade(TradeTransformer.toProto(trade))
                .setTrackLog(trackLog.copyToProto())
                .build();

        CreateTradeSTDResponse response = this.blockingStub.createTrade(request);
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrade(TradeTransformer.fromProto(response.getTrade()));
        return tcsResponse;
    }

    /**
     * @param trade
     * @return instance of the updated Trade
     */
    public TradeCoreStoreResponse updateTrade(Trade trade) {
        TradeCoreStoreResponse tcsResponse = new TradeCoreStoreResponse();
        UpdateTradeSTDRequest request =
                UpdateTradeSTDRequest.newBuilder().addTrades(TradeTransformer.toProto(trade)).build();
        UpdateTradeSTDResponse response = blockingStub.updateTrade(request);
        tcsResponse.setDsStartTS(Timestamp.fromProto(response.getDsStartTimestamp()));
        tcsResponse.setDsFinishTS(Timestamp.fromProto(response.getDsFinishTimestamp()));
        tcsResponse.setTrade(TradeTransformer.fromProto(response.getTrades(0)));
        return tcsResponse;
    }
}