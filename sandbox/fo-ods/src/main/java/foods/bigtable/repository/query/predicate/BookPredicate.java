package foods.bigtable.repository.query.predicate;

import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.util.Bytes;

import static foods.bigtable.repository.TradesTable.COLUMN_book;
import static foods.bigtable.repository.TradesTable.FAMILY_TRADE_EQUITY;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.EQUAL;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.GREATER;
import static org.apache.hadoop.hbase.filter.CompareFilter.CompareOp.LESS;

/**
 * Created by roman on 21/07/2017.
 */
public class BookPredicate extends PropertyPredicate {

    public BookPredicate(String book, CompareFilter.CompareOp compareOp) {

        super(FAMILY_TRADE_EQUITY, COLUMN_book, compareOp, Bytes.toBytes(book), "Book", book);
    }

    public static BookPredicate book(String book) {
        return new BookPredicate(book, EQUAL);
    }
    public static BookPredicate bookGreater(String book) {return new BookPredicate(book, GREATER); }
    public static BookPredicate bookLess(String book) {return new BookPredicate(book, LESS); }
}
