package com.maplequad.fo.ods.tradecore.data.model.trade;

import com.maplequad.fo.ods.tradecore.data.model.BiTemporal;

/***
 * TradeLeg - List of all attributes that form individual leg of a trade
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public abstract class TradeLeg extends BiTemporal implements ITradeLeg {

    public static final String TRADE_ID = "tradeId";
    public static final String LEG_NUMBER = "legNumber";
    public static final String LEG_TYPE = "legType";

    public static final String BOOK = "book";
    public static final String INT_PRD_TYPE = "internalProductType";
    public static final String INT_PRD_REF = "internalProductRef";
    public static final String INSTRUMENT_ID = "instrumentId";
    public static final String RIC = "ric";
    public static final String ISIN = "isin";
    public static final String CURRENCY = "currency";

    public static final String EXCHANGE = "exchange";
    public static final String BUY_SELL_IND = "buySellInd";


    //Primary Key
    protected String tradeId;
    protected int legNumber = 1;

    //Attributes
    protected String legType;
    protected String book;
    protected String internalProductType;
    protected String internalProductRef;
    protected String instrumentId;
    protected String ric;
    protected String isin;
    protected String currency;
    protected String exchange;
    protected String buySellInd;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public void setLegNumber(int legNumber) {
        this.legNumber = legNumber;
    }

    public String getLegType() {
        return legType;
    }

    public void setLegType(String legType) {
        this.legType = legType;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getInternalProductType() {
        return internalProductType;
    }

    public void setInternalProductType(String internalProductType) {
        this.internalProductType = internalProductType;
    }

    public String getInternalProductRef() {
        return internalProductRef;
    }

    public void setInternalProductRef(String internalProductRef) {
        this.internalProductRef = internalProductRef;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getRic() {
        return ric;
    }

    public void setRic(String ric) {
        this.ric = ric;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getBuySellInd() {
        return buySellInd;
    }

    public void setBuySellInd(String buySellInd) {
        this.buySellInd = buySellInd;
    }
}
