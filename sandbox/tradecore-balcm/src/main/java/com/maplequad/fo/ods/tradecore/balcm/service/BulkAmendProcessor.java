package com.maplequad.fo.ods.tradecore.balcm.service;

import com.maplequad.fo.ods.tradecore.balcm.handlers.CompressionHandler;
import com.maplequad.fo.ods.tradecore.balcm.handlers.StockSplitHandler;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass.BulkAmendDetails;

import java.util.List;

public interface BulkAmendProcessor {
    TradeQuery createQuery(BulkAmendDetails details);
    ///TODO 1
    //List<Trade> processTrades(BulkAmendOuterClass.BulkAmend amend, List<Trade> trades);
    List<Trade> processTrades(List<Trade> trades);

    static BulkAmendProcessor processorFor(BulkAmendDetails.TypeCase typeCase) {
        switch (typeCase) {
            case SPLIT:
                return StockSplitHandler.INSTANCE;
            case DIVIDEND:
                return null;
            case COMPRESSION:
                return CompressionHandler.INSTANCE;
            default:
                throw new RuntimeException("Unsupported bulk amend type " + typeCase);
        }
    }
}
