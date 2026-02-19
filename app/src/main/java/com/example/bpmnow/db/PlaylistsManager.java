package com.example.bpmnow.db;

import com.example.bpmnow.network.FirebaseDBConnection;
import com.example.bpmnow.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Map;

public class PlaylistsManager {
    private static volatile PlaylistsManager instance;
    private final FirebaseFirestore db = FirebaseDBConnection.getInstance().getDB();

    private PlaylistsManager() {}

    public static PlaylistsManager getInstance() {
        if (instance == null) {
            synchronized (PlaylistsManager.class) {
                if (instance == null) {
                    instance = new PlaylistsManager();
                }
            }
        }
        return instance;
    }

    public Task<QuerySnapshot> getPlaylistsByDj(String djId) {
        return db.collection(Constants.COLLECTION_DJ_PLAYLISTS)
                .whereEqualTo("uid", djId)
                .get();
    }

    public Task<Void> batchSavePlaylists(List<Map<String, Object>> playlistMaps) {
        WriteBatch batch = db.batch();
        for (Map<String, Object> playlistMap : playlistMaps) {
            DocumentReference docRef = db.collection(Constants.COLLECTION_DJ_PLAYLISTS).document();
            batch.set(docRef, playlistMap);
        }
        return batch.commit();
    }
}