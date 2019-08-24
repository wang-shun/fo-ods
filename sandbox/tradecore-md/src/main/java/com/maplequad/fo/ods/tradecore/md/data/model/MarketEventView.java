package com.maplequad.fo.ods.tradecore.md.data.model;

import com.google.common.base.Strings;
import com.maplequad.fo.ods.tradecore.md.data.model.reuters.MarketData;

import java.math.BigDecimal;
import java.util.Date;

/***
 * Fields
 *
 * This class is used to convert the data from Reuters Market Data Model
 * in to the structure that we need to show on UI
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public abstract class MarketEventView {

    protected String ric;
    protected BigDecimal ask;
    protected BigDecimal bid;
    protected BigDecimal pcBid;
    protected BigDecimal pcAsk;
    protected BigDecimal ncBid;
    protected BigDecimal ncAsk;
    protected String dispNm;
    protected Date qTs;

    /**
     * @param d
     * @param decimalPlace
     * @return
     */
    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public String getRic() {
        return ric;
    }

    protected void setRic(String ric) {
        this.ric = ric;
    }

    protected BigDecimal getAsk() {
        return ask;
    }

    protected void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    protected BigDecimal getBid() {
        return bid;
    }

    protected void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getPcBid() {
        return pcBid;
    }

    protected void setPcBid(BigDecimal pcBid) {
        this.pcBid = pcBid;
    }

    protected BigDecimal getPcAsk() {
        return pcAsk;
    }

    protected void setPcAsk(BigDecimal pcAsk) {
        this.pcAsk = pcAsk;
    }

    protected BigDecimal getNcBid() {
        return ncBid;
    }

    protected void setNcBid(BigDecimal ncBid) {
        this.ncBid = ncBid;
    }

    protected BigDecimal getNcAsk() {
        return ncAsk;
    }

    protected void setNcAsk(BigDecimal ncAsk) {
        this.ncAsk = ncAsk;
    }

    protected String getDispNm() {
        return dispNm;
    }

    protected void setDispNm(String dispNm) {
        this.dispNm = dispNm;
    }
    public Date getQTs() {
        return qTs;
    }

    protected void setQTs(Date qTs) {
        this.qTs = qTs;
    }

}
