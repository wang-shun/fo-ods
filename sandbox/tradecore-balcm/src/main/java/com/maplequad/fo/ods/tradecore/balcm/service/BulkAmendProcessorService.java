package com.maplequad.fo.ods.tradecore.balcm.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.maplequad.fo.ods.tradecore.balcm.data.access.BulkAmendDAO;
import com.maplequad.fo.ods.tradecore.balcm.data.model.BulkAmendRecord;
import com.maplequad.fo.ods.tradecore.balcm.data.model.BulkAmendView;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.xform.TradeTransformer;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.BulkAmend;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.CancelBulkAmendResponse;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.ExecuteBulkAmendRequest;
import com.maplequad.fo.ods.tradecore.store.service.TradeCoreStoreService;
import com.maplequad.fo.ods.tradecore.utils.SysEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

////import com.maplequad.fo.ods.tradecore.balcm.data.access.BulkAmendDAO;

/**
 * BulkAmendProcessorService -
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class BulkAmendProcessorService {
    static Logger LOGGER = LoggerFactory.getLogger(BulkAmendProcessorService.class);

    private static String assetClass = SysEnv.TC_ASSET_CLASS;//"ird";
    private static String serviceAccountPath = SysEnv.TC_FB_SRVC_ACCT_KEY;//src/main/resources/service-account/fo-ods-cred.json";
    private static String databaseURL = SysEnv.TC_FB_DB_URL;//https://fo-ods.firebaseio.com";
    private static String tradeGrid = SysEnv.TC_FB_BASE_GRID;//irdBulkAmendGrid";
    private static String authId = SysEnv.TC_FB_AUTH_ID;
    private static String authValue = SysEnv.TC_FB_AUTH_VALUE;
    private static String serviceHost = SysEnv.TC_STORE_SRVC_HOST; //"localhost";
    private static int servicePort = SysEnv.TC_STORE_SRVC_PORT; //3002;
    private TradeCoreStoreService tradeCoreStoreService;
    private BulkAmendDAO bulkAmendDAO;

    /**
     *
     */
    public BulkAmendProcessorService() {
        this.tradeCoreStoreService = new TradeCoreStoreService(serviceHost, servicePort);
        this.bulkAmendDAO = new BulkAmendDAO(serviceAccountPath, databaseURL, tradeGrid, authId, authValue);
    }

    public BulkAmendResponse initiateBulkAmend(BulkAmend bulkAmend) {
        try {
            LOGGER.info("Initiate bulk amend {} ", JsonFormat.printer().print(bulkAmend));
        } catch (InvalidProtocolBufferException e) {}
        String bulkAmendId = tradeCoreStoreService.createBulkAmend(bulkAmend);
        BulkAmendResponse response = this.process(bulkAmendId, bulkAmend);
        this.sendToViewServer(bulkAmendId, response, "INIT");
        return response;
    }

    public BulkAmendResponse executeBulkAmend(ExecuteBulkAmendRequest request) {
        LOGGER.info("Execuring bulk amend {}", request.getBulkAmendId());
        String bulkAmendId = request.getBulkAmendId();
        BulkAmend bulkAmend = tradeCoreStoreService.getBulkAmend(bulkAmendId);
        try {
            LOGGER.info("Found bulk amend {} ", JsonFormat.printer().print(bulkAmend));
        } catch (InvalidProtocolBufferException e) {}
        BulkAmendResponse response = this.process(bulkAmendId, bulkAmend);
        tradeCoreStoreService.executeBulkAmend(response);
        this.sendToViewServer(bulkAmendId, response, "DONE");
        return response;
    }

    /**
     */
    private BulkAmendResponse process(String bulkAmendId, BulkAmend bulkAmend) {
        BulkAmendProcessor processor = BulkAmendProcessor.processorFor(bulkAmend.getType().getTypeCase());

        List<Trade> impactedTrades = this.tradeCoreStoreService.
                getLatestTrades(processor.createQuery(bulkAmend.getType())).getTrades();
        List<Trade> impactedTradesCopy = impactedTrades.stream().map(TradeTransformer::toProto).map(TradeTransformer::fromProto).collect(toList());
        List<Trade> processedTrades = processor.processTrades(impactedTradesCopy);

        return BulkAmendResponse.newBuilder().
                setBulkAmend(bulkAmend).
                addAllImpactedTrades(impactedTrades.stream().map(TradeTransformer::toProto).collect(toList())).
                addAllImpactedTradeIds(impactedTrades.stream().map(Trade::getTradeId).collect(toList())).
                addAllResultingTrades(processedTrades.stream().map(TradeTransformer::toProto).collect(toList())).
                setBulkAmendId(bulkAmendId).
                build();
    }


    private void sendToViewServer(String bulkAmendId, BulkAmendResponse response, String status) {
        BulkAmend bulkAmend = response.getBulkAmend();
        BulkAmendView bulkAmendView = new BulkAmendView();
        List<BulkAmendRecord> pre = response.getImpactedTradesList().stream().
                map(t->new BulkAmendRecord(TradeTransformer.fromProto(t))).
                collect(toList());
        List<BulkAmendRecord> post = response.getResultingTradesList().stream().
                map(t->new BulkAmendRecord(TradeTransformer.fromProto(t))).
                collect(toList());

        bulkAmendView.setBulkAmendId(bulkAmendId);
        bulkAmendView.setBulkAmendType(bulkAmend.getBulkAmendType());
        bulkAmendView.setCreatedBy(bulkAmend.getCreatedBy());
        bulkAmendView.setLastModifiedAt(new Date());
        bulkAmendView.setScope("Book:"+bulkAmend.getType().getCompression().getBookId()+", InstrumentID:"+bulkAmend.getType().getCompression().getInstrumentId());
        bulkAmendView.setDescription(bulkAmend.getDescription());
        bulkAmendView.setStatus(status);

        this.bulkAmendDAO.upsert(bulkAmendView, pre, post);
    }

    /**
     * @param buildAmendId
     * @return
     */
    public CancelBulkAmendResponse cancelBulkAmend(String buildAmendId) {
        //Send to ViewServer
        this.bulkAmendDAO.delete(buildAmendId);
        tradeCoreStoreService.cancelBulkAmend(buildAmendId);
        return CancelBulkAmendResponse.newBuilder().setBulkAmendId(buildAmendId).build();
    }
}
