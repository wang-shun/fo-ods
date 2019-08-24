package foods.bigtable.repository;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by roman on 20/07/2017.
 */
public class TradesTable {

    public static final byte[] TRADES_TABLE_NAME = Bytes.toBytes("trades3");

    public static final int MAX_VERSION = 1_000_000;
    public static final int MAX_VERSION_DIGIT_COUNT = String.valueOf(MAX_VERSION - 1).length();

    public static final byte[] FAMILY_META = Bytes.toBytes("meta");
    public static final byte[] FAMILY_PARTIES = Bytes.toBytes("parties");
    public static final byte[] FAMILY_TRADE_EQUITY = Bytes.toBytes("trade_eq");
    public static final byte[] FAMILY_LINKAGES = Bytes.toBytes("linkages");
    public static final byte[] FAMILY_EVENTS = Bytes.toBytes("events");

    // meta
    public static final byte[] COLUMN_tradeId = Bytes.toBytes("tradeId");
    public static final byte[] COLUMN_tradeVersion = Bytes.toBytes("tradeVersion");
    public static final byte[] COLUMN_lastUpdated = Bytes.toBytes("lastUpdated");
    public static final byte[] COLUMN_productType = Bytes.toBytes("productType");

    public static final byte[] COLUMN_party_type = Bytes.toBytes("type");
    public static final byte[] COLUMN_party_id = Bytes.toBytes("id");

    public static final byte[] COLUMN_instrument_id = Bytes.toBytes("instrument/id");
    public static final byte[] COLUMN_instrument_ric = Bytes.toBytes("instrument/ric");
    public static final byte[] COLUMN_instrument_isin = Bytes.toBytes("instrument/isin");
    public static final byte[] COLUMN_buySell = Bytes.toBytes("buySell");
    public static final byte[] COLUMN_quantity = Bytes.toBytes("quantity");
    public static final byte[] COLUMN_price = Bytes.toBytes("price");
    public static final byte[] COLUMN_consideration = Bytes.toBytes("consideration");
    public static final byte[] COLUMN_ccy = Bytes.toBytes("ccy");
    public static final byte[] COLUMN_settlementCcy = Bytes.toBytes("settlementCcy");
    public static final byte[] COLUMN_executionVenue = Bytes.toBytes("executionVenue");
    public static final byte[] COLUMN_book = Bytes.toBytes("book");
    public static final byte[] COLUMN_tradeDatetime = Bytes.toBytes("tradeDatetime");
    public static final byte[] COLUMN_valueDate = Bytes.toBytes("valueDate");

    public static final byte[] COLUMN_link_type = Bytes.toBytes("type");
    public static final byte[] COLUMN_link_system = Bytes.toBytes("system");
    public static final byte[] COLUMN_link_id = Bytes.toBytes("id");

    public static final byte[] COLUMN_event_type = Bytes.toBytes("type");
    public static final byte[] COLUMN_event_timestamp = Bytes.toBytes("timestamp");
    public static final byte[] COLUMN_event_userId = Bytes.toBytes("userId");
    public static final byte[] COLUMN_event_firstTrade = Bytes.toBytes("firstTrade");


    public static void createTable(Connection conn) throws Exception {
        // The admin API lets us create, manage and delete tables
        Admin admin = conn.getAdmin();
        // [END connecting_to_bigtable]

        if (admin.tableExists(TableName.valueOf(TRADES_TABLE_NAME))) {
            System.out.println("Table already exists");
            return;
        }

        // [START creating_a_table]
        // Create a table with a single column family
        HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(TRADES_TABLE_NAME));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_META));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_PARTIES));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_TRADE_EQUITY));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_LINKAGES));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_EVENTS));

        System.out.println("Creating table " + descriptor.getNameAsString());
        admin.createTable(descriptor);
        // [END creating_a_table]

    }
}
