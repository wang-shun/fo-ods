package com.maplequad.fo.ods.tradecore.md.data.model;

import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.md.data.model.reuters.MarketData;

import java.math.BigDecimal;
import java.util.Date;

/***
 * Fields
 *
 * This class is used to convert the FX data from Reuters Market Data Model
 * in to the structure that we need to show on UI
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public class FxMarketEventView extends MarketEventView {

    private String qDt;
    private Date qVal;
    private String cntrbtr;

    /**
     * @param marketData
     * @param prevAskStr
     * @param prevBidStr
     */
    public FxMarketEventView(MarketData marketData, String prevAskStr, String prevBidStr) {

        this.setRic(marketData.getRic());
        String askStr = null;
        if (marketData.getFields().getAsk() != null) {
            askStr = marketData.getFields().getAsk().getValue();
            if (!Strings.isNullOrEmpty(askStr)) {
                this.setAsk(new BigDecimal(askStr));
            }
            BigDecimal prevAsk = getAsk();
            if (!Strings.isNullOrEmpty(prevAskStr)) {
                prevAsk = new BigDecimal(prevAskStr);
            }
            if (!Strings.isNullOrEmpty(askStr)) {
                this.setPcAsk(round(((getAsk().floatValue() -
                        prevAsk.floatValue()) / prevAsk.floatValue() * 100), 2));
            }
            this.setNcAsk(new BigDecimal(getAsk().floatValue() - prevAsk.floatValue()));
        }

        String bidStr = null;
        if (marketData.getFields().getBid() != null) {
            bidStr = marketData.getFields().getBid().getValue();
            if (!Strings.isNullOrEmpty(bidStr)) {
                this.setBid(new BigDecimal(bidStr));
            }
            BigDecimal prevBid = getBid();
            if (!Strings.isNullOrEmpty(prevBidStr)) {
                prevBid = new BigDecimal(prevBidStr);
            }
            if (!Strings.isNullOrEmpty(bidStr)) {
                this.setPcBid(round(((getBid().floatValue() -
                        prevBid.floatValue()) / prevBid.floatValue() * 100), 2));
            }
            this.setNcBid(new BigDecimal(getBid().floatValue() - prevBid.floatValue()));
        }

        if (marketData.getFields().getDsplyName() != null) {
            this.setDispNm(marketData.getFields().getDsplyName().getValue());
        }

        if (marketData.getFields().getQuoteDate() != null) {
            this.setQDt(marketData.getFields().getQuoteDate().getValue());
        }

        long quoteTs = Long.parseLong(marketData.getTimestamp());
        this.setQTs(new Date(quoteTs));
        if (marketData.getFields().getQuoTimMs() != null) {
            String quoteTimMs = marketData.getFields().getQuoTimMs().getValue();
            if (!Strings.isNullOrEmpty(quoteTimMs)) {
                long quoteVal = Long.parseLong(quoteTimMs);
                this.setQVal(new Date((quoteTs + quoteVal)));
            }
        }
        if (marketData.getFields().getCtbTr1() != null) {
            this.setCntrbtr(marketData.getFields().getCtbTr1().getValue());
        }
    }


    private String getQDt() {
        return qDt;
    }

    private void setQDt(String qDt) {
        this.qDt = qDt;
    }

    private Date getQVal() {
        return qVal;
    }

    private void setQVal(Date qVal) {
        this.qVal = qVal;
    }

    private String getCntrbtr() {
        return cntrbtr;
    }

    private void setCntrbtr(String cntrbtr) {
        this.cntrbtr = cntrbtr;
    }

}
