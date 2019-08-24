package spanner;


import java.math.BigDecimal;

/**
 * Created by roman on 20/07/2017.
 */
public class EquityTradeLeg {

    private TradeId tradeId;
    private int tradeVersion;
    private int legId;

    private String type;
    private double quantity;
    private String ccy;
    private String book;
    private String client;
    private int account;
    private int account1m;
    private int account10m;
    private String book1000;

    public EquityTradeLeg() {
    }

    public EquityTradeLeg(TradeId tradeId, int tradeVersion, int legId, String type, double quantity, String ccy,
                          String book, String client, int account, int account1m, int account10m, String book1000) {
        this.tradeId = tradeId;
        this.tradeVersion = tradeVersion;
        this.legId = legId;
        this.type = type;
        this.quantity = quantity;
        this.ccy = ccy;
        this.book = book;
        this.client = client;
        this.account = account;
        this.book1000 = book1000;
        this.account1m = account1m;
        this.account10m = account10m;
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

    public int getLegId() {
        return legId;
    }

    public void setLegId(int legId) {
        this.legId = legId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getBook1000() {
        return book1000;
    }

    public void setBook1000(String book1000) {
        this.book1000 = book1000;
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

    public EquityTradeLeg forNewVersion(int newTradeVersion) {
        EquityTradeLeg result = new EquityTradeLeg(tradeId, newTradeVersion, legId, type, quantity, ccy,
            book, client, account, account1m, account10m, book1000);
        return result;
    }
}
