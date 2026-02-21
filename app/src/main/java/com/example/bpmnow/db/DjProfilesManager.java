package com.example.bpmnow.db;

import com.example.bpmnow.models.clubber.DjCardItem;
import com.example.bpmnow.models.dj.Dj;
import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DjProfilesManager {
    private static volatile DjProfilesManager instance;
    private final FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();

    // Cached DJ list populated by getPopularDJs(), used by search methods in 'search' fragment of clubber
    private List<DjCardItem> cachedDjCardItmes = new ArrayList<>();

    private DjProfilesManager() {
    }

    public static DjProfilesManager getInstance() {
        if (instance == null) {
            synchronized (DjProfilesManager.class) {
                if (instance == null) {
                    instance = new DjProfilesManager();
                }
            }
        }
        return instance;
    }

    public List<DjCardItem> getCachedDjs() {
        return cachedDjCardItmes;
    }

    public void setCachedDjs(List<DjCardItem> cachedDjs) {
        this.cachedDjCardItmes = cachedDjs;
    }

    public Task<QuerySnapshot> getPopularDJs(int limit) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .orderBy("totalRequests", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

//      Search cached DJs by stage name (case-insensitive contains).
    public List<DjCardItem> searchByName(String query) {
        List<DjCardItem> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (DjCardItem dj : cachedDjCardItmes) {
            if (dj.getStageName() != null &&
                    dj.getStageName().toLowerCase().contains(lowerQuery)) {
                results.add(dj);
            }
        }
        return results;
    }


//      Filter cached DJs by genre.
    public List<DjCardItem> searchByGenre(String genre) {
        List<DjCardItem> results = new ArrayList<>();
        for (DjCardItem dj : cachedDjCardItmes) {
            if (dj.getGenres() != null && dj.getGenres().contains(genre)) {
                results.add(dj);
            }
        }
        return results;
    }

    public Task<DocumentSnapshot> getDjProfile(String djId) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .document(djId)
                .get();
    }

    public Task<Void> saveDjProfile(String uid, Map<String, Object> djProfileData) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .document(uid)
                .set(djProfileData);
    }

    public Task<Void> incrementLikes(String djId, int delta) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .document(djId)
                .update("totalLikes", FieldValue.increment(delta));
    }

    public Task<Void> incrementRequests(String djId, int delta) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .document(djId)
                .update("totalRequests", FieldValue.increment(delta));
    }

    public Task<Void> incrementFeedback(String djId) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .document(djId)
                .update("totalFeedback", FieldValue.increment(1));
    }

    public Task<Void> savePlaylistIds(String djId, List<String> playlistIds) {
        return db.collection(Constants.COLLECTION_DJ_PROFILES)
                .document(djId)
                .update("playlistId", playlistIds);
    }
}