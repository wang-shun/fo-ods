package com.maplequad.fo.ods.ulg.dao;


import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.bigtable.hbase.BigtableConfiguration;

/***
 * BigTableHandle -
 *
 * This class gives the handle to Bigtable database.
 *
 * @author Madhav Mindhe
 * @since :   02/08/2017
 */
public class BigTableHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigTableHandle.class);
    private static BigTableHandle instance;
    private static Connection connection = null;
    private static final String PROJECT_ID = "fo-ods";
    private static final String INSTANCE_ID = "repository-test";

    /***
     * Default constructor is made private
     */
    private BigTableHandle() {
    }

    /**
     * This constructor is used  to create an instance of BigTableHandle
     *
     * @throws Exception
     */
    public static BigTableHandle getInstance(){
        if(instance == null){
            synchronized (BigTableHandle.class) {
                if(instance == null){
                    instance = new BigTableHandle();
                    LOGGER.info("Creating database connection with {} and {}", PROJECT_ID, INSTANCE_ID);
                    instance.setConnection(BigtableConfiguration.connect(PROJECT_ID, INSTANCE_ID));
                    /*Configuration config = HBaseConfiguration.create();
                    try {
                        instance.setConnection(ConnectionFactory.createConnection(config));
                    }
                    catch(IOException ioe){
                        LOGGER.error("ERROR - Exception occured while creating connection", ioe);
                    }*/
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }
}