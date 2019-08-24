package com.maplequad.fo.ods.tradecore.data.model.trade;

import com.google.cloud.Timestamp;
import com.maplequad.fo.ods.tradecore.proto.model.TradeOuterClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/***
 * Trade - List of all immutable attributes of a trade
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class Trade {

    public static final String ENTITY = "TRADE";
    public static final String TRADE_ID = "tradeId";
    public static final String TRADE_DATE = "tradeDate";
    public static final String ASSET_CLASS = "assetClass";
    public static final String TRADE_TYPE = "tradeType";
    public static final String PRODUCT_TYPE = "productType";
    public static final String ORIG_SYS = "origSystem";
    public static final String OS_TRADE_ID = "osTradeId";
    public static final String CREATED_TS = "createdTimeStamp";
    public static final String CREATED_BY = "createdBy";

    //This is used for filtering performance test trades into UI
    public static final String SKIP_UI_EVENT = "SKIP-UI-EVENT";

    //Collections
    List<TradeEvent> tradeEventList;
    //Primary Key
    private String tradeId;
    //Attributes
    private Date tradeDate;
    private String assetClass;
    private TradeOuterClass.TradeType tradeType;
    private String productType;
    private String origSystem;
    private String osTradeId;
    //Audit Trail
    private Timestamp createdTimeStamp;
    private String createdBy;

    private Map<String, String> equalityMap;


    //Default Constructor
    public Trade() {

        this.setTradeEventList(new ArrayList<TradeEvent>());

    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public TradeOuterClass.TradeType getTradeType() {
        return this.tradeType;
    }

    public void setTradeType(TradeOuterClass.TradeType tradeType) {
        this.tradeType = tradeType;
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

    public String getOsTradeId() {
        return osTradeId;
    }

    public void setOsTradeId(String osTradeId) {
        this.osTradeId = osTradeId;
    }

    public List<TradeEvent> getTradeEventList() {
        return tradeEventList;
    }

    public void setTradeEventList(List<TradeEvent> tradeEventList) {
        this.tradeEventList = tradeEventList;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * This method checks if the input trade has the required attributes matching with the filter
     *
     * @param filterTrade - input filter criteria
     * @return boolean - indicator to know if the trade is matched or to be filtered.
     */
    public boolean filter(Trade filterTrade) {
        boolean assetClassCheck = getAssetClass().equals(filterTrade.getAssetClass());
        boolean eventRefCheck = true;
        if (!getTradeEventList().isEmpty()) {
            eventRefCheck = !getTradeEventList().get(0).getEventReference().startsWith(SKIP_UI_EVENT);
        }
        return assetClassCheck && eventRefCheck;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", tradeDate=" + tradeDate +
                ", assetClass='" + assetClass + '\'' +
                ", productType='" + productType + '\'' +
                ", origSystem='" + origSystem + '\'' +
                ", osTradeId='" + osTradeId + '\'' +
                ", tradeEventList=" + tradeEventList +
                ", createdTimeStamp=" + createdTimeStamp +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trade)) return false;

        Trade trade = (Trade) o;

        return getTradeId().equals(trade.getTradeId());
    }

    @Override
    public int hashCode() {
        return getTradeId().hashCode();
    }
}
