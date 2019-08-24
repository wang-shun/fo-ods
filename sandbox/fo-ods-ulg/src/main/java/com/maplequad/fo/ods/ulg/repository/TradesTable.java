package com.maplequad.fo.ods.ulg.repository;

import com.maplequad.fo.ods.ulg.dao.BigTableHandle;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roman on 20/07/2017.
 */
public class TradesTable {

    public static final int MAX_VERSION = 1_000_000;
    public static final byte[] FAMILY_HEADERS = Bytes.toBytes("headers");
    public static final byte[] FAMILY_PRODUCT = Bytes.toBytes("product");
    public static final byte[] FAMILY_EVENT = Bytes.toBytes("event");
    public static final byte[] FAMILY_PARTIES = Bytes.toBytes("party");
    public static final byte[] FAMILY_CLEARING = Bytes.toBytes("clearing");
    public static final byte[] FAMILY_REGULATORY = Bytes.toBytes("regulatory");
    public static final byte[] FAMILY_CASHFLOW = Bytes.toBytes("cashflow");
    private static Logger LOGGER = LoggerFactory.getLogger(TradesTable.class);

    public static void createTable(String tableName) throws Exception {
        LOGGER.trace("Entered createTable");
        // The admin API lets us create, manage and delete tables
        Admin admin = BigTableHandle.getInstance().getConnection().getAdmin();
        // [END connecting_to_bigtable]

        if (admin.tableExists(TableName.valueOf(Bytes.toBytes(tableName)))) {
            LOGGER.info("Table already exists");
        }
        else {
            // [START creating_a_table]
            // Create a table with all required column families
            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(Bytes.toBytes(tableName)));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_HEADERS));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_PRODUCT));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_EVENT));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_PARTIES));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_CLEARING));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_REGULATORY));
            descriptor.addFamily(new HColumnDescriptor(FAMILY_CASHFLOW));
            LOGGER.info("Creating table " + descriptor.getNameAsString());
            admin.createTable(descriptor);
            // [END creating_a_table]
        }
        LOGGER.trace("Exited createTable");
    }
}
