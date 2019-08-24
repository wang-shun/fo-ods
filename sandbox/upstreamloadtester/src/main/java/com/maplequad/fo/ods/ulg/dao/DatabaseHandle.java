package com.maplequad.fo.ods.ulg.dao;

import foods.bigtable.repository.Database;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandle.class);
    private Connection connection = null;
    private static DatabaseHandle instance;

    /**
     * This constructor is used  to create an instance of TradePersistenceService
     * for a given google project id and instance id
     *
     * @param projectId
     * @param instanceId
     * @throws Exception
     */
    public static DatabaseHandle getInstance(String projectId, String instanceId){
        if(instance == null){
            synchronized (DatabaseHandle.class) {
                if(instance == null){
                    instance = new DatabaseHandle();
                    instance.setConnection(Database.createConnection(projectId,instanceId));
                }
            }
        }
        return instance;
    }

    /***
     * Default constructor is made private
     */
    private DatabaseHandle() {
    }


    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}

