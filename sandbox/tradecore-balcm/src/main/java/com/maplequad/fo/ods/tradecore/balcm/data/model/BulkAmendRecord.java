package com.maplequad.fo.ods.tradecore.balcm.data.model;

import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;
import com.maplequad.fo.ods.tradecore.exception.InvalidAssetClassException;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;

import java.util.Date;

/**
 * BulkAmendRecord -
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class BulkAmendRecord {

    //Trade
    private String tradeId;
    private String osTradeId;
    private String tradeDate;
    private String assetClass;
    private String productType;
    private String origSystem;

    //TradeEvent
    private String eventRef;
    private String eventType;
    private String eventStatus;
    private String trader;
    private Date eventTS;

    //TradeLeg
    private int legNumber;
    private String book;
    private String instrumentId;
    private String currency;
    private String exchange;

    //Equity
    private int quantity;
    private float price;
    private String buySellInd;

    //IRS
    private float notional;
    private float rate;
    private float spread;
    private String term;
    private String index;
    private String maturityDate;

    //FX_FORWARD
    private float spotRate;
    private float fwdRate;
    private float allInRate;
    private String ccyPair;
    private String tenor;
    private String valueDate;

    public BulkAmendRecord(Trade trade) {
        //Trade
        this.setTradeId(trade.getTradeId());
        this.setOsTradeId(trade.getOsTradeId());
        this.setAssetClass(trade.getAssetClass());
        this.setTradeDate(StringUtils.format_dd_MMM_YYYY(trade.getTradeDate()));
        this.setProductType(trade.getProductType());
        this.setOrigSystem(trade.getOrigSystem());
        //TradeEvent
        TradeEvent event = trade.getTradeEventList().get(0);
        this.setEventRef(event.getEventReference());
        this.setEventType(event.getEventType());
        this.setEventStatus(event.getEventStatus());
        this.setTrader(event.getTrader());
        this.setEventTS(StringUtils.getDate(event.getTransactionTimeFrom()));
        //TradeLeg
        TradeLeg leg = (TradeLeg) event.getTradeLegList().get(0);
        this.setLegNumber(leg.getLegNumber());
        this.setBook(leg.getBook());
        this.setInstrumentId(leg.getInstrumentId());
        this.setCurrency(leg.getCurrency());
        this.setExchange(leg.getExchange());

        switch (leg.getLegType()) {
            case ProductType.CASH_EQUITY:
                Equity eqLeg = (Equity) leg;
                this.setQuantity(eqLeg.getQuantity());
                this.setPrice(eqLeg.getPrice());
                this.setBuySellInd(eqLeg.getBuySellInd());
                break;
            case ProductType.IR_SWAP:
                Ird irdLeg = (Ird) leg;
                this.setNotional(irdLeg.getNotional());
                this.setRate(irdLeg.getRate());
                this.setSpread(irdLeg.getSpread());
                this.setTerm(irdLeg.getTerm());
                this.setIndex(irdLeg.getIndex());
                this.setMaturityDate(StringUtils.format_dd_MMM_YYYY(irdLeg.getMaturityDate()));
                break;
            case ProductType.FX_FORWARD:
                Fxd fxdLeg = (Fxd) leg;
                this.spotRate = fxdLeg.getSpotRate();
                this.allInRate = fxdLeg.getAllInRate();
                this.fwdRate = allInRate - spotRate;
                this.ccyPair = fxdLeg.getBaseCcy() + fxdLeg.getCounterCcy();
                this.valueDate = fxdLeg.getValueDate().toString();
                break;
            default:
                throw new InvalidAssetClassException(leg.getLegType());
        }
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getOsTradeId() {
        return osTradeId;
    }

    public void setOsTradeId(String osTradeId) {
        this.osTradeId = osTradeId;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getOrigSystem() {
        return origSystem;
    }

    public void setOrigSystem(String origSystem) {
        this.origSystem = origSystem;
    }


    public Date getEventTS() {
        return eventTS;
    }

    public void setEventTS(Date eventTS) {
        this.eventTS = eventTS;
    }

    public String getEventRef() {
        return eventRef;
    }

    public void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public int getLegNumber() {
        return legNumber;
    }

    public void setLegNumber(int legNumber) {
        this.legNumber = legNumber;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
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

    public String getBuySellInd() {
        return buySellInd;
    }

    public void setBuySellInd(String buySellInd) {
        this.buySellInd = buySellInd;
    }

    public float getNotional() {
        return notional;
    }

    public void setNotional(float notional) {
        this.notional = notional;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getSpread() {
        return spread;
    }

    public void setSpread(float spread) {
        this.spread = spread;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public float getSpotRate() {
        return spotRate;
    }

    public void setSpotRate(float spotRate) {
        this.spotRate = spotRate;
    }

    public float getFwdRate() {
        return fwdRate;
    }

    public void setFwdRate(float fwdRate) {
        this.fwdRate = fwdRate;
    }

    public float getAllInRate() {
        return allInRate;
    }

    public void setAllInRate(float allInRate) {
        this.allInRate = allInRate;
    }

    public String getCcyPair() {
        return ccyPair;
    }

    public void setCcyPair(String ccyPair) {
        this.ccyPair = ccyPair;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }
}
