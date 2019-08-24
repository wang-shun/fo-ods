package com.maplequad.fo.ods.tradecore.data.model.trade.eq;

import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;

/***
 * Equity - List of attributes of an equities trade
 *
 * @author Madhav Mindhe
 * @since :   30/08/2017
 */
public class Equity extends TradeLeg {

    public static final String ENTITY = "EQUITY_LEG";

    public static final String CFI_CODE = "cfiCode";
    public static final String QUANTITY = "quantity";
    public static final String PRICE = "price";
    public static final String GROSS_PRICE = "grossPrice";

    private String cfiCode;
    private int quantity;
    private float price;
    private float grossPrice;

    public String getCfiCode() {
        return cfiCode;
    }

    public void setCfiCode(String cfiCode) {
        this.cfiCode = cfiCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getGrossPrice() {
        return grossPrice;
    }

    public void setGrossPrice(float grossPrice) {
        this.grossPrice = grossPrice;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equity)) return false;

        Equity tradeLeg = (Equity) o;

        if (getLegNumber() != tradeLeg.getLegNumber()) return false;
        return getTradeId().equals(tradeLeg.getTradeId());
    }

    @Override
    public int hashCode() {
        int result = getTradeId().hashCode();
        result = 31 * result + getLegNumber();
        return result;
    }

    @Override
    public String toString() {
        return "Equity{" +
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
                ", cfiCode='" + cfiCode + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", grossPrice=" + grossPrice +
                ", buySellInd='" + buySellInd + '\'' +
                '}';
    }
}
