package com.maplequad.fo.ods.ulg.repository;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roman on 20/07/2017.
 */
public class TradesTable {
	static Logger logger = LoggerFactory.getLogger(TradesTable.class);
    //public static final byte[] TRADES_TABLE_NAME = Bytes.toBytes("trades");

    public static final int MAX_VERSION = 1_000_000;

    public static final byte[] FAMILY_META = Bytes.toBytes("headers");
    public static final byte[] FAMILY_PARTIES = Bytes.toBytes("party");
    public static final byte[] FAMILY_PRODUCT = Bytes.toBytes("product");
    public static final byte[] FAMILY_LINKAGES = Bytes.toBytes("linkages");
    public static final byte[] FAMILY_EVENTS = Bytes.toBytes("event");
    public static final byte[] FAMILY_regulatory = Bytes.toBytes("regulatory");
    
    
    /*
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

    public static final byte[] COLUMN_instrument_id = Bytes.toBytes("instrumentid");
    public static final byte[] COLUMN_instrument_ric = Bytes.toBytes("instrumentric");
    public static final byte[] COLUMN_instrument_isin = Bytes.toBytes("instrumentisin");
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
*/

    public static void createTable(Connection conn, byte[] myTable) throws Exception {
        // The admin API lets us create, manage and delete tables
        Admin admin = conn.getAdmin();
        // [END connecting_to_bigtable]

        if (admin.tableExists(TableName.valueOf(myTable))) {
            System.out.println("Table already exists");
            return;
        }

        // [START creating_a_table]
        // Create a table with a single column family
        HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(myTable));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_META));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_PARTIES));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_PRODUCT));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_LINKAGES));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_EVENTS));
        descriptor.addFamily(new HColumnDescriptor(FAMILY_regulatory));

        logger.info("Creating table " + descriptor.getNameAsString());
        admin.createTable(descriptor);
        // [END creating_a_table]

    }
}
