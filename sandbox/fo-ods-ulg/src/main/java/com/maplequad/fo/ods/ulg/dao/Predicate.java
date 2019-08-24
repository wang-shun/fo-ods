package com.maplequad.fo.ods.ulg.dao;

import com.maplequad.fo.ods.ulg.utils.StringUtils;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.EQUAL;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.GREATER;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.LESS;

/***
 * This class implements IPredicate
 *
 * @author Madhav Mindhe
 * @since :   04/08/2017
 */
public class Predicate implements IPredicate {

    private final byte[] family;
    private final byte[] qualifier;
    private final byte[] value;
    private final CompareFilter.CompareOp compareOp;
    private final String valueAsString;
    private final String dataType;
    private final String desc;
    private final boolean allVersionsFlag;
    private final static String DOT = ".";


    public Predicate(String columnFamily, String qualifier, String valueAsString,CompareFilter.CompareOp compareOp){
        this.family = Bytes.toBytes(columnFamily);
        this.qualifier =  Bytes.toBytes(qualifier);
        this.value =  Bytes.toBytes(valueAsString);
        this.valueAsString = valueAsString;
        this.dataType = "String";
        this.allVersionsFlag = false;
        this.compareOp = compareOp;
        this.desc = StringUtils.concat(columnFamily,DOT,qualifier);
    }

    @Override
    public Filter getFilter() {
        if(allVersionsFlag){
            Filter filter = new PrefixFilter(value);
            return filter;
        }
        else{
            SingleColumnValueFilter filter = new SingleColumnValueFilter(family, qualifier, compareOp, value);
            filter.setFilterIfMissing(true);
            return filter;
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(desc);
        sb.append("{").append(compareOp.name()).append(" ").append(valueAsString).append('}');
        return sb.toString();
    }

    public Predicate equal(String key, String value){
        return new Predicate(key, value, EQUAL);
    }

    public Predicate greater(String key, String value){
        return new Predicate(key, value, GREATER);
    }

    public Predicate lesser(String key, String value){
        return new Predicate(key, value, LESS);
    }

    public Predicate(String key, String value)
    {
        this.compareOp = EQUAL;
        this.valueAsString = value;
        this.allVersionsFlag = true;
        this.value = Bytes.toBytes(value);
        this.desc = key;
        this.family = null;
        this.qualifier = null;
        this.dataType = "String";
    }
    public Predicate(String key, String value, CompareFilter.CompareOp compareOp)
    {
        this.valueAsString = value;
        this.allVersionsFlag = true;
        this.value = Bytes.toBytes(value);
        this.desc = key;
        this.family = null;
        this.qualifier = null;
        this.dataType = "String";
        this.compareOp = compareOp;
    }
    public static Predicate allVersions(String key, String value) {
        return new Predicate(key, value);
    }

}
