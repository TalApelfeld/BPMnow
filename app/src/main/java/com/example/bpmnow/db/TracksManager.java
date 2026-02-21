package com.example.bpmnow.db;

import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class TracksManager {
    private static volatile TracksManager instance;
    private final FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();

    private TracksManager() {}

    public static TracksManager getInstance() {
        if (instance == null) {
            synchronized (TracksManager.class) {
                if (instance == null) {
                    instance = new TracksManager();
                }
            }
        }
        return instance;
    }

    public Task<QuerySnapshot> getPlaylistTracks(String djId, String playlistId) {
        return db.collection(Constants.COLLECTION_TRACKS)
                .whereEqualTo("uid", djId)
                .whereEqualTo("idPlaylist", playlistId)
                .limit(20)
                .get();
    }

    public Task<QuerySnapshot> findTrackByIdAndDj(String trackId, String djId) {
        return db.collection(Constants.COLLECTION_TRACKS)
                .whereEqualTo("id", trackId)
                .whereEqualTo("uid", djId)
                .get();
    }

    public Task<QuerySnapshot> findTrackById(String trackId) {
        return db.collection(Constants.COLLECTION_TRACKS)
                .whereEqualTo("id", trackId)
                .get();
    }

    public Task<Void> incrementRequests(DocumentReference trackRef, int delta) {
        return trackRef.update("requests", FieldValue.increment(delta));
    }

    public Task<Void> addRequestedBy(DocumentReference trackRef, String uid) {
        return trackRef.update("requestedBy", FieldValue.arrayUnion(uid));
    }

    public Task<Void> removeRequestedBy(DocumentReference trackRef, String uid) {
        return trackRef.update("requestedBy", FieldValue.arrayRemove(uid));
    }

    public Task<Void> addReview(DocumentReference trackRef, String comment) {
        return trackRef.update("reviews", FieldValue.arrayUnion(comment));
    }

    public Task<QuerySnapshot> getRequestedTracks(String djId) {
        return db.collection(Constants.COLLECTION_TRACKS)
                .whereEqualTo("uid", djId)
                .whereGreaterThan("requests", 0)
                .orderBy("requests", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getTopRequestedTracks(String djId, int limit) {
        return db.collection(Constants.COLLECTION_TRACKS)
                .whereEqualTo("uid", djId)
                .orderBy("requests", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    public Task<Void> batchSaveTracks(List<Map<String, Object>> trackMaps) {
        WriteBatch batch = db.batch();
        for (Map<String, Object> trackMap : trackMaps) {
            DocumentReference docRef = db.collection(Constants.COLLECTION_TRACKS).document();
            batch.set(docRef, trackMap);
        }
        return batch.commit();
    }
}
