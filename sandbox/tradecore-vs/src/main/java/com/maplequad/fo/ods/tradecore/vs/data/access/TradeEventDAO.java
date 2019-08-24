package com.maplequad.fo.ods.tradecore.vs.data.access;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.maplequad.fo.ods.tradecore.vs.data.model.TradeEventView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TradeEventDAO {

    private static Logger LOGGER = LoggerFactory.getLogger(TradeEventDAO.class);

    private DatabaseReference databaseRef = null;

    public TradeEventDAO(String serviceAccountPath, String databaseURL, String tradeGrid, String authId, String authValue) {

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
        this.setDatabaseRef(FirebaseDatabase.getInstance().getReference(tradeGrid)); //"/tradeGrid" }
    }

    private DatabaseReference getDatabaseRef() {
        return databaseRef;
    }

    private void setDatabaseRef(DatabaseReference dbRef) {
        databaseRef = dbRef;
    }

    public void upsert(TradeEventView tradeEventView) {
        LOGGER.info("upsert entered for tradeId {}.", tradeEventView.getTradeId());
        this.getDatabaseRef().child(tradeEventView.getTradeId()).setValue
                (new Gson().toJson(tradeEventView), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    LOGGER.error("Data could not be saved " + databaseError.getMessage());
                } else {
                    LOGGER.info("Data saved successfully for tradeId {}", tradeEventView.getTradeId());
                    //ack the message now!
                }
            }
        });
        LOGGER.info("upsert exited for tradeId {}.", tradeEventView.getTradeId());
    }

    public void delete(String tradeId) {

        this.getDatabaseRef().push().child(tradeId).removeValue(new DatabaseReference.CompletionListener() {
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