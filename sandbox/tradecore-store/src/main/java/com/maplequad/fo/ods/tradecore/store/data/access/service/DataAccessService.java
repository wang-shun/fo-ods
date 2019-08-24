package com.maplequad.fo.ods.tradecore.store.data.access.service;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.BulkAmend;
import com.maplequad.fo.ods.tradecore.proto.service.grpc.BulkAmendResponse;

import java.util.List;

/***
 * DataAccessService -
 *
 * This interface gives the handle to database access operations.
 *
 * @author Madhav Mindhe
 * @since :   12/08/2017
 */
public interface DataAccessService {

    Trade insert(Trade trade);

    /*
    Latest version of trade by tradeId
    */
    Trade readLatest(String tradeId);

    /*
    Latest version of trade by tradeId as of given timestamp
    */
    Trade readLatest(String tradeId, Timestamp asOf);

    /*
    All trades for the criteria
    */
    List<Trade> readTrades(String subQuery, Timestamp asOf);

    /*
    Update the trade
    */
    Trade update(Trade trade);

    /**
     * Bulk Amends
     */
    String createBulkAmend(BulkAmend request);

    BulkAmend getBulkAmend(String bulkAmendId);

    void executeBulkAmend(BulkAmendResponse bulkAmend);

    void cancelBulkAmend(String bulkAmendId);
}
