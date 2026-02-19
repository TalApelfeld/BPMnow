package com.example.bpmnow.db;

import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class DjLikesManager {
    private static volatile DjLikesManager instance;
    private final FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();

    private DjLikesManager() {}

    public static DjLikesManager getInstance() {
        if (instance == null) {
            synchronized (DjLikesManager.class) {
                if (instance == null) {
                    instance = new DjLikesManager();
                }
            }
        }
        return instance;
    }

    public static String buildLikeDocId(String clubberId, String djId) {
        return clubberId + "_" + djId;
    }

    public Task<DocumentSnapshot> checkIfLiked(String clubberId, String djId) {
        return db.collection(Constants.COLLECTION_DJ_LIKES)
                .document(buildLikeDocId(clubberId, djId))
                .get();
    }

    public Task<Void> likeDj(String clubberId, String djId, Map<String, Object> likeData) {
        return db.collection(Constants.COLLECTION_DJ_LIKES)
                .document(buildLikeDocId(clubberId, djId))
                .set(likeData);
    }

    public Task<Void> unlikeDj(String clubberId, String djId) {
        return db.collection(Constants.COLLECTION_DJ_LIKES)
                .document(buildLikeDocId(clubberId, djId))
                .delete();
    }

    public Task<QuerySnapshot> getLikesByClubber(String clubberId) {
        return db.collection(Constants.COLLECTION_DJ_LIKES)
                .whereEqualTo("clubberId", clubberId)
                .get();
    }
}