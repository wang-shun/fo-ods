package datastore;


import java.math.BigDecimal;

/**
 * Created by roman on 20/07/2017.
 */
public class EquityTrade {

    private TradeId tradeId;
    private int tradeVersion;
    //private Instant lastUpdated;
    private String productType;

    //private String type;
    //private String id;

    private String instrumentId;
    private String instrumentRic;
    private String instrumentIsin;
    private String buySell;
    private double quantity;
    private BigDecimal price;
    private double consideration;
    private String ccy;
    private String settlementCcy;
    private String executionVenue;
    private String book;
    //private Instant tradeDatetime;
    //private ZonedDateTime valueDate;

    //private String type;
    //private String system;
    //private String id;

    //private String type;
    //private String timestamp;
    //private String userId;
    //private String firstTrade;

    public EquityTrade() {

    }

    public EquityTrade(TradeId tradeId, int tradeVersion, String productType, String instrumentId, String instrumentRic,
                       String instrumentIsin, String buySell, double quantity, BigDecimal price, double consideration,
                       String ccy, String settlementCcy, String executionVenue, String book) {
        this.tradeId = tradeId;
        this.tradeVersion = tradeVersion;
        this.productType = productType;
        this.instrumentId = instrumentId;
        this.instrumentRic = instrumentRic;
        this.instrumentIsin = instrumentIsin;
        this.buySell = buySell;
        this.quantity = quantity;
        this.price = price;
        this.consideration = consideration;
        this.ccy = ccy;
        this.settlementCcy = settlementCcy;
        this.executionVenue = executionVenue;
        this.book = book;
    }

    public TradeId getTradeId() {
        return tradeId;
    }

    public void setTradeId(TradeId tradeId) {
        this.tradeId = tradeId;
    }

    public int getTradeVersion() {
        return tradeVersion;
    }

    public void setTradeVersion(int tradeVersion) {
        this.tradeVersion = tradeVersion;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentRic() {
        return instrumentRic;
    }

    public void setInstrumentRic(String instrumentRic) {
        this.instrumentRic = instrumentRic;
    }

    public String getInstrumentIsin() {
        return instrumentIsin;
    }

    public void setInstrumentIsin(String instrumentIsin) {
        this.instrumentIsin = instrumentIsin;
    }

    public String getBuySell() {
        return buySell;
    }

    public void setBuySell(String buySell) {
        this.buySell = buySell;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public double getConsideration() {
        return consideration;
    }

    public void setConsideration(double consideration) {
        this.consideration = consideration;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSettlementCcy() {
        return settlementCcy;
    }

    public void setSettlementCcy(String settlementCcy) {
        this.settlementCcy = settlementCcy;
    }

    public String getExecutionVenue() {
        return executionVenue;
    }

    public void setExecutionVenue(String executionVenue) {
        this.executionVenue = executionVenue;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EquityTrade{");
        sb.append("tradeId=").append(tradeId);
        sb.append(", tradeVersion=").append(tradeVersion);
        sb.append(", productType='").append(productType).append('\'');
        sb.append(", instrumentId='").append(instrumentId).append('\'');
        sb.append(", instrumentRic='").append(instrumentRic).append('\'');
        sb.append(", instrumentIsin='").append(instrumentIsin).append('\'');
        sb.append(", buySell='").append(buySell).append('\'');
        sb.append(", quantity=").append(quantity);
        sb.append(", price=").append(price);
        sb.append(", consideration=").append(consideration);
        sb.append(", ccy='").append(ccy).append('\'');
        sb.append(", settlementCcy='").append(settlementCcy).append('\'');
        sb.append(", executionVenue='").append(executionVenue).append('\'');
        sb.append(", book='").append(book).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getIdVersion() {
        return tradeId.getId() + "/" + tradeVersion;
    }

    public EquityTrade incrementVersion() {
        return new EquityTrade(tradeId, tradeVersion + 1, productType, instrumentId, instrumentRic,
                instrumentIsin, buySell, quantity, price, consideration, ccy, settlementCcy, executionVenue, book);
    }
}
