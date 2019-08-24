package foods.bigtable.repository.query.predicate;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.util.Bytes;

import static foods.bigtable.repository.TradesTable.*;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.*;

/**
 * Created by roman on 21/07/2017.
 */
public class VersionPredicate extends PropertyPredicate {

    public VersionPredicate(int version, CompareFilter.CompareOp compareOp) {
        super(FAMILY_META, COLUMN_tradeVersion, compareOp, Bytes.toBytes(version), "Version", "" + version);
    }

    public static VersionPredicate version(int version) {
        return new VersionPredicate(version, EQUAL);
    }
    public static VersionPredicate versionGreater(int version) {return new VersionPredicate(version, GREATER); }
    public static VersionPredicate versionLess(int version) {return new VersionPredicate(version, LESS); }


}
