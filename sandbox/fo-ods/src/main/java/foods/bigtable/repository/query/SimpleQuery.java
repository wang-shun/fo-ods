package foods.bigtable.repository.query;

import foods.bigtable.repository.query.predicate.Predicate;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by roman on 21/07/2017.
 */
public class SimpleQuery implements Query {

    private final List<Predicate> predicates;
    private Optional<Long> maybeMaxResultSize = Optional.empty();

    public SimpleQuery(List<Predicate> predicates, Optional<Long> maybeMaxResultSize) {
        this.predicates = predicates;
        this.maybeMaxResultSize = maybeMaxResultSize;
    }

    public SimpleQuery(Predicate predicate, Optional<Long> maybeMaxResultSize) {
        this(Collections.singletonList(predicate), maybeMaxResultSize);
    }

    public static SimpleQuery forAll(Predicate... predicate) {
        return new SimpleQuery(Arrays.asList(predicate), Optional.empty());
    }

    public static SimpleQuery forAll(long maxResultSize, Predicate... predicate) {
        return new SimpleQuery(Arrays.asList(predicate), Optional.of(maxResultSize));
    }

    @Override
    public Filter getFilter() {
      FilterList list = new FilterList();
      for (Predicate p: predicates) {
          list.addFilter(p.getFilter());
      }
      if (maybeMaxResultSize.isPresent()) {
        list.addFilter(new PageFilter(maybeMaxResultSize.get()));
      }
      return list;
    }

    @Override
    public Optional<Long> getMaxResultSize() {
        return maybeMaxResultSize;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SimpleQuery{");
        if (maybeMaxResultSize.isPresent()) {
          sb.append("maxRows=").append(maybeMaxResultSize.get()).append(", ");
        }

        sb.append(predicates).append('}');
        return sb.toString();
    }
}
