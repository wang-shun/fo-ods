package foods.bigtable.repository.query;

import org.apache.hadoop.hbase.filter.Filter;

import java.util.Optional;

/**
 * Created by roman on 20/07/2017.
 */
public interface Query {
    Filter getFilter();
    Optional<Long> getMaxResultSize();
}
