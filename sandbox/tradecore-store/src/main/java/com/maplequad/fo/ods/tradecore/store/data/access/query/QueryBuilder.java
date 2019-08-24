package com.maplequad.fo.ods.tradecore.store.data.access.query;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement;
import com.maplequad.fo.ods.tradecore.data.model.BiTemporal;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeEvent;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeLeg;
import com.maplequad.fo.ods.tradecore.data.model.trade.TradeParty;
import com.maplequad.fo.ods.tradecore.data.model.trade.eq.Equity;
import com.maplequad.fo.ods.tradecore.data.model.trade.fi.Ird;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/***
 * QueryBuilder -
 *
 * This class is used to build the SQL queries as required for getting the data from the database.
 *
 * @author Madhav Mindhe
 * @since :   13/08/2017
 */
public class QueryBuilder {

    /**
     * List of All Columns in Trade Table
     */
    private static final String[] TRADE_COLUMNS =
            new String[]{Trade.TRADE_ID, Trade.TRADE_DATE, Trade.ASSET_CLASS, Trade.TRADE_TYPE, Trade.PRODUCT_TYPE,
                    Trade.ORIG_SYS, Trade.OS_TRADE_ID, Trade.CREATED_TS, Trade.CREATED_BY};
    /**
     * List of All Columns in TradeEvent Table
     */
    private static final String[] EVENT_COLUMNS =
            new String[]{TradeEvent.TRADE_ID, TradeEvent.VALID_TIME_FROM, TradeEvent.VALID_TIME_TO,
                    TradeEvent.TXN_TIME_FROM, TradeEvent.TXN_TIME_TO, TradeEvent.PARENT_TRADE_ID,
                    TradeEvent.EVENT_TYPE, TradeEvent.EVENT_STATUS, TradeEvent.EVENT_REF, TradeEvent.EVENT_REM,
                    TradeEvent.NO_OF_LEGS, TradeEvent.OS_VERSION, TradeEvent.OS_VERSION_STATUS, TradeEvent.ORDER_ID,
                    TradeEvent.EXCH_EXEC_ID, TradeEvent.TRADER_ID, TradeEvent.SALESMAN_ID, TradeEvent.TRADER_CNTRY,
                    TradeEvent.SALESMAN_CNTRY, TradeEvent.CREATED_BY, /*TradeEvent.ACTIVE_FLAG, TradeEvent.LOCKED_FLAG*/};

    private static Logger LOGGER = LoggerFactory.getLogger(QueryBuilder.class);

    //A private constructor to hide the implicit public one.
    private QueryBuilder() {
    }

    public static String tradeIdSubQuery(String tradeId) {
        return tradeEventsAsOf(concat(equalTo(Trade.TRADE_ID, tradeId), " AND "));
    }

    public static String bookInstrumentIdSubquery(String bookId, String instrumentId) {
        return concat(
                "SELECT DISTINCT ", uniqueTradeEventColumns(), " FROM ", Ird.ENTITY, " WHERE ",
                equalTo(Ird.BOOK, bookId), " AND ", equalTo(Ird.INSTRUMENT_ID, instrumentId) );
    }

    public static String osTradeIdSubQuery(String osTradeId) {
        return concat(
                "SELECT ", list(Trade.TRADE_ID, max(BiTemporal.VALID_TIME_FROM), max(BiTemporal.TXN_TIME_FROM)),
                " FROM ", TradeEvent.ENTITY,
                " INNER JOIN ", Trade.ENTITY, " USING (", Trade.TRADE_ID, ") ",
                " WHERE ", equalTo(Trade.OS_TRADE_ID, osTradeId),
                " AND ", bitemporalCriteria(),
                " GROUP BY ", Trade.TRADE_ID);
    }

    public static String instrumentIdSubQuery(String instrumentId) {
        return concat(
                "SELECT DISTINCT ", uniqueTradeEventColumns(), " FROM ", Equity.ENTITY, " WHERE ",
                equalTo(Ird.INSTRUMENT_ID, instrumentId) );
    }

