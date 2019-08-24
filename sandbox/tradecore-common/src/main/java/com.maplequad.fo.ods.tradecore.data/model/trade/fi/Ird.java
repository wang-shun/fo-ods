package com.maplequad.fo.ods.tradecore.data.model.trade.fi;

import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;

import java.util.Date;

/***
 * Ird - List of attributes of an Ird trade
 *
 * @author Madhav Mindhe
 * @since :   30/08/2017
 */
public class Ird extends TradeLeg {

    public static final String ENTITY = "IRD_LEG";

    public static final String IRD_LEG_TYPE = "irdLegType";
    public static final String MATURITY_DATE = "maturityDate";
    public static final String NOTIONAL = "notional";
    public static final String INDEX = "index";
    public static final String NOTIONAL_EXP = "notionalExp";
    public static final String TERM = "term";
    public static final String RATE = "rate";
    public static final String BASIS = "basis";
    public static final String SPREAD = "spread";
    public static final String SETTLEMENT_CCY = "settlementCcy";
    public static final String SETTLEMENT_AMNT = "settlementAmount";
    public static final String SETTLEMENT_DATE = "settlementDate";

    private String irdLegType;
    private Date maturityDate;
    private float notional;
    private String index;
    private String notionalExp;
    private String term;
    private float rate;
    private String basis;
    private float spread;
    private String settlementCcy;
    private float settlementAmount;
    private Date settlementDate;

    public String getIrdLegType() {
        return irdLegType;
    }

    public void setIrdLegType(String irdLegType) {
        this.irdLegType = irdLegType;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public float getNotional() {
        return notional;
    }

    public void setNotional(float notional) {
        this.notional = notional;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getNotionalExp() {
        return notionalExp;
    }

    public void setNotionalExp(String notionalExp) {
        this.notionalExp = notionalExp;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public float getSpread() {
        return spread;
    }

    public void setSpread(float spread) {
        this.spread = spread;
    }

    public String getSettlementCcy() {
        return settlementCcy;
    }

    public void setSettlementCcy(String settlementCcy) {
        this.settlementCcy = settlementCcy;
    }

    public float getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(float settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    @Override
    public String toString() {
        return "Ird{" +
                "tradeId='" + tradeId + '\'' +
                ", legNumber=" + legNumber +
                ", legType=" + legType +
                ", book='" + book + '\'' +
                ", internalProductType='" + internalProductType + '\'' +
                ", internalProductRef='" + internalProductRef + '\'' +
                ", instrumentId='" + instrumentId + '\'' +
                ", ric='" + ric + '\'' +
                ", isin='" + isin + '\'' +
                ", currency='" + currency + '\'' +
                ", exchange='" + exchange + '\'' +
                ", irdLegType='" + irdLegType + '\'' +
                ", maturityDate=" + maturityDate +
                ", notional=" + notional +
                ", index=" + index +
                ", notionalExp='" + notionalExp + '\'' +
                ", term='" + term + '\'' +
                ", rate='" + rate + '\'' +
                ", basis='" + basis + '\'' +
                ", spread='" + spread + '\'' +
                ", settlementCcy='" + settlementCcy + '\'' +
                ", settlementAmount='" + settlementAmount + '\'' +
                ", settlementDate='" + settlementDate + '\'' +
                ", buySellInd='" + buySellInd + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ird)) return false;

        Ird tradeLeg = (Ird) o;

        if (getLegNumber() != tradeLeg.getLegNumber()) return false;
        return getTradeId().equals(tradeLeg.getTradeId());
    }

    @Override
    public int hashCode() {
        int result = getTradeId().hashCode();
        result = 31 * result + getLegNumber();
        return result;
    }
}
