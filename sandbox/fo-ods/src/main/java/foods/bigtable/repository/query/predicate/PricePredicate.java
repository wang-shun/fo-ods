package foods.bigtable.repository.query.predicate;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;

import static foods.bigtable.repository.TradesTable.*;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.*;

/**
 * Created by roman on 21/07/2017.
 */
public class PricePredicate extends PropertyPredicate {

    public PricePredicate(BigDecimal decimal, CompareFilter.CompareOp compareOp) {

        super(FAMILY_TRADE_EQUITY, COLUMN_price, compareOp, Bytes.toBytes(decimal), "Price", decimal.toString());
    }

    public static PricePredicate price(BigDecimal decimal) {
        return new PricePredicate(decimal, EQUAL);
    }
    public static PricePredicate priceGreater(BigDecimal decimal) {return new PricePredicate(decimal, GREATER); }
    public static PricePredicate priceLess(BigDecimal decimal) {return new PricePredicate(decimal, LESS); }
}
