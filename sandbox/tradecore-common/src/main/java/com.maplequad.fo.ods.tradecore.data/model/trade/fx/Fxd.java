package com.maplequad.fo.ods.tradecore.data.model.trade.fx;

import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;

import java.time.LocalDate;

public class Fxd extends TradeLeg {

    public static final String ENTITY = "FXD_LEG";
    public static final String SPOT_RATE = "spotRate";
    public static final String ALL_IN_RATE = "allInRate";
    public static final String BASE_CCY = "baseCcy";
    public static final String COUNTER_CCY = "counterCcy";  // ??? == currency
    public static final String TENOR = "tenor";
    public static final String VALUE_DATE = "valueDate";
    public static final String VOLUME = "volume";
    private float spotRate;
    private float allInRate;
    private String baseCcy;
    private String counterCcy;
    private String tenor;
    private LocalDate valueDate;
    private float volume;

    public float getSpotRate() {
        return spotRate;
    }

    public void setSpotRate(float spotRate) {
        this.spotRate = spotRate;
    }

    public float getAllInRate() {
        return allInRate;
    }

    public void setAllInRate(float allInRate) {
        this.allInRate = allInRate;
    }

    public String getBaseCcy() {
        return baseCcy;
    }

    public void setBaseCcy(String baseCcy) {
        this.baseCcy = baseCcy;
    }

    public String getCounterCcy() {
        return counterCcy;
    }

    public void setCounterCcy(String counterCcy) {
        this.counterCcy = counterCcy;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Fxd{" +
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
                ", spotRate='" + spotRate + '\'' +
                ", allInRate='" + allInRate + '\'' +
                ", baseCcy='" + baseCcy + '\'' +
                ", counterCcy='" + counterCcy + '\'' +
                ", tenor='" + tenor + '\'' +
                ", valueDate='" + valueDate + '\'' +
                ", volume='" + volume + '\'' +
                ", buySellInd='" + buySellInd + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fxd)) return false;

        Fxd tradeLeg = (Fxd) o;

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