    public static Statement queryTradesAndEventAsOf(String subQuery, Timestamp validFrom, Timestamp ttf) {
        LOGGER.info("validFrom & ttf {} {}", validFrom, ttf);
        return statementBuilder(
                "SELECT ",
                tableColumnsWithPrefix(TRADE_COLUMNS, Trade.ENTITY), ",",
                tableColumnsWithPrefix(EVENT_COLUMNS, TradeEvent.ENTITY),
                " FROM (", subQuery, ")",
                " INNER JOIN ",
                " (", tradeEventsAsOf(), ")",
                " USING (",uniqueTradeEventColumns(),")",
                " INNER JOIN ", Trade.ENTITY,
                " USING (", Trade.TRADE_ID, ")",
                " INNER JOIN ", TradeEvent.ENTITY,
                " USING (",uniqueTradeEventColumns(),")").
                bind(TradeEvent.VALID_TIME_FROM).to(validFrom).
                bind(TradeEvent.TXN_TIME_FROM).to(ttf).
                build();
    }

    public static Statement queryTradePartiesAsOf(String subQuery, Timestamp validFrom, Timestamp ttf) {
        return statementBuilder(
                "SELECT * ",
                " FROM (", subQuery, ")",
                " INNER JOIN ",
                " (", tradeEventsAsOf(), ")",
                " USING (",uniqueTradeEventColumns(),")",
                " INNER JOIN ", TradeParty.ENTITY,
                " USING (", uniqueTradeEventColumns(),")").
                bind(TradeParty.VALID_TIME_FROM).to(validFrom).
                bind(TradeParty.TXN_TIME_FROM).to(ttf).
                build();
    }

    public static Statement queryTradeLegsAsOf(String legEntity, String subQuery, Timestamp validFrom, Timestamp ttf) {
        return statementBuilder(
                "SELECT * ",
                " FROM (", subQuery, ")",
                " INNER JOIN ",
                " (", tradeEventsAsOf(), ")",
                " USING (",uniqueTradeEventColumns(),")",
                " INNER JOIN ", legEntity,
                " USING (", uniqueTradeEventColumns(),")").
                bind(TradeParty.VALID_TIME_FROM).to(validFrom).
                bind(TradeParty.TXN_TIME_FROM).to(ttf).
                build();
    }
    private static String tableColumnsWithPrefix(String[] columns, String prefix) {
        return stream(columns).
                map(x->concat(prefix, ".", x, " as ", prefix, x)).
                collect(joining(","));
    }

    private static String tradeEventsAsOf() {
        return tradeEventsAsOf("");
    }

    private static String tradeEventsAsOf(String clause) {
        return concat(
                "SELECT ", list(Trade.TRADE_ID, max(BiTemporal.VALID_TIME_FROM), max(BiTemporal.TXN_TIME_FROM)),
                " FROM ", TradeEvent.ENTITY, " WHERE ", clause, bitemporalCriteria(),
                " GROUP BY ", Trade.TRADE_ID);
    }

    private static String uniqueTradeEventColumns() {
        return list(Trade.TRADE_ID, BiTemporal.VALID_TIME_FROM, BiTemporal.TXN_TIME_FROM);
    }

    private static String max(String column) {
        return concat("max(", column, ") as ", column);
    }

    private static String bitemporalCriteria() {
        return concat(TradeLeg.VALID_TIME_FROM, " <= @", TradeLeg.VALID_TIME_FROM,
                " AND ",
                TradeLeg.TXN_TIME_FROM, " <= @", TradeLeg.TXN_TIME_FROM);
    }

    private static String forceIndex(String index) {
        return concat("@{FORCE_INDEX=",index,"}");
    }

    private static Statement.Builder statementBuilder(String ...statementParts) {
        String sql = concat(statementParts);
        LOGGER.info("SQL " + sql);
        return Statement.newBuilder(sql);
    }

    private static String equalTo(String column, String value) {
        return concat(column, " = '", value, "'");
    }

    private static String concat(String ...strings) {
        return join("", strings);
    }

    private static String list(String ...strings) {
        return join(", ", strings);
    }
}
