package foods.bigtable.loadtest;

import foods.bigtable.model.TradeId;
import foods.bigtable.repository.Repository;

/**
 * Created by roman on 20/07/2017.
 */
public class QueryRowkeyTester extends QueryTester.BaseQueryTesterFunc {

    @Override
    public void test(Repository repository, TradeId tradeId, int version) throws Exception {
        repository.get(tradeId, version);
    }
}



        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(versionGreater(50)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qty(210.2002807559431)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qtyGreater(5)));
        //List<EquityTrade> trades = tester.query(SimpleQuery.forAll(PricePredicate.priceGreater(BigDecimal.valueOf(500))));
        //log.info("Got {} rows back ", trades.size());
        //for (EquityTrade trade: trades) {
        //    log.info("{} = {}", trade.getIdVersion(), trade.getBook());
            //log.info(trade.getIdVersion() + " = " + trade.getQuantity());
            //log.info(trade.getIdVersion() + " = " + trade.getPrice());
            //log.info(trade.getIdVersion() + " = " + trade.getTradeVersion());
        //}

        //log.info();

        //trades = tester.query(SimpleQuery.forAll(QuantityPredicate.qtyLess(5)));
        //trades = tester.query(SimpleQuery.forAll(PricePredicate.priceLess(BigDecimal.valueOf(500))));
        //trades = tester.query(SimpleQuery.forAll(bookLess("BOOK2")));
        //trades = tester.query(SimpleQuery.forAll(versionLess(50)));
        //for (EquityTrade trade: trades) {
            //log.info(trade.getIdVersion() + " = " + trade.getPrice());
            //log.info(trade.getIdVersion() + " = " + trade.getQuantity());
            //log.info(trade.getIdVersion() + " = " + trade.getBook());
            //log.info(trade.getIdVersion() + " = " + trade.getTradeVersion());
        //}
