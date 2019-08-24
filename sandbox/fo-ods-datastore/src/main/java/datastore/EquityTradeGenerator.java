package datastore;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Created by roman on 20/07/2017.
 */
public class EquityTradeGenerator {

    private final Random random = new Random();

    private final Randomizer ric = new Randomizer("VOD.L", "BP.L", "BT.L", "BLT.L", "LLOYD.L");
    private final Randomizer isin = new Randomizer("ISIN111111111", "ISIN222222222", "ISIN333333333", "ISIN444444444");
    private final Randomizer buySell = new Randomizer("B", "S");
    private final Randomizer ccy = new Randomizer("GBP", "EUR", "USD", "HKD", "RUB", "JPY");
    private final Randomizer venue = new Randomizer("XLON", "XPAR", "XMAD", "XETR", "XFRA", "BATE", "CHIX", "XCSE", "XAMS");
    private final Randomizer book = new Randomizer("BOOK1", "BOOK2", "BOOK3", "BOOK4", "BOOK5", "BOOK6");

    public Collection<EquityTrade> createTrades(int count, int versionsCount) {
        Collection<EquityTrade> trades = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            EquityTrade trade = createTrade();
            trades.add(trade);
            for (int j = 1; j < versionsCount; j++) {
                trade = trade.incrementVersion();
                trades.add(trade);
            }
        }
        return trades;
    }

    public EquityTrade createTrade() {
        EquityTrade trade = new EquityTrade();


        trade.setTradeId(new TradeId(randomString()));
        trade.setTradeVersion(randomInt(100));
        //trade.setLastUpdated(randomString());
        trade.setProductType("eq");

        //trade.setType(randomString());
        //trade.setd(randomString());


        trade.setInstrumentId(randomString());
        trade.setInstrumentRic(ric.nextValue());
        trade.setInstrumentIsin(randomBlob());
        trade.setBuySell(buySell.nextValue());
        trade.setQuantity(randomDouble());
        trade.setPrice(randomDecimal());
        trade.setConsideration(randomDouble());
        trade.setCcy(ccy.nextValue());
        trade.setSettlementCcy(ccy.nextValue());
        trade.setExecutionVenue(venue.nextValue());
        trade.setBook(book.nextValue());
        //trade.setTradeDatetime(randomString());
        //trade.setValueDate(randomString());

        //trade.settype(randomString());
        //trade.setsystem(randomString());
        //trade.setid(randomString());

        //trade.settype(randomString());
        //trade.settimestamp(randomString());
        //trade.setuserId(randomString());
        //trade.setfirstTrade(randomString());


        return trade;
    }

    private String randomBlob() {
        String s = randomString();
        StringBuffer sb = new StringBuffer();
        // 18*2000 = 35 KB
        for (int i = 0; i < 2000; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    private BigDecimal randomDecimal() {
        BigDecimal value = BigDecimal.valueOf(randomDouble());
        return value;
    }

    private String randomString() {
        return String.valueOf(randomLong());
    }

    private long randomLong() {
        return random.nextLong() & Long.MAX_VALUE;
    }

    private int randomInt(int bound) {
        int value = random.nextInt(bound) & Integer.MAX_VALUE;
        return value;
    }

    private double randomDouble() {
        double value = random.nextDouble() * 1000;
        return value;
    }

    private class Randomizer {
        private final String[] values;

        Randomizer(String... values) {
            this.values = values;
        }

        private String nextValue() {
            return values[randomInt(values.length)];
        }
    }
}
