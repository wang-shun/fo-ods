package spanner;

/**
 * Created by roman on 20/07/2017.
 */
public class TradeDetails {
    public final String id;
    public final int version;
    public final int account;

    public TradeDetails(String id, int version, int account) {
        this.id = id; this.version = version;
        this.account = account;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TradeDetails{");
        sb.append(id).append(", ");
        sb.append(version).append(", ");
        sb.append(account).append('}');
        return sb.toString();
    }
}
