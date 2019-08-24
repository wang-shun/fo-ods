package com.maplequad.fo.ods.tradecore.dao;

import com.google.cloud.spanner.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.cloud.spanner.TransactionRunner.TransactionCallable;

import com.google.spanner.admin.database.v1.CreateDatabaseMetadata;

import javax.annotation.Nullable;
import java.util.*;


/***
 * SpannerHandle -
 *
 * This class gives the handle to Spanner database.
 *
 * @author Madhav Mindhe
 * @since :   09/08/2017
 */
public class SpannerHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpannerHandle.class);
    private static SpannerHandle instance;
    private static DatabaseClient dbClient = null;
    private static DatabaseAdminClient adminClient = null;
    private static DatabaseId databaseId = null;
    private static final String PROJECT_ID = "fo-ods";
    private static final String INSTANCE_ID = "tradecore";
    private static final String DATABASE = "tradecore";
    private static String [] TABLE_LIST = new String[]{"TRADE","TRADE_EVENT","TRADE_PARTY","TRADE_LEG"};

    /***
     * Default constructor is made private
     */
    private SpannerHandle() {
    }

    /**
     * This constructor is used  to create an instance of BigTableHandle
     *
     * @throws Exception
     */
    public static SpannerHandle getInstance(){
        if(instance == null){
            synchronized (SpannerHandle.class) {
                if(instance == null){
                    instance = new SpannerHandle();
                    LOGGER.info("Creating database connection with {}, {} and {}", PROJECT_ID, INSTANCE_ID, DATABASE);

                    SpannerOptions options = SpannerOptions.newBuilder().build();
                    Spanner spanner = options.getService();
                    try {
                        DatabaseId db = DatabaseId.of(options.getProjectId(), INSTANCE_ID, DATABASE);
                        // [END init_client]
                        // This will return the default project id based on the environment.
                        String clientProject = spanner.getOptions().getProjectId();
                        if (!db.getInstanceId().getProject().equals(clientProject)) {
                            System.err.println("Invalid project specified. Project in the database id should match"
                                    + "the project name set in the environment variable GCLOUD_PROJECT. Expected: "
                                    + clientProject);
                        }
                        // [START init_client]
                        DatabaseClient dbClient = spanner.getDatabaseClient(db);
                        DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
                        // [END init_client]
                        instance.setdbClient(dbClient);
                        instance.setAdminClient(dbAdminClient);
                        instance.setDatabaseId(db);

                    } finally {
                        //spanner.close();
                    }
                }
            }
        }
        return instance;
    }

    public static DatabaseAdminClient getAdminClient() {
        return adminClient;
    }

    public static void setAdminClient(DatabaseAdminClient adminClient) {
        SpannerHandle.adminClient = adminClient;
    }

    public static DatabaseId getDatabaseId() {
        return databaseId;
    }

    public static void setDatabaseId(DatabaseId databaseId) {
        SpannerHandle.databaseId = databaseId;
    }

    public static DatabaseClient getdbClient() {
        return dbClient;
    }

    private static void setdbClient(DatabaseClient newDBClient) {
        dbClient = newDBClient;
    }

    //??
       private static void createDatabase(DatabaseAdminClient dbAdminClient, DatabaseId id) {
            Operation<Database, CreateDatabaseMetadata> op = dbAdminClient
                    .createDatabase(
                            id.getInstanceId().getInstance(),
                            id.getDatabase(),
                            Arrays.asList(
                                    "CREATE TABLE Singers (\n"
                                            + "  SingerId   INT64 NOT NULL,\n"
                                            + "  FirstName  STRING(1024),\n"
                                            + "  LastName   STRING(1024),\n"
                                            + "  SingerInfo BYTES(MAX)\n"
                                            + ") PRIMARY KEY (SingerId)",
                                    "CREATE TABLE Albums (\n"
                                            + "  SingerId     INT64 NOT NULL,\n"
                                            + "  AlbumId      INT64 NOT NULL,\n"
                                            + "  AlbumTitle   STRING(MAX)\n"
                                            + ") PRIMARY KEY (SingerId, AlbumId),\n"
                                            + "  INTERLEAVE IN PARENT Singers ON DELETE CASCADE"));
            Database db = op.waitFor().getResult();
            System.out.println("Created database [" + db.getId() + "]");
        }

        // [START write]
       private static Mutation insertOrUpdateRecord(String tableName, List<Column> columnsList, boolean isInsert) {
        LOGGER.info("insertOrUpdateRecord enterted for table {} with {}", tableName, columnsList);
        //List<Mutation> mutations = new ArrayList<>();
           Mutation.WriteBuilder builder = null;

           if(isInsert) {
               builder = Mutation.newInsertBuilder(tableName);
           }
           else {
               builder = Mutation.newUpdateBuilder(tableName);
           }

           for (Column column : columnsList) {
               String name = column.getName();
               switch (column.getType()) {
                   case "int":
                       builder.set(name).to(column.getIntValue());
                       break;
                   case "long":
                       builder.set(name).to(column.getLongValue());
                       break;
                   case "float":
                       builder.set(name).to(column.getFloatValue());
                       break;
                   case "string":
                       builder.set(name).to(column.getStrValue());
                       break;
                   case "boolean":
                       builder.set(name).to(column.isBooleanValue());
                       break;
                   case "date":
                       builder.set(name).to(column.getDate());
                       break;
                   case "timestamp":
                       builder.set(name).to(column.getTimestamp());
                       break;
                   default:
                       throw new IllegalArgumentException("Unsupported column type: " + column.getType());
               }
               //mutations.add(builder.build());
           }
           LOGGER.info("insertOrUpdateRecord exited for table {}",tableName);
           return builder.build();
       }

       public static void insert(Map<String, List<List<Column>>> map){
           dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
               @Nullable
               @Override
               public Void run(TransactionContext txContext) throws Exception {
                   for(String tableName : TABLE_LIST) {
                       for (List<Column> column : map.get(tableName)) {
                           Mutation mutation = insertOrUpdateRecord(tableName, column, true);
                           txContext.buffer(mutation);
                       }
                   }
                   return null;
               }
           });
       }


        public static void insert(String tableName, List<Column> columns) {

            dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
                @Nullable
                @Override
                public Void run(TransactionContext txContext) throws Exception {
                    Mutation mutation = insertOrUpdateRecord(tableName, columns, true);
                    txContext.buffer(mutation);
                    return null;
                }
            });
        }

        public static void update(String tableName, List<Column> columns) {
            dbClient.readWriteTransaction().run(new TransactionRunner.TransactionCallable<Void>() {
                @Nullable
                @Override
                public Void run(TransactionContext txContext) throws Exception {
                    Mutation mutation = insertOrUpdateRecord(tableName, columns, false);
                    txContext.buffer(mutation);
                    return null;
                }
            });
        }

        // [END update]
        // [END write]

        // [START query]
        //resultSet.isNull("Column_Name") ? "NULL" : resultSet.getLong("Column_Name")); ??
    public static List<List<Column>> query(String query, List<Column> columns) {
            // singleUse() can be used to execute a single read or query against Cloud Spanner.
            ResultSet resultSet = dbClient.singleUse().executeQuery(Statement.of(query));
            List<List<Column>> result = new ArrayList<>();
            while (resultSet.next()) {
                int cCount = 0;
                for(Column column : columns){
                    String name = column.getName();
                    switch (column.getType()) {
                        case "int":
                            column.setIntValue((int)resultSet.getLong(cCount));
                            break;
                        case "long":
                            column.setLongValue(resultSet.getLong(cCount));
                            break;
                        case "float":
                            column.setFloatValue((float)resultSet.getDouble(cCount));
                            break;
                        case "string":
                            column.setStrValue(resultSet.getString(cCount));
                            break;
                        case "boolean":
                            column.setBooleanValue(resultSet.getBoolean(cCount));
                            break;
                        case "date":
                            column.setDate(resultSet.getDate(cCount));
                            break;
                        case "timestamp":
                            column.setTimestamp(resultSet.getTimestamp(cCount));
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported column type: " + column.getType());
                    }
                    cCount++;
                }
                result.add(columns);
            }
            return result;
        }
        // [END query]


        // [START read]
        public static List<List<Column>> read(String tableName, String... columns) {
            return readUsingIndex(tableName, null, columns);
        }

        public static List<List<Column>> readUsingIndex(String tableName, String index, String... columns) {
            ResultSet resultSet = null;

            if(index == null) {
                resultSet = dbClient.singleUse().read(tableName, KeySet.all(), Arrays.asList(columns));
            }else{
                resultSet = dbClient.singleUse().readUsingIndex(tableName, index, KeySet.all(), Arrays.asList(columns));
            }

            List<List<Column>> result = new ArrayList<>();
            while (resultSet.next()) {
                int cCount = 0;
                List<Column> columnsList = new ArrayList<>();
                for (String col : columns) {
                    columnsList.add(new Column(col, resultSet.getString(cCount)));
                }
                result.add(columnsList);
            }
            return result;
       }
        // [END read]

        public static void addColumn(String tableName, String column, String type) {
            adminClient.updateDatabaseDdl(getDatabaseId().getInstanceId().getInstance(),
                    getDatabaseId().getDatabase(),
                    Arrays.asList("ALTER TABLE "+tableName+" ADD COLUMN "+column+" "+type),
                    null).waitFor();
            LOGGER.info("Added column {} to table {}", column, tableName);
        }

        // [START add_index] ??
        private static void addIndex(String indexStatement) {
            adminClient.updateDatabaseDdl(getDatabaseId().getInstanceId().getInstance(), getDatabaseId().getDatabase(),
                    Arrays.asList(indexStatement), null).waitFor();
            LOGGER.info("Added index {} ",indexStatement);
        }
        // [END add_index]


        // [START read_only_transaction]
       private static void readOnlyTransaction(DatabaseClient dbClient) {
            // ReadOnlyTransaction must be closed by calling close() on it to release resources held by it.
            // We use a try-with-resource block to automatically do so.
            try (ReadOnlyTransaction transaction = dbClient.readOnlyTransaction()) {
                ResultSet queryResultSet =
                        transaction.executeQuery(
                                Statement.of("SELECT SingerId, AlbumId, AlbumTitle FROM Albums"));
                while (queryResultSet.next()) {
                    System.out.printf(
                            "%d %d %s\n",
                            queryResultSet.getLong(0), queryResultSet.getLong(1), queryResultSet.getString(2));
                }
                ResultSet readResultSet =
                        transaction.read(
                                "Albums", KeySet.all(), Arrays.asList("SingerId", "AlbumId", "AlbumTitle"));
                while (readResultSet.next()) {
                    System.out.printf(
                            "%d %d %s\n",
                            readResultSet.getLong(0), readResultSet.getLong(1), readResultSet.getString(2));
                }
            }
        }
        // [END read_only_transaction]

    // [START transaction] ??
    private static void writeWithTransaction(DatabaseClient dbClient) {
        dbClient
                .readWriteTransaction()
                .run(
                        new TransactionCallable<Void>() {
                            @Override
                            public Void run(TransactionContext transaction) throws Exception {
                                // Transfer marketing budget from one album to another. We do it in a transaction to
                                // ensure that the transfer is atomic.
                                Struct row =
                                        transaction.readRow("Albums", Key.of(2, 2), Arrays.asList("MarketingBudget"));
                                long album2Budget = row.getLong(0);
                                // Transaction will only be committed if this condition still holds at the time of
                                // commit. Otherwise it will be aborted and the callable will be rerun by the
                                // client library.
                                if (album2Budget >= 300000) {
                                    long album1Budget =
                                            transaction
                                                    .readRow("Albums", Key.of(1, 1), Arrays.asList("MarketingBudget"))
                                                    .getLong(0);
                                    long transfer = 200000;
                                    album1Budget += transfer;
                                    album2Budget -= transfer;
                                    transaction.buffer(
                                            Mutation.newUpdateBuilder("Albums")
                                                    .set("SingerId")
                                                    .to(1)
                                                    .set("AlbumId")
                                                    .to(1)
                                                    .set("MarketingBudget")
                                                    .to(album1Budget)
                                                    .build());
                                    transaction.buffer(
                                            Mutation.newUpdateBuilder("Albums")
                                                    .set("SingerId")
                                                    .to(2)
                                                    .set("AlbumId")
                                                    .to(2)
                                                    .set("MarketingBudget")
                                                    .to(album2Budget)
                                                    .build());
                                }
                                return null;
                            }
                        });
    }
    // [END transaction]

        public static void main(String[] args) throws Exception {

            // [START init_client]
            SpannerOptions options = SpannerOptions.newBuilder().build();
            Spanner spanner = options.getService();
            try {
                String command = args[0];
                DatabaseId db = DatabaseId.of(options.getProjectId(), args[1], args[2]);
                // [END init_client]
                // This will return the default project id based on the environment.
                String clientProject = spanner.getOptions().getProjectId();
                if (!db.getInstanceId().getProject().equals(clientProject)) {
                    System.err.println("Invalid project specified. Project in the database id should match"
                            + "the project name set in the environment variable GCLOUD_PROJECT. Expected: "
                            + clientProject);
                }
                // [START init_client]
                DatabaseClient dbClient = spanner.getDatabaseClient(db);
                DatabaseAdminClient dbAdminClient = spanner.getDatabaseAdminClient();
                // [END init_client]
                //run(dbClient, dbAdminClient, command, db);
            } finally {
                spanner.close();
            }
            System.out.println("Closed client");
        }
    }
