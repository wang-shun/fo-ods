package foods.bigtable.repository.query.predicate;

import org.apache.hadoop.hbase.filter.Filter;

/**
 * Created by roman on 20/07/2017.
 */
public interface Predicate {
    Filter getFilter();
}
