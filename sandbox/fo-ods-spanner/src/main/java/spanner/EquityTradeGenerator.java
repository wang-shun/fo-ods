package spanner;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by roman on 20/07/2017.
 */
public class EquityTradeGenerator {

    private final Random random = new Random();

    private final Randomizer ric = new PresetRandomizer("VOD.L", "BP.L", "BT.L", "BLT.L", "LLOYD.L");
    private final Randomizer isin = new PresetRandomizer("ISIN111111111", "ISIN222222222", "ISIN333333333", "ISIN444444444");
    private final Randomizer buySell = new PresetRandomizer("B", "S");
    private final Randomizer ccy = new PresetRandomizer("GBP", "EUR", "USD", "HKD", "RUB", "JPY");
    private final Randomizer venue = new PresetRandomizer("XLON", "XPAR", "XMAD", "XETR", "XFRA", "BATE", "CHIX", "XCSE", "XAMS");
    private final Randomizer book = new PresetRandomizer("BOOK1", "BOOK2", "BOOK3", "BOOK4", "BOOK5", "BOOK6");
    private final Randomizer client = new RandomizerString(4);
    private final Randomizer book1000 = new RandomizerString(3);

    public Collection<EquityTrade> createTrades(int count, int versionsCount, int legsCount) {
        Collection<EquityTrade> trades = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            EquityTrade trade = createTrade(legsCount);
            trades.add(trade);
            for (int j = 1; j < versionsCount; j++) {
                trade = trade.incrementVersion();
                trades.add(trade);
            }
        }
        return trades;
    }

    public EquityTrade createTrade(int legsCount) {
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
        trade.setBook1000(book1000.nextValue());
        trade.setClient(client.nextValue());
        trade.setAccount(randomInt(100_000));
        trade.setAccount1m(randomInt(1_000_000));
        trade.setAccount10m(randomInt(10_000_000));
        //trade.setTradeDatetime(randomString());
        //trade.setValueDate(randomString());

        //trade.settype(randomString());
        //trade.setsystem(randomString());
        //trade.setid(randomString());

        //trade.settype(randomString());
        //trade.settimestamp(randomString());
        //trade.setuserId(randomString());
        //trade.setfirstTrade(randomString());

        List<EquityTradeLeg> legs = new ArrayList<>(legsCount);
        for (int i = 0; i < legsCount; i++) {
            legs.add(createLeg(trade, i));
        }

        trade.setLegs(legs);

        return trade;
    }

    private EquityTradeLeg createLeg(EquityTrade trade, int i) {
        EquityTradeLeg leg = new EquityTradeLeg();
        leg.setTradeId(trade.getTradeId());
        leg.setTradeVersion(trade.getTradeVersion());
        leg.setLegId(i);
        leg.setType(randomString());

        leg.setQuantity(randomDouble());
        leg.setCcy(ccy.nextValue());
        leg.setBook(book.nextValue());
        leg.setBook1000(book1000.nextValue());
        leg.setClient(client.nextValue());
        leg.setAccount(randomInt(100_000));
        leg.setAccount1m(randomInt(1_000_000));
        leg.setAccount10m(randomInt(10_000_000));
        return leg;
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

    private interface Randomizer {
        String nextValue();
    }

    private class PresetRandomizer implements Randomizer {
        private final String[] values;

        PresetRandomizer(String... values) {
            this.values = values;
        }

        public String nextValue() {
            return values[randomInt(values.length)];
        }
    }

    private class RandomizerString implements Randomizer {

        private final int chars;

        RandomizerString(int chars) {
            this.chars = chars;
        }

        public String nextValue() {
            String str = randomString();
            return str.substring(0, chars);
        }
    }
}
