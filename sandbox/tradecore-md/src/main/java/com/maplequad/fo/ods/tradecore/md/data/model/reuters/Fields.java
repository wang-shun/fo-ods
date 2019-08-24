package com.maplequad.fo.ods.tradecore.md.data.model.reuters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.maplequad.fo.ods.tradecore.md.data.model.reuters.attribute.*;

/***
 * Fields
 *
 * This is part of the Reuters Market Data Model
 *
 * @author Madhav Mindhe
 * @since :   10/09/2017
 */
public class Fields {

    @SerializedName("BID")
    @Expose
    private BID bid;
    @SerializedName("QUOTE_DATE")
    @Expose
    private QUOTEDATE quoteDate;
    @SerializedName("DSPLY_NAME")
    @Expose
    private DSPLYNAME dsplyName;
    @SerializedName("GV4_TEXT")
    @Expose
    private GV4TEXT gv4Text;
    @SerializedName("PRIMACT_1")
    @Expose
    private PRIMACT1 primAct1;
    @SerializedName("GN_TXT16_2")
    @Expose
    private GNTXT162 gnTxt162;
    @SerializedName("QUOTIM_MS")
    @Expose
    private QUOTIMMS quoTimMs;
    @SerializedName("SEC_ACT_1")
    @Expose
    private SECACT1 secAct1;
    @SerializedName("ASK")
    @Expose
    private ASK ask;
    @SerializedName("VALUE_TS1")
    @Expose
    private VALUETS1 valueTs1;
    @SerializedName("CTBTR_1")
    @Expose
    private CTBTR1 ctbTr1;
    @SerializedName("GN_TXT24_1")
    @Expose
    private GNTXT241 gnTxt241;
    @SerializedName("GEN_VAL3")
    @Expose
    private GENVAL3 getVal3;
    @SerializedName("PROD_PERM")
    @Expose
    private PRODPERM prodPerm;
    @SerializedName("TRDPRC_1")
    @Expose
    private TRDPRC_1 trdPrc1;

    public BID getBid() {
        return bid;
    }

    public void setBid(BID bid) {
        this.bid = bid;
    }

    public QUOTEDATE getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(QUOTEDATE quoteDate) {
        this.quoteDate = quoteDate;
    }

    public DSPLYNAME getDsplyName() {
        return dsplyName;
    }

    public void setDsplyName(DSPLYNAME dsplyName) {
        this.dsplyName = dsplyName;
    }

    public GV4TEXT getGv4Text() {
        return gv4Text;
    }

    public void setGv4Text(GV4TEXT gv4Text) {
        this.gv4Text = gv4Text;
    }

    public PRIMACT1 getPrimAct1() {
        return primAct1;
    }

    public void setPrimAct1(PRIMACT1 primAct1) {
        this.primAct1 = primAct1;
    }

    public GNTXT162 getGnTxt162() {
        return gnTxt162;
    }

    public void setGnTxt162(GNTXT162 gnTxt162) {
        this.gnTxt162 = gnTxt162;
    }

    public QUOTIMMS getQuoTimMs() {
        return quoTimMs;
    }

    public void setQuoTimMs(QUOTIMMS quoTimMs) {
        this.quoTimMs = quoTimMs;
    }

    public SECACT1 getSecAct1() {
        return secAct1;
    }

    public void setSecAct1(SECACT1 secAct1) {
        this.secAct1 = secAct1;
    }

    public ASK getAsk() {
        return ask;
    }

    public void setAsk(ASK ask) {
        this.ask = ask;
    }

    public VALUETS1 getValueTs1() {
        return valueTs1;
    }

    public void setValueTs1(VALUETS1 valueTs1) {
        this.valueTs1 = valueTs1;
    }

    public CTBTR1 getCtbTr1() {
        return ctbTr1;
    }

    public void setCtbTr1(CTBTR1 ctbTr1) {
        this.ctbTr1 = ctbTr1;
    }

    public GNTXT241 getGnTxt241() {
        return gnTxt241;
    }

    public void setGnTxt241(GNTXT241 gnTxt241) {
        this.gnTxt241 = gnTxt241;
    }

    public GENVAL3 getGetVal3() {
        return getVal3;
    }

    public void setGetVal3(GENVAL3 getVal3) {
        this.getVal3 = getVal3;
    }

    public PRODPERM getProdPerm() {
        return prodPerm;
    }

    public void setProdPerm(PRODPERM prodPerm) {
        this.prodPerm = prodPerm;
    }

    public TRDPRC_1 getTrdPrc1() {
        return trdPrc1;
    }

    public void setTrdPrc1(TRDPRC_1 trdPrc1) {
        this.trdPrc1 = trdPrc1;
    }

    @Override
    public String toString() {
        return "Fields{" +
                "bid=" + bid +
                ", quoteDate=" + quoteDate +
                ", dsplyName=" + dsplyName +
                ", gv4Text=" + gv4Text +
                ", primAct1=" + primAct1 +
                ", gnTxt162=" + gnTxt162 +
                ", quoTimMs=" + quoTimMs +
                ", secAct1=" + secAct1 +
                ", ask=" + ask +
                ", valueTs1=" + valueTs1 +
                ", ctbTr1=" + ctbTr1 +
                ", gnTxt241=" + gnTxt241 +
                ", getVal3=" + getVal3 +
                ", prodPerm=" + prodPerm +
                ", trdPrc1=" + trdPrc1 +
                '}';
    }
}