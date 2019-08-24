package foods.bigtable.repository.query.predicate;

import foods.bigtable.model.TradeId;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by roman on 21/07/2017.
 */
public class AllTradeVersionsPredicate implements Predicate {

    private final TradeId tradeId;

    public AllTradeVersionsPredicate(TradeId tradeId) {
        this.tradeId = tradeId;
    }

    public static AllTradeVersionsPredicate allTradeVersions(TradeId tradeId) {
        return new AllTradeVersionsPredicate(tradeId);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new PrefixFilter(Bytes.toBytes(tradeId.getId()));
        return filter;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AllTradeVersions{");
        sb.append(tradeId).append('}');
        return sb.toString();
    }
}
