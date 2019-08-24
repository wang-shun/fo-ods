package com.maplequad.fo.ods.tradecore.store.data.access.service;

import com.google.cloud.spanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/***
 * SpannerDatabaseService -
 *
 * This class gives the handle to Spanner database.
 *
 * @author Madhav Mindhe
 * @since :   11/08/2017
 */
public class SpannerDatabaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpannerDatabaseService.class);
    private static SpannerDatabaseService instance;
    private static DatabaseClient dBClient = null;
    private static DatabaseAdminClient dbAdminClient = null;
    private static DatabaseId databaseId = null;

    /***
     * Default constructor is made private
     */
    private SpannerDatabaseService() {
    }

    /**
     * This constructor is used  to create an instance of BigTableHandle
     *
     * @throws Exception
     */
    public static SpannerDatabaseService getInstance() {
        if (instance == null) {
            instance = new SpannerDatabaseService();
            String instanceId = System.getenv("INSTANCE_ID");
            String database = System.getenv("DATABASE");
            //Using values from test instance if not set as ENV Variables
            if (instanceId == null || database == null) {
                instanceId = "tradecore";
                database = "tradecore";
            }

            LOGGER.info("Creating database connection with {} and {}", instanceId, database);

            SpannerOptions options = SpannerOptions.newBuilder().build();
            Spanner spanner = options.getService();
            try {
                databaseId = DatabaseId.of(options.getProjectId(), instanceId, database);
                // [END init_client]
                // This will return the default project id based on the environment.
                String clientProject = spanner.getOptions().getProjectId();
                if (!databaseId.getInstanceId().getProject().equals(clientProject)) {
                    LOGGER.error("Invalid project specified. Expected: {}", clientProject);
                }
                // [START init_client]

                // FIXME: maybe dbCLient, dbAdminClient, databaseId should be non-static?
                // otherwise, what's the point for creating an instance of this class?
                dBClient = spanner.getDatabaseClient(databaseId);
                dbAdminClient = spanner.getDatabaseAdminClient();
                // [END init_client]
                instance.setDatabaseId(databaseId);
            } catch (Exception exception) {
                LOGGER.error("ERROR : Exception occured while connecting to Spanner Database - {}", exception);
            }
        }
        return instance;
    }

    /**
     * @return
     */
    public static DatabaseAdminClient getdbAdminClient() {
        return dbAdminClient;
    }

    /**
     * @param dbAdminClient
     */
    private static void setAdminClient(DatabaseAdminClient dbAdminClient) {
        SpannerDatabaseService.dbAdminClient = dbAdminClient;
    }

    /**
     * @return
     */
    public static DatabaseId getDatabaseId() {
        return databaseId;
    }

    /**
     * @param databaseId
     */
    private static void setDatabaseId(DatabaseId databaseId) {
        SpannerDatabaseService.databaseId = databaseId;
    }

    /**
     * @return
     */
    public static DatabaseClient getDBClient() {
        return dBClient;
    }

    private static void setDBClient(DatabaseClient newDBClient) {
        dBClient = newDBClient;
    }
}
