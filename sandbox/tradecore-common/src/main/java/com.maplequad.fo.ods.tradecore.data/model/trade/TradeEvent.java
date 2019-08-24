package com.maplequad.fo.ods.tradecore.data.model.trade;

import com.google.common.base.Objects;
import com.maplequad.fo.ods.tradecore.data.model.BiTemporal;

import java.util.ArrayList;
import java.util.List;

/***
 * TradeEvent - List of all attributes that form event of a trade
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class TradeEvent extends BiTemporal {

    public static final String ENTITY = "TRADE_EVENT";
    public static final String TRADE_ID = "tradeId";
    public static final String PARENT_TRADE_ID = "parentTradeId";
    public static final String EVENT_TYPE = "eventType";
    public static final String EVENT_STATUS = "eventStatus";
    public static final String EVENT_REF = "eventReference";
    public static final String EVENT_REM = "eventRemarks";
    public static final String NO_OF_LEGS = "noOfLegs";
    public static final String OS_VERSION = "osVersion";
    public static final String OS_VERSION_STATUS = "osVersionStatus";
    public static final String ORDER_ID = "orderId";
    public static final String EXCH_EXEC_ID = "exchangeExecutionId";
    public static final String TRADER_ID = "trader";
    public static final String SALESMAN_ID = "salesman";
    public static final String TRADER_CNTRY = "traderCountry";
    public static final String SALESMAN_CNTRY = "salesmanCountry";
    public static final String CREATED_BY = "createdBy";
    //public static final String ACTIVE_FLAG = "activeFlag";
    //public static final String LOCKED_FLAG = "lockedFlag";

    //Primary Key
    private String tradeId;

    //Attributes
    private String parentTradeId;

    private String eventType;
    private String eventStatus;
    private String eventReference;
    private String eventRemarks="";
    private int noOfLegs;
    private String osVersion;
    private String osVersionStatus;
    private String orderId;
    private String exchangeExecutionId;
    private String trader;
    private String salesman;
    private String traderCountry;
    private String salesmanCountry;

    //Collections
    private List<ITradeLeg> tradeLegList;
    private List<TradeParty> tradePartyList;

    //Audit Trail
    private String createdBy;

    //Faster Bi-Temporal Reads
    private boolean activeFlag;

    //Used for Amends with no pending BulkAmends
    private boolean lockedFlag;

    //Default Constructor
    public TradeEvent() {

        this.setTradeLegList(new ArrayList<ITradeLeg>());
        this.setTradePartyList(new ArrayList<TradeParty>());
        //this.setValidTimeTo(END_OF_TIME);
        //this.setTransactionTimeTo(END_OF_TIME);
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getParentTradeId() {
        return parentTradeId;
    }

    public void setParentTradeId(String parentTradeId) {
        this.parentTradeId = parentTradeId;
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

    public String getEventReference() {
        return eventReference;
    }

    public void setEventReference(String eventReference) {
        this.eventReference = eventReference;
    }

    public String getEventRemarks() {
        return eventRemarks;
    }

    public void setEventRemarks(String eventRemarks) {
        this.eventRemarks = eventRemarks;
    }

    public int getNoOfLegs() {
        return noOfLegs;
    }

    public void setNoOfLegs(int noOfLegs) {
        this.noOfLegs = noOfLegs;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsVersionStatus() {
        return osVersionStatus;
    }

    public void setOsVersionStatus(String osVersionStatus) {
        this.osVersionStatus = osVersionStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getExchangeExecutionId() {
        return exchangeExecutionId;
    }

    public void setExchangeExecutionId(String exchangeExecutionId) {
        this.exchangeExecutionId = exchangeExecutionId;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getTraderCountry() {
        return traderCountry;
    }

    public void setTraderCountry(String traderCountry) {
        this.traderCountry = traderCountry;
    }

    public String getSalesmanCountry() {
        return salesmanCountry;
    }

    public void setSalesmanCountry(String salesmanCountry) {
        this.salesmanCountry = salesmanCountry;
    }

    public List<ITradeLeg> getTradeLegList() {
        return tradeLegList;
    }

    public void setTradeLegList(List<ITradeLeg> tradeLegList) {
        this.tradeLegList = tradeLegList;
    }

    public List<TradeParty> getTradePartyList() {
        return tradePartyList;
    }

    public void setTradePartyList(List<TradeParty> tradePartyList) {
        this.tradePartyList = tradePartyList;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "TradeEvent{" +
                "tradeId=" + tradeId +
                ", validTimeFrom=" + this.getValidTimeFrom() + '\'' +
                ", validTimeTo=" + this.getValidTimeTo() + '\'' +
                ", transactionTimeFrom=" + this.getTransactionTimeFrom() + '\'' +
                ", transactionTimeTo=" + this.getTransactionTimeTo() + '\'' +
                ", parentTradeId=" + parentTradeId +
                ", eventType='" + eventType + '\'' +
                ", eventStatus='" + eventStatus + '\'' +
                ", eventReference='" + eventReference + '\'' +
                ", eventRemarks='" + eventRemarks + '\'' +
                ", noOfLegs=" + noOfLegs +
                ", osVersion='" + osVersion + '\'' +
                ", osVersionStatus='" + osVersionStatus + '\'' +
                ", orderId='" + orderId + '\'' +
                ", exchangeExecutionId='" + exchangeExecutionId + '\'' +
                ", trader='" + trader + '\'' +
                ", salesman='" + salesman + '\'' +
                ", traderCountry='" + traderCountry + '\'' +
                ", salesmanCountry='" + salesmanCountry + '\'' +
                ", tradeLegList=" + tradeLegList +
                ", tradePartyList=" + tradePartyList +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeEvent)) return false;
        TradeEvent that = (TradeEvent) o;
        return getNoOfLegs() == that.getNoOfLegs() &&
                Objects.equal(getTradeId(), that.getTradeId()) &&
                Objects.equal(getValidTimeFrom(), that.getValidTimeFrom()) &&
                Objects.equal(getValidTimeTo(), that.getValidTimeTo()) &&
                Objects.equal(getTransactionTimeFrom(), that.getTransactionTimeFrom()) &&
                Objects.equal(getTransactionTimeTo(), that.getTransactionTimeTo()) &&
                Objects.equal(getParentTradeId(), that.getParentTradeId()) &&
                Objects.equal(getEventType(), that.getEventType()) &&
                Objects.equal(getEventStatus(), that.getEventStatus()) &&
                Objects.equal(getEventReference(), that.getEventReference()) &&
                Objects.equal(getEventRemarks(), that.getEventRemarks()) &&
                Objects.equal(getOsVersion(), that.getOsVersion()) &&
                Objects.equal(getOsVersionStatus(), that.getOsVersionStatus()) &&
                Objects.equal(getOrderId(), that.getOrderId()) &&
                Objects.equal(getExchangeExecutionId(), that.getExchangeExecutionId()) &&
                Objects.equal(getTrader(), that.getTrader()) &&
                Objects.equal(getSalesman(), that.getSalesman()) &&
                Objects.equal(getTraderCountry(), that.getTraderCountry()) &&
                Objects.equal(getSalesmanCountry(), that.getSalesmanCountry()) &&
                Objects.equal(getCreatedBy(), that.getCreatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTradeId(), getValidTimeFrom(), getValidTimeTo(),
                getTransactionTimeFrom(), getTransactionTimeTo(), getParentTradeId(), getEventType(),
                getEventStatus(), getEventReference(), getEventRemarks(), getNoOfLegs(), getOsVersion(),
                getOsVersionStatus(), getOrderId(), getExchangeExecutionId(), getTrader(), getSalesman(),
                getTraderCountry(), getSalesmanCountry(), getCreatedBy());
    }
}
