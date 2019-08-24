package com.maplequad.fo.ods.ulg.dao;

import org.apache.hadoop.hbase.filter.Filter;

/***
 * Predicate
 *
 * This is an interface.
 *
 * @author Madhav Mindhe
 * @since :   04/08/2017
 */
public interface IPredicate {
    Filter getFilter();
}
