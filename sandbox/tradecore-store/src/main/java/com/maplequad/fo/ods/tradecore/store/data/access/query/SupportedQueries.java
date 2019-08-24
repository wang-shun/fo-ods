package com.maplequad.fo.ods.tradecore.store.data.access.query;

import com.maplequad.fo.ods.tradecore.proto.model.Tradequery;
import com.maplequad.fo.ods.tradecore.proto.model.Tradequery.TradeQuery;

public enum SupportedQueries {
    BYTRADEID {
        String subQueryImpl(TradeQuery query) {
            return QueryBuilder.tradeIdSubQuery(query.getByTradeId());
        }
    },
    BYOSTRADEID {
        String subQueryImpl(TradeQuery query) {
            return QueryBuilder.osTradeIdSubQuery(query.getByOsTradeId());
        }
    },
    BYINSTRUMENTID {
        String subQueryImpl(TradeQuery query) {
            return QueryBuilder.instrumentIdSubQuery(query.getByInstrumentId());
        }
    },
    BYBOOKINSTRUMENTID {
        String subQueryImpl(TradeQuery query) {
            Tradequery.QueryByBookAndInstrumentId byBookInstrumentId = query.getByBookInstrumentId();
            return QueryBuilder.bookInstrumentIdSubquery(
                    byBookInstrumentId.getBookId(),
                    byBookInstrumentId.getInstrumentId());

        }
    },
    QUERYTYPE_NOT_SET {
        String subQueryImpl(TradeQuery query) {
            throw new IllegalArgumentException("Invalid query " + query);
        }
    };

    abstract String subQueryImpl(TradeQuery query);

    public static String subQuery(TradeQuery query) {
        return valueOf(query.getQueryTypeCase().name()).subQueryImpl(query);
    }
}
