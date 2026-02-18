package com.example.bpmnow.network;

import com.google.firebase.auth.FirebaseAuth;


public class FirebaseAuthConnection {
    //    volatile ensures the fully constructed instance is visible across all threads
    private static volatile FirebaseAuthConnection instance;
    private FirebaseAuth mAuth;
    private String userRole;

    private FirebaseAuthConnection() {
        mAuth = FirebaseAuth.getInstance();
    }

    //    The outer if avoids the expensive synchronized block on every call after initialization.
    //    The inner if handles the race condition where two threads both passed the outer check simultaneously
    public static FirebaseAuthConnection getInstance() {
        if (instance == null) {
            synchronized (FirebaseAuthConnection.class) {
                if (instance == null) {
                    instance = new FirebaseAuthConnection();
                }
            }
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public Boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    public String getUserEmail(){
        return mAuth.getCurrentUser().getEmail();
    }

    public void logout() {
        mAuth.signOut();
    }


}
