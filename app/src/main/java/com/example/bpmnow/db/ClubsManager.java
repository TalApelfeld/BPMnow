package com.example.bpmnow.db;

import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ClubsManager {
    private static volatile ClubsManager instance;
    private final FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();

    private ClubsManager() {}

    public static ClubsManager getInstance() {
        if (instance == null) {
            synchronized (ClubsManager.class) {
                if (instance == null) {
                    instance = new ClubsManager();
                }
            }
        }
        return instance;
    }

    public Task<DocumentSnapshot> getClubByName(String clubName) {
        return db.collection(Constants.COLLECTION_CLUBS)
                .document(clubName)
                .get();
    }

    public Task<Void> addDjToClub(String clubName, Map<String, Object> djEntry) {
        return db.collection(Constants.COLLECTION_CLUBS)
                .document(clubName)
                .update("djs", com.google.firebase.firestore.FieldValue.arrayUnion(djEntry));
    }

    public Task<Void> createClub(String clubName, Map<String, Object> clubData) {
        return db.collection(Constants.COLLECTION_CLUBS)
                .document(clubName)
                .set(clubData);
    }
}