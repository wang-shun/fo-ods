package com.maplequad.fo.ods.tradecore.store.data.access.rowmapper;

import com.google.cloud.spanner.ResultSet;
import com.maplequad.fo.ods.tradecore.data.model.trade.ITradeLeg;

import java.util.Collection;

import static java.util.Arrays.asList;

public interface TradeLegMapper {
    ITradeLeg readLeg(ResultSet resultSet);
    String databaseEntity();

    Collection<TradeLegMapper> ALL_LEG_MAPPERS = asList(
            EquityTradeRowMapper.INSTANCE,
            IrdTradeRowMapper.INSTANCE,
            FxdTradeRowMapper.INSTANCE
    );

}
