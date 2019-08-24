package com.maplequad.fo.ods.tradecore.vs.data.model;

import com.maplequad.fo.ods.tradecore.data.model.trade.*;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import com.maplequad.fo.ods.tradecore.data.model.trade.fx.Fxd;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;
import com.maplequad.fo.ods.tradecore.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TradeEventView {

    //Trade
    private String tradeId;
    private String osTradeId;
    private String tradeDate;
    private String assetClass;
    private String tradeType;
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
    private double volume;

    //private List<TradeLegView> tradeLegView;

    public static List<TradeEventView> createView(Trade trade) {

        List<TradeEventView> list;

        TradeEvent event = trade.getTradeEventList().get(0);
        list = event.getTradeLegList().stream().
                map(l -> new TradeEventView(trade,(TradeLeg)l)).collect(toList());

        //Temp Fix
        switch (trade.getTradeType()) {
        case STRUCTURED:
            if(list.size() == 2) {
                TradeEventView view1 = list.get(0);
                TradeEventView view2 = list.get(1);
                if(view1.legNumber == 1){
                    view1.spotRate = view2.spotRate;
                    view1.volume = view2.volume;
                    view1.ccyPair = view2.ccyPair;
                    list = Arrays.asList(view1);
                }else if(view1.legNumber == 2){
                    view2.spotRate = view1.spotRate;
                    view2.volume = view1.volume;
                    view2.ccyPair = view1.ccyPair;
                    list = Arrays.asList(view2);
                }
            }
            break;
        }
        /*     switch (trade.getTradeType()) {
            case SINGLE:
                return new TradeEventView(trade, (TradeLeg)event.getTradeLegList().get(0));
            case STRUCTURED:
                return new TradeEventView(
                    trade,
                    event.getTradeLegList().stream().
                            map(l -> new TradeEventView(trade, (TradeLeg)l)).
                            collect(toList()));
            default:
                throw new RuntimeException("Unknown trade type");
        }*/
        return list;
    }

    private TradeEventView(Trade trade, TradeLeg leg) {
        setTradeDetails(trade);
        setLegDetails(leg);
    }
    

    private void setTradeDetails(Trade trade) {
        //Trade
        this.tradeId = trade.getTradeId();
        this.osTradeId = trade.getOsTradeId();
        this.assetClass = trade.getAssetClass();
        this.tradeDate = StringUtils.format_dd_MMM_YYYY(trade.getTradeDate());
        this.productType = trade.getProductType();
        this.origSystem = trade.getOrigSystem();
        //TradeEvent
        TradeEvent event = trade.getTradeEventList().get(0);
        this.eventRef = event.getEventReference();
        this.eventType = event.getEventType();
        this.eventStatus = event.getEventStatus();
        this.trader = event.getTrader();
        this.eventTS = StringUtils.getDate(event.getTransactionTimeFrom());
    }

    private void setLegDetails(TradeLeg leg) {
        this.legNumber = leg.getLegNumber();
        this.book = leg.getBook();
        this.instrumentId = leg.getInstrumentId();
        this.currency = leg.getCurrency();
        this.exchange = leg.getExchange();

        switch (leg.getLegType()) {
            case ProductType.CASH_EQUITY:
                Equity eqLeg = (Equity) leg;
                this.quantity = eqLeg.getQuantity();
                this.price = eqLeg.getPrice();
                this.buySellInd = eqLeg.getBuySellInd();
                break;
            case ProductType.IR_SWAP:
                Ird irdLeg = (Ird) leg;
                this.notional = irdLeg.getNotional();
                this.rate = irdLeg.getRate();
                this.spread = irdLeg.getSpread();
                this.term = irdLeg.getTerm();
                this.index = irdLeg.getIndex();
                this.maturityDate = StringUtils.format_dd_MMM_YYYY(irdLeg.getMaturityDate());
                break;
            case ProductType.FX_FORWARD:
                Fxd fxdLeg = (Fxd) leg;
                this.spotRate = fxdLeg.getSpotRate();
                this.allInRate = fxdLeg.getAllInRate();
                this.volume = fxdLeg.getVolume();
                this.fwdRate = allInRate - spotRate;
                this.ccyPair = fxdLeg.getBaseCcy() + fxdLeg.getCounterCcy();
                this.valueDate = fxdLeg.getValueDate().toString();
                break;
        }
    }

    public String getTradeId() {
        return tradeId+legNumber;
    }

}
