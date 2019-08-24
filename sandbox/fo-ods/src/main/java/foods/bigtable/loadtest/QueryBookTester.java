package foods.bigtable.loadtest;

import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Repository;
import foods.bigtable.repository.TradesTable;
import foods.bigtable.repository.query.Query;
import foods.bigtable.repository.query.SimpleQuery;
import foods.bigtable.repository.query.predicate.PropertyPredicate;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;

import static foods.bigtable.repository.query.predicate.BookPredicate.book;
import static foods.bigtable.repository.query.predicate.VersionPredicate.versionGreater;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryBookTester extends QueryTester.BaseQueryTesterFunc {

  private Query query;

  @Override
  protected void init() {
    int maxRows = Integer.valueOf(args[4]);
    query = SimpleQuery.forAll(maxRows, book("BOOK2"), versionGreater(98),
        new PropertyPredicate(TradesTable.FAMILY_TRADE_EQUITY, TradesTable.COLUMN_instrument_id,
            CompareFilter.CompareOp.GREATER, Bytes.toBytes(9223372036850000000L),
            "222", "333"));
  }

  @Override
    public void test(Repository repository, TradeId tradeId, int version) throws Exception {
      List<EquityTrade> trades = repository.find(query);
      log.info("Found {} trades", trades.size());
    }
}
