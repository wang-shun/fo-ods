package com.maplequad.fo.ods.tradecore.balcm.handlers;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.balcm.service.BulkAmendProcessor;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.QueryByBookAndInstrumentId;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;
import com.maplequad.fo.ods.tradecore.proto.model.event.BulkAmendOuterClass;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CompressionHandler -
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public enum CompressionHandler implements BulkAmendProcessor {
    INSTANCE;

    private static final int MIN_COMPRESSION_SIZE = 1;
    private static final int MAX_COMPRESSION_SIZE = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(CompressionHandler.class);

    @Override
    public TradeQuery createQuery(BulkAmendOuterClass.BulkAmendDetails details) {
        BulkAmendOuterClass.Compression compression = details.getCompression();
        return TradeQuery.newBuilder().setByBookInstrumentId(
                QueryByBookAndInstrumentId.newBuilder().
                        setInstrumentId(compression.getInstrumentId()).
                        setBookId(compression.getBookId()).
                        build()).
                build();
    }

    @Override
    ////TODO 1
   /// public List<Trade> processTrades(BulkAmendOuterClass.BulkAmend amend, List<Trade> impactedTrades) {
    public List<Trade> processTrades(List<Trade> impactedTrades) {
        LOGGER.info("handleBulkAmend entered for {} trades", impactedTrades.size());
        int compressionSize = this.getCompressionSize(MIN_COMPRESSION_SIZE, (MAX_COMPRESSION_SIZE < impactedTrades.size()) ? MAX_COMPRESSION_SIZE : 1);
        LOGGER.info("Compression size determined as {} trades", compressionSize);
        int[] compressionBlocks = this.getCompressionBlocks(impactedTrades.size(), compressionSize);
        LOGGER.info("Compression blocks determined as {} trades", StringUtils.concat(compressionBlocks));
        List<Trade> compressedTrades = new ArrayList<>();
        //Compression is meaningless otherwise
        if(compressionSize < impactedTrades.size()) {
            int rCount = 0;
            for (int bCount = 0; bCount < compressionSize; bCount++) {
                float bNotional = 0;
                float[] notionalBlocks = null;
                for (int cCount = 0; cCount < compressionBlocks[bCount]; cCount++) {
                    Trade iTrade = impactedTrades.get(rCount);
                    TradeEvent event = iTrade.getTradeEventList().get(0);
                    event.setValidTimeFrom(Timestamp.now());
                    event.setEventStatus("PENDVER");
                    //event.setActiveFlag(true);
                    event.setEventReference("IRS Trade Compression");

                    if (cCount == compressionBlocks[bCount] - 1) {
                        event.setEventType("PTERM");
                        notionalBlocks = getNotionalBlocks(bNotional, event.getTradeLegList().size());
                        LOGGER.info("Notional block determined as {}", StringUtils.concat(notionalBlocks));
                        int lCount = 0;
                        for (ITradeLeg tradeLeg : event.getTradeLegList()) {
                            Ird ird = (Ird) tradeLeg;
                            ird.setNotional(ird.getNotional() + notionalBlocks[lCount]);
                            lCount++;
                        }
                        compressedTrades.add(iTrade);
                    } else {
                        event.setEventType("FTERM");
                        for (ITradeLeg tradeLeg : event.getTradeLegList()) {
                            Ird ird = (Ird) tradeLeg;
                            bNotional = bNotional + ird.getNotional();
                            ird.setMaturityDate(new Date());
                        }
                    }
                    rCount++;
                }
            }
        }
        LOGGER.info("handleBulkAmend exited with {} trades", compressedTrades.size());
        return compressedTrades;
    }

    private int getCompressionSize(int min, int max) {
        int range = (max - min) + 1;
        return (int) (Math.random() * range) + min;
    }

    private int[] getCompressionBlocks(int tTrades, int cTrades) {
        int rTrades = tTrades;
        int rCount = cTrades;
        int[] comprBlocks = new int[cTrades];
        for (int iCount = 0; iCount < cTrades; iCount++) {
            comprBlocks[iCount] = (int) Math.ceil((double) rTrades / rCount);
            rTrades = rTrades - comprBlocks[iCount];
            rCount--;
        }
        return comprBlocks;
    }

    private float[] getNotionalBlocks(float tNotional, int tLegs) {
        float rNotional = tNotional;
        int rLegs = tLegs;
        float[] notionalBlocks = new float[tLegs];
        for (int iCount = 0; iCount < tLegs; iCount++) {
            notionalBlocks[iCount] = rNotional / rLegs;
            rNotional = rNotional - notionalBlocks[iCount];
            rLegs--;
        }
        return notionalBlocks;
    }
}
