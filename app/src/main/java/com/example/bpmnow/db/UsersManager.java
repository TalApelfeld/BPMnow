package com.example.bpmnow.db;

import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class UsersManager {
    private static volatile UsersManager instance;
    private final FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();

    private UsersManager() {}

    public static UsersManager getInstance() {
        if (instance == null) {
            synchronized (UsersManager.class) {
                if (instance == null) {
                    instance = new UsersManager();
                }
            }
        }
        return instance;
    }
//    After the form submmison of Dj/Clubber we put the 'uid' as the ID of the whole doc, so here we query by
//    the doc id and get 'DocumentSnapshot', it would be much more simple for the db,
//    insted of, if we do 'whereEqualTo("uid", uid)' then it would search by field all the docs and will return us 'querySnapshot'
//    which is an array of ''DocumentSnapshot' documents.
    public Task<DocumentSnapshot> getUserDocument(String uid) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get();
    }

    public Task<Void> saveUserProfile(String uid, Map<String, Object> userData) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(userData);
    }
}