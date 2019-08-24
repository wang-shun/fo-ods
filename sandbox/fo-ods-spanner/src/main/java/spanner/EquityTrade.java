package spanner;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private String client;
    private int account;
    private int account1m;
    private int account10m;
    private String book1000;
    private List<EquityTradeLeg> legs = Collections.emptyList();
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
                       String ccy, String settlementCcy, String executionVenue, String book, String client, int account,
                       int account1m, int account10m, String book1000, List<EquityTradeLeg> legs) {
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
        this.book1000 = book1000;
        this.client = client;
        this.account = account;
        this.account1m = account1m;
        this.account10m = account10m;
        this.legs = new ArrayList<>(legs.size());
        for (EquityTradeLeg leg: legs) {
            EquityTradeLeg newLeg = leg.forNewVersion(tradeVersion);
            this.legs.add(newLeg);
        }
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
        //sb.append(", instrumentIsin='").append(instrumentIsin).append('\'');
        sb.append(", buySell='").append(buySell).append('\'');
        sb.append(", quantity=").append(quantity);
        sb.append(", price=").append(price);
        sb.append(", consideration=").append(consideration);
        sb.append(", ccy='").append(ccy).append('\'');
        sb.append(", settlementCcy='").append(settlementCcy).append('\'');
        sb.append(", executionVenue='").append(executionVenue).append('\'');
        sb.append(", book='").append(book).append('\'');
        sb.append(", book1000='").append(book1000).append('\'');
        sb.append(", client='").append(client).append('\'');
        sb.append(", account='").append(account).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getIdVersion() {
        return tradeId.getId() + "/" + tradeVersion;
    }

    public EquityTrade incrementVersion() {
        return new EquityTrade(tradeId, tradeVersion + 1, productType, instrumentId, instrumentRic,
            instrumentIsin, buySell, quantity, price, consideration, ccy, settlementCcy, executionVenue, book,
            client, account, account1m, account10m, book1000, legs);
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getAccount() {
        return account;
    }
    public String getClient() {
        return client;
    }

    public void setBook1000(String book1000) {
        this.book1000 = book1000;
    }

    public String getBook1000() {
        return book1000;
    }

    public int getAccount1m() {
        return account1m;
    }

    public void setAccount1m(int account1m) {
        this.account1m = account1m;
    }

    public int getAccount10m() {
        return account10m;
    }

    public void setAccount10m(int account10m) {
        this.account10m = account10m;
    }

    public List<EquityTradeLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<EquityTradeLeg> legs) {
        this.legs = legs;
    }
}
