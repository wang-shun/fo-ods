package com.maplequad.fo.ods.tradecore.md.data.model;

import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.md.data.model.reuters.MarketData;

import java.math.BigDecimal;
import java.util.Date;

/***
 * Fields
 *
 * This class is used to convert the EQ data from Reuters Market Data Model
 * in to the structure that we need to show on UI
 *
 * @author Madhav Mindhe
 * @since :   20/09/2017
 */
public class EqMarketEventView extends MarketEventView {

    protected BigDecimal ltp;
    protected BigDecimal pcLtp;
    protected BigDecimal ncLtp;

    public BigDecimal getLtp() {
        return ltp;
    }

    public void setLtp(BigDecimal ltp) {
        this.ltp = ltp;
    }

    public BigDecimal getPcLtp() {
        return pcLtp;
    }

    public void setPcLtp(BigDecimal pcLtp) {
        this.pcLtp = pcLtp;
    }

    public BigDecimal getNcLtp() {
        return ncLtp;
    }

    public void setNcLtp(BigDecimal ncLtp) {
        this.ncLtp = ncLtp;
    }

    /**
     * @param marketData
     * @param prevAskStr
     * @param prevBidStr
     */
    public EqMarketEventView(MarketData marketData, String prevAskStr, String prevBidStr, String prevLtpStr) {

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

        String ltpStr = null;
        if (marketData.getFields().getTrdPrc1() != null) {
            ltpStr = marketData.getFields().getTrdPrc1().getValue();
            if (!Strings.isNullOrEmpty(ltpStr)) {
                this.setLtp(new BigDecimal(ltpStr));
            }
            BigDecimal prevLtp = getLtp();
            if (!Strings.isNullOrEmpty(prevLtpStr)) {
                prevLtp = new BigDecimal(prevLtpStr);
            }
            if (!Strings.isNullOrEmpty(ltpStr)) {
                this.setPcLtp(round(((getLtp().floatValue() -
                        prevLtp.floatValue()) / prevLtp.floatValue() * 100), 2));
            }
            this.setNcLtp(new BigDecimal(getLtp().floatValue() - prevLtp.floatValue()));
        }

        long quoteTs = Long.parseLong(marketData.getTimestamp());
        this.setQTs(new Date(quoteTs));

        if (marketData.getFields().getDsplyName() != null) {
            this.setDispNm(marketData.getFields().getDsplyName().getValue());
        }
    }
}
