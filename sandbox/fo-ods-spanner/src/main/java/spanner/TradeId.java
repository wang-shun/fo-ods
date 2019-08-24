package spanner;

/**
 * Created by roman on 20/07/2017.
 */
public class TradeId {
    private final String id;

    public TradeId(String id) {
        this.id = id;
    }

    public static TradeId tradeId(String id) {
        return new TradeId(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TradeId{");
        sb.append(id).append('}');
        return sb.toString();
    }
}
