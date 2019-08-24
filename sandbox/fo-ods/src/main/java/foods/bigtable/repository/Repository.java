package foods.bigtable.repository;

import foods.bigtable.model.EquityTrade;
import foods.bigtable.model.TradeId;
import foods.bigtable.repository.query.Query;

import java.util.List;

/**
 * Created by roman on 20/07/2017.
 */
public interface Repository {
    EquityTrade getLatest(TradeId id) throws Exception;
    List<EquityTrade> getAllVersions(TradeId id) throws Exception;
    EquityTrade get(TradeId id, int version) throws Exception;
    List<EquityTrade> find(Query query) throws Exception;
    void put(EquityTrade trade) throws Exception;
}
