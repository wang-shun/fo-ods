package com.maplequad.fo.ods.tradecore.balcm.data.access;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.maplequad.fo.ods.tradecore.balcm.data.model.BulkAmendRecord;
import com.maplequad.fo.ods.tradecore.balcm.data.model.BulkAmendView;
import com.maplequad.fo.ods.tradecore.data.model.trade.Trade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BulkAmendDAO
 *
 * @author Madhav Mindhe
 * @since :   05/09/2017
 */
public class BulkAmendDAO {

    private static Logger LOGGER = LoggerFactory.getLogger(BulkAmendDAO.class);

    private DatabaseReference databaseRef1 = null;
    private DatabaseReference databaseRef2 = null;

    public BulkAmendDAO(String serviceAccountPath, String databaseURL, String bulkAmendGrid, String authId, String authValue) {

        FileInputStream serviceAccount = null;
        // Initialize the app with a custom auth variable, limiting the server's access
        Map<String, Object> auth = null;
        if (authId != null && authValue != null) {
            auth = new HashMap<String, Object>();
            auth.put(authId, authValue);
        }
        // Fetch the service account key JSON file contents
        try {
            serviceAccount = new FileInputStream(serviceAccountPath); //"service-account/fo-ods-cred.json"
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl(databaseURL) //"https://fo-ods.firebaseio.com"
                    .setDatabaseAuthVariableOverride(auth)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (FileNotFoundException fnfe) {
            LOGGER.error("ERROR : Exception occurred while trying to find database credentials.");
        } catch (IOException ioe) {
            LOGGER.error("ERROR : Exception occurred while setting credentials from certificate.");
        }
        // The app only has access to public data as defined in the Security Rules
        this.setDatabaseRef1(FirebaseDatabase.getInstance().getReference(bulkAmendGrid));
        String ref2 = bulkAmendGrid.replace("Amend","AmendDetails");
        this.setDatabaseRef2(FirebaseDatabase.getInstance().getReference(ref2));
    }

    private DatabaseReference getDatabaseRef1() {
        return databaseRef1;
    }

    private void setDatabaseRef1(DatabaseReference dbRef) {
        databaseRef1 = dbRef;
    }

    private DatabaseReference getDatabaseRef2() {
        return databaseRef2;
    }

    private void setDatabaseRef2(DatabaseReference dbRef) {
        databaseRef2 = dbRef;
    }

    public void upsert(BulkAmendView bulkAmendView, List<BulkAmendRecord> pre, List<BulkAmendRecord> post) {
        LOGGER.info("upsert entered for bulkAmendId {}.", bulkAmendView.getBulkAmendId());
        this.getDatabaseRef1().child(bulkAmendView.getBulkAmendId()).setValue(new Gson().toJson(bulkAmendView), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    LOGGER.error("Data could not be saved " + databaseError.getMessage());
                } else {
                    LOGGER.info("Data saved successfully for bulkAmendId {}", bulkAmendView.getBulkAmendId());
                    //ack the message now!
                }
            }
        });

        pre.forEach(bulkAmendRecord -> {
            this.getDatabaseRef2().child(bulkAmendView.getBulkAmendId() + "/pre/" +
                    bulkAmendRecord.getTradeId() ).setValue(new Gson().toJson(bulkAmendRecord),
                    new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        LOGGER.error("Data could not be saved " + databaseError.getMessage());
                    } else {
                        LOGGER.info("Data saved successfully for bulkAmendId {}", bulkAmendView.getBulkAmendId());
                        //ack the message now!
                    }
                }
            });
        });


        post.forEach(bulkAmendRecord -> {
            this.getDatabaseRef2().child(bulkAmendView.getBulkAmendId() + "/post/"
                    + bulkAmendRecord.getTradeId()).setValue(new Gson().toJson(bulkAmendRecord),
                    new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        LOGGER.error("Data could not be saved " + databaseError.getMessage());
                    } else {
                        LOGGER.info("Data saved successfully for bulkAmendId {}", bulkAmendView.getBulkAmendId());
                        //ack the message now!
                    }
                }
            });
        });


        LOGGER.info("upsert exited for bulkAmendId {}.", bulkAmendView.getBulkAmendId());
    }

    public void delete(String bulkAmendId) {

        this.getDatabaseRef1().child(bulkAmendId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    LOGGER.error("Data could not be deleted " + databaseError.getMessage());
                } else {
                    LOGGER.info("Data deleted successfully.");
                    //ack the message now!
                }
            }
        });
        this.getDatabaseRef2().child(bulkAmendId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    LOGGER.error("Data could not be deleted " + databaseError.getMessage());
                } else {
                    LOGGER.info("Data deleted successfully.");
                    //ack the message now!
                }
            }
        });
    }


}