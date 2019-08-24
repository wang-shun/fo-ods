package foods.bigtable.repository.query.predicate;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.util.Bytes;

import static foods.bigtable.repository.TradesTable.COLUMN_quantity;
import static foods.bigtable.repository.TradesTable.FAMILY_TRADE_EQUITY;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.*;

/**
 * Created by roman on 21/07/2017.
 */
public class QuantityPredicate extends PropertyPredicate {

    public QuantityPredicate(double quantity, CompareFilter.CompareOp compareOp) {
        super(FAMILY_TRADE_EQUITY, COLUMN_quantity, compareOp, Bytes.toBytes(quantity), "Quantity", "" + quantity);
    }

    public static QuantityPredicate qty(double quantity) {
        return new QuantityPredicate(quantity, EQUAL);
    }
    public static QuantityPredicate qtyGreater(double quantity) {return new QuantityPredicate(quantity, GREATER); }
    public static QuantityPredicate qtyLess(double quantity) {return new QuantityPredicate(quantity, LESS); }


}
