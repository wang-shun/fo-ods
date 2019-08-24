package foods.bigtable.repository.query.predicate;

import foods.bigtable.model.TradeId;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by roman on 21/07/2017.
 */
public class PrefixPredicate implements Predicate {

    private final String prefix;

    public PrefixPredicate(String prefix) {
        this.prefix = prefix;
    }

    public static PrefixPredicate prefix(String prefix) {
        return new PrefixPredicate(prefix);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new PrefixFilter(Bytes.toBytes(prefix));
        return filter;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Prefix{");
        sb.append(prefix).append('}');
        return sb.toString();
    }
}
