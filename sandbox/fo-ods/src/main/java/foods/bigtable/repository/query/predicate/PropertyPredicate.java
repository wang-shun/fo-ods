package foods.bigtable.repository.query.predicate;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

/**
 * Created by roman on 21/07/2017.
 */
public class PropertyPredicate implements Predicate {

    private final byte[] family;
    private final byte[] qualifier;

    private final byte[] value;
    private final CompareFilter.CompareOp compareOp;
    private final String desc;
    private final String valueAsString;

    public PropertyPredicate(byte[] family, byte[] qualifier, CompareFilter.CompareOp compareOp, byte[] value,
                             String desc, String valueAsString) {
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
        this.compareOp = compareOp;
        this.desc = desc;
        this.valueAsString = valueAsString;
    }

    @Override
    public Filter getFilter() {
        SingleColumnValueFilter filter = new SingleColumnValueFilter(family, qualifier, compareOp, value);
        filter.setFilterIfMissing(true);
        return filter;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(desc);
        sb.append("{").append(compareOp.name()).append(" ").append(valueAsString).append('}');
        return sb.toString();
    }
}
