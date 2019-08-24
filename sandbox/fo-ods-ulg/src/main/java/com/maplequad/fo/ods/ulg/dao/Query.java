package com.maplequad.fo.ods.ulg.dao;

import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/***
 * SimpleQuery
 *
 * This implements IQuery interface.
 *
 * @author Madhav Mindhe
 * @since :   04/08/2017
 */
public class Query implements IQuery {

    private final List<Predicate> predicates;
    private Optional<Long> maybeMaxResultSize = Optional.empty();

    public Query(List<Predicate> predicates, Optional<Long> maybeMaxResultSize) {
        this.predicates = predicates;
        this.maybeMaxResultSize = maybeMaxResultSize;
    }

    public Query(Predicate predicate, Optional<Long> maybeMaxResultSize) {
        this(Collections.singletonList(predicate), maybeMaxResultSize);
    }

    public static Query forAll(Predicate... predicate) {

        return new Query(Arrays.asList(predicate), Optional.empty());
    }

    public static Query forAll(long maxResultSize, Predicate... predicate) {
        return new Query(Arrays.asList(predicate), Optional.of(maxResultSize));
    }

    @Override
    public Filter getFilter() {
        Filter result;
        if (predicates.size() == 1) {
            result = predicates.get(0).getFilter();
        } else {
            FilterList list = new FilterList();
            for (Predicate p: predicates) {
                list.addFilter(p.getFilter());
            }
            result = list;
        }
        return result;
    }

    @Override
    public Optional<Long> getMaxResultSize() {
        return maybeMaxResultSize;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Query{");
        sb.append(predicates).append('}');
        return sb.toString();
    }
}
