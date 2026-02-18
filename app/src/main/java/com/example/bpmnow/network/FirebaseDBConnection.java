package com.example.bpmnow.network;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDBConnection {
    //    volatile ensures the fully constructed instance is visible across all threads
    private static volatile FirebaseDBConnection instance;
    private FirebaseFirestore db;

    private FirebaseDBConnection() {
        db = FirebaseFirestore.getInstance();
    }

    //    The outer if avoids the expensive synchronized block on every call after initialization.
    //    The inner if handles the race condition where two threads both passed the outer check simultaneously
    public static FirebaseDBConnection getInstance() {
        if (instance == null) {
            synchronized (FirebaseDBConnection.class) {
                if (instance == null) {
                    instance = new FirebaseDBConnection();
                }
            }
        }
        return instance;
    }

    public FirebaseFirestore getDB() {
        return db;
    }
}
