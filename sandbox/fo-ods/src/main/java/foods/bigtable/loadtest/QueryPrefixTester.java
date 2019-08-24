package foods.bigtable.loadtest;

import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Repository;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryPrefixTester extends QueryTester.BaseQueryTesterFunc {

    @Override
    public void test(Repository repository, TradeId tradeId, int version) throws Exception {
            repository.getLatest(tradeId);

    }
}
